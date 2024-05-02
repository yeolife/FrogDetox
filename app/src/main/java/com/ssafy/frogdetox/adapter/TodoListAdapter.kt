package com.ssafy.frogdetox.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.dto.TodoDto

class TodoListAdapter(var list: MutableList<TodoDto>): RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {
    interface ItemClickListener {
        fun onClick(position: Int)
    }

    lateinit var itemClickListener: ItemClickListener
    inner class TodoViewHolder(private var itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: TodoDto) {
            itemView.findViewById<CheckBox>(R.id.cb_isComplete).apply {
                isChecked = item.isComplete
            }
            itemView.findViewById<TextView>(R.id.tv_context).text = item.content
        }

        init {
            itemView.setOnClickListener {
                itemClickListener.onClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_todo, parent, false)

        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.apply { bind(list[position]) }
    }

    override fun getItemCount(): Int = list.size
}