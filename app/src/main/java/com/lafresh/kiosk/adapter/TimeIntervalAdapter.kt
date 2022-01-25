package com.lafresh.kiosk.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.R
import com.lafresh.kiosk.activity.ShopActivity
import com.lafresh.kiosk.model.MealTime
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Kevin on 2020/4/7.
 */
class TimeIntervalAdapter(private val mContext: Context, private val mData: List<MealTime.DataBean>) : RecyclerView.Adapter<TimeIntervalAdapter.ViewHolder>() {

    // 建立ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 宣告元件
        val tvTime: TextView
        val tvTab: TextView
        val ibTime: ImageButton

        init {
            tvTime = itemView.findViewById(R.id.tv_time)
            tvTab = itemView.findViewById(R.id.tv_tab)
            ibTime = itemView.findViewById(R.id.ib_time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 連結項目布局檔list_item
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_time_interval, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        // 設置tvTime要顯示的內容
        holder.tvTime.text = String.format("%s", mData[position].time)
        holder.tvTab.text = "" + (position + 1)
        holder.ibTime.setOnClickListener {
            val intent = Intent(mContext.applicationContext, ShopActivity::class.java)
            mContext.startActivity(intent)
            Config.mealDate = SimpleDateFormat("yyyy-MM-dd").format(Date()) + " " + mData[position].time + ":00"
            (mContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}
