package com.ssafy.frogdetox.view.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.frogdetox.data.TodoDto
import com.ssafy.frogdetox.databinding.ItemHeaderTodoBinding
import com.ssafy.frogdetox.databinding.ItemListTodoBinding
import com.ssafy.frogdetox.common.todoListSwiper.ItemTouchHelperListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TYPE_HEADER = 1
private const val TYPE_ITEM = 2

class TodoListAdapter() :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(TodoListDiffCallback()),
    ItemTouchHelperListener {

    interface TodoSwipeListener {
        fun onItemDelete(id: String)
    }

    interface TodoCompleteListener {
        fun onChecked(id: String, isChecked: Boolean)
    }

    interface TodoClickListener {
        fun onTodoClick(id: String, state: Int)
    }

    var todoClickListener: TodoClickListener? = null
    var todoSwipeListener: TodoSwipeListener? = null
    var todoCompleteListener: TodoCompleteListener? = null

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> run {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHeaderTodoBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding, todoClickListener)
            }

            TYPE_ITEM -> run {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListTodoBinding.inflate(layoutInflater, parent, false)
                TodoViewHolder(binding, todoClickListener, todoCompleteListener)
            }

            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> TYPE_HEADER
            is DataItem.TodoItem -> TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TodoViewHolder -> {
                val item = getItem(position) as DataItem.TodoItem
                holder.bind(item.item)
            }

            is HeaderViewHolder -> {
                holder.bind()
            }
        }
    }

    class TodoViewHolder(
        private val binding: ItemListTodoBinding,
        private val todoClickListener: TodoClickListener?,
        private val todoCompleteListener: TodoCompleteListener?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoDto) {
            binding.cbIsComplete.isChecked = item.complete
            binding.tvContext.text = item.content
            binding.tvTime.apply {
                if (item.isAlarm) {
                    visibility = View.VISIBLE
                    text = item.time
                } else {
                    visibility = View.GONE
                }
            }

            binding.llTodo.setOnClickListener {
                todoClickListener?.onTodoClick(item.id, TodoFragment.TODO_UPDATE)
            }

            binding.cbIsComplete.setOnCheckedChangeListener { _, isChecked ->
                todoCompleteListener?.onChecked(item.id, isChecked)
            }
        }
    }

    class HeaderViewHolder(
        private val binding: ItemHeaderTodoBinding,
        private val todoClickListener: TodoClickListener?
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.llTodoHeader.setOnClickListener {
                todoClickListener?.onTodoClick("-1", TodoFragment.TODO_INSERT)
            }
        }
    }

    fun addHeaderAndSubmitList(list: List<TodoDto>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.TodoItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun onItemMove(from_position: Int, to_position: Int): Boolean = false

    override fun onItemSwipe(position: Int) {}

    override fun onRightClick(position: Int, viewHolder: RecyclerView.ViewHolder?) {
        todoSwipeListener?.onItemDelete(currentList[position].id)
    }
}

class TodoListDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

sealed class DataItem {
    data class TodoItem(val item: TodoDto) : DataItem() {
        override val id = item.id
    }

    data object Header : DataItem() {
        override val id = Int.MIN_VALUE.toString()
    }

    abstract val id: String
}