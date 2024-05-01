package com.ssafy.frogdetox.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.databinding.ItemListTododateBinding
import com.ssafy.frogdetox.dto.TodoDateDto
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val TAG = "TodoDateAdapter_싸피"

class TodoDateAdapter (val context : Context): ListAdapter<TodoDateDto, TodoDateAdapter.TodoWeeklyViewHolder>(TodoDateComparator) {
    companion object TodoDateComparator: DiffUtil.ItemCallback<TodoDateDto>() {
        override fun areItemsTheSame(oldItem: TodoDateDto, newItem: TodoDateDto): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: TodoDateDto, newItem: TodoDateDto): Boolean {
            return oldItem == newItem
        }
    }

    inner class TodoWeeklyViewHolder(private var binding: ItemListTododateBinding): RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: TodoDateDto) {
            val date = itemView.findViewById<TextView>(R.id.tv_Day)
            val today = itemView.findViewById<ImageView>(R.id.ivBlank)
//            val week = itemView.findViewById<TextView>(R.id.tv_Day).text

            Glide.with(context)
            .load(R.drawable.cutefrog)
            .into(itemView.findViewById(R.id.ivDay))
            val currentNowTime =
                Instant.ofEpochMilli(item.date).atZone(ZoneId.systemDefault()).toLocalDateTime()
            // 오늘 날짜
            val formatNowTime = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일").format(currentNowTime)
            val days = DateTimeFormatter.ofPattern("dd").format(currentNowTime)
            date.text = days

            val currentMillis = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()?.toEpochMilli() ?: 0
            val currentDateTime =
                Instant.ofEpochMilli(currentMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
            val formatDateTime = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일").format(currentDateTime)
            // 오늘 날짜와 캘린더의 오늘 날짜가 같을 경우 background_blue 적용하기
            Log.d(TAG, "bind: $formatNowTime $formatDateTime ${item.date} $currentMillis")
            if (formatNowTime == formatDateTime) {
                today.visibility= View.VISIBLE
                Glide.with(context)
                    .load(R.drawable.todayfrog)//todo : 추후 모양 변경 예정. 개굴 표시로 오늘 표시해도 될듯?
                    .into(itemView.findViewById(R.id.ivDay))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoWeeklyViewHolder {
        return TodoWeeklyViewHolder(
            DataBindingUtil.inflate( LayoutInflater.from(parent.context), R.layout.item_list_tododate, parent, false )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TodoWeeklyViewHolder, position: Int) {
        holder.apply {
            bind(getItem(position))
        }
    }
}