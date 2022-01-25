package com.lafresh.kiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.R
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.PickupMethod

@RequiresApi(Build.VERSION_CODES.M)
class PickupMethodAdapter(private val mContext: Context, private val mData: List<PickupMethod>, onPickupMethodSelectListener: OnPickupMethodSelectListener) : RecyclerView.Adapter<PickupMethodAdapter.ViewHolder>() {
    // 建立ViewHolder
    interface OnPickupMethodSelectListener {
        fun onSelect(pickupMethodMsg: String?)
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 宣告元件
        val btnType: Button = itemView.findViewById(R.id.btn_type)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 連結項目布局檔list_item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pickup_methods, parent, false)
        return ViewHolder(view)
    }
    private val monPickupMethodSelectListener = onPickupMethodSelectListener
    private var nowSelectPos = -1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.btnType.text = mData[position].name
        holder.btnType.setOnClickListener {
            nowSelectPos = position
            notifyDataSetChanged()
            monPickupMethodSelectListener.onSelect(mData[position].message)
            OrderManager.getInstance().pickupMethod = mData[position]
        }
        if (nowSelectPos == position) {
            holder.btnType.setBackgroundResource(R.drawable.btn_large_retangle_pressed)
        } else {
            holder.btnType.setBackgroundResource(R.drawable.btn_large_retangle)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}
