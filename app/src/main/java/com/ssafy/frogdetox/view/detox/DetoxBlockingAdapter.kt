package com.ssafy.frogdetox.view.detox

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.data.AppInfoDto

private const val TAG = "DetoxBlockingAdapter"
class DetoxBlockingAdapter(var list: List<AppInfoDto>): RecyclerView.Adapter<DetoxBlockingAdapter.CustomViewHolder>() {
    inner class CustomViewHolder(private var itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: AppInfoDto) {
            itemView.findViewById<ImageView>(R.id.iv_appIcon).setImageDrawable(item.appIcon)
            itemView.findViewById<TextView>(R.id.tv_appTitle).text = item.appTitle
            itemView.findViewById<SwitchCompat>(R.id.switch_appBlock).isChecked = item.appBlockingState
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_detox_blocking, parent, false)

        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.apply { bind(list[position]) }
    }
}