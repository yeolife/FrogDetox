package com.ssafy.frogdetox.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.databinding.ItemListTodoBinding
import com.ssafy.frogdetox.dto.TodoDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TYPE_HEADER = 1
private const val TYPE_ITEM = 2

class TodoListAdapter(private val clickListener: ItemClickListener):
    ListAdapter<DataItem, RecyclerView.ViewHolder>(TodoListDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_HEADER -> HeaderViewHolder.from(parent)
            TYPE_ITEM -> TodoViewHolder.from(parent)
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

    class TodoViewHolder private constructor(val binding: ItemListTodoBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoDto, clickListener: ItemClickListener) {
            binding.todo = item
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): TodoViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemListTodoBinding.inflate(layoutInflater, parent, false)
                return TodoViewHolder(binding)
            }
        }
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

class ItemClickListener(val clickListener: (id: Int) -> Unit) {
    fun onClick(todo: TodoDto) = clickListener(todo.id)
}

sealed class DataItem {
    data class TodoItem(val item: TodoDto) : DataItem() {
        override val id = item.id
    }

    object Header: DataItem() {
        override val id = Int.MIN_VALUE
    }

    abstract val id: Int
}

class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {
    companion object {
        fun from(parent: ViewGroup): HeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_header_todo, parent, false)
            return HeaderViewHolder(view)
        }
    }
}