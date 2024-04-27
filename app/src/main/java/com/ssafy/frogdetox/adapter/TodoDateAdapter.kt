package com.ssafy.frogdetox.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.dto.TodoDateDto

class TodoDateAdapter (var list: MutableList<TodoDateDto>): RecyclerView.Adapter<TodoDateAdapter.TodoWeeklyViewHolder>() {
    inner class TodoWeeklyViewHolder(private var itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: TodoDateDto) {
            itemView.findViewById<TextView>(R.id.tv_Day).text = item.week.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoWeeklyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_tododate, parent, false)

        return TodoWeeklyViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoWeeklyViewHolder, position: Int) {
        holder.apply { bind(list[position]) }
    }

    override fun getItemCount(): Int = if(list.size > 7) 7 else list.size
}