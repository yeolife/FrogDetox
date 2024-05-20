package com.ssafy.frogdetox.view.todo

import android.view.LayoutInflater
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

class TodoListAdapter(private val clickListener: ItemClickListener) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(TodoListDiffCallback()),
    ItemTouchHelperListener {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    interface TodoSwipeListener {
        fun onItemDelete(id: String)
    }

    interface TodoCompleteListener {
        fun onChecked(id: String, isChecked: Boolean)
    }

    var todoSwipeListener: TodoSwipeListener? = null
    var todoCompleteListener: TodoCompleteListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder.from(parent)
            TYPE_ITEM -> run {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListTodoBinding.inflate(layoutInflater, parent, false)
                TodoViewHolder(binding, todoCompleteListener)
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
                holder.bind(item.item, clickListener)
            }

            is HeaderViewHolder -> {
                holder.bind(clickListener)
            }
        }
    }

    class TodoViewHolder(
        private val binding: ItemListTodoBinding,
        private val todoCompleteListener: TodoCompleteListener?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TodoDto, clickListener: ItemClickListener) {
            binding.todo = item
            binding.clickListener = clickListener

            binding.cbIsComplete.setOnCheckedChangeListener { _, isChecked ->
                todoCompleteListener?.onChecked(item.id, isChecked)
            }
        }
    }

    class HeaderViewHolder private constructor(private val binding: ItemHeaderTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: ItemClickListener) {
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHeaderTodoBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
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

class ItemClickListener(val clickListener: (id: String, state: Int) -> Unit) {
    fun onClick(todo: TodoDto) = clickListener(todo.id, TodoFragment.TODO_UPDATE)
    fun onHeaderClick() = clickListener("-1", TodoFragment.TODO_INSERT)
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