package com.ssafy.frogdetox.adapter

import android.content.Context
import android.media.Image
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.dto.TodoDateDto
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val TAG = "TodoDateAdapter_싸피"
class TodoDateAdapter (val context : Context, var list: MutableList<TodoDateDto>): RecyclerView.Adapter<TodoDateAdapter.TodoWeeklyViewHolder>() {
    inner class TodoWeeklyViewHolder(private var itemView: View): RecyclerView.ViewHolder(itemView) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: TodoDateDto) {
            val date = itemView.findViewById<TextView>(R.id.tv_Day)
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
//            val now = LocalDate.now().format(DateTimeFormatter.ofPattern("dd").withLocale(Locale.forLanguageTag("ko")))
            // 오늘 날짜와 캘린더의 오늘 날짜가 같을 경우 background_blue 적용하기
            Log.d(TAG, "bind: $formatNowTime $formatDateTime")
            if (formatNowTime == formatDateTime) {
                Glide.with(context)
                    .load(R.drawable.todayfrog)//todo : 추후 모양 변경 예정. 개굴 표시로 오늘 표시해도 될듯?
                    .into(itemView.findViewById(R.id.ivDay))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoWeeklyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_tododate, parent, false)

        return TodoWeeklyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TodoWeeklyViewHolder, position: Int) {
        holder.apply { bind(list[position]) }
    }

    override fun getItemCount(): Int = list.size
}