package com.lafresh.kiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.Kiosk
import com.lafresh.kiosk.R
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.Order
import com.lafresh.kiosk.model.TicketBean
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils
import java.util.*
import kotlinx.coroutines.*

/**
 * Created by Kevin on 2020/10/14.
 */
@RequiresApi(Build.VERSION_CODES.M)
class CouponAdapter(private val mContext: Context) : RecyclerView.Adapter<CouponAdapter.ViewHolder>() {
    private lateinit var checkedArray: BooleanArray
    private var tickets: MutableList<TicketBean> = mutableListOf()
    private var tempTickets: MutableList<Order.TicketsBean> = mutableListOf()
    private var tempPrice: Int = 0

    val order = OrderManager.getInstance()

    // 建立ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 宣告元件
        val tvTitle: TextView
        val tvUse: TextView
        val tvSubTitle: TextView
        val cbUse: CheckBox
        val ivLogo: ImageView
        val clCoupon: ConstraintLayout

        init {
            tvTitle = itemView.findViewById(R.id.tv_title)
            tvUse = itemView.findViewById(R.id.tv_use)
            tvSubTitle = itemView.findViewById(R.id.tv_subtitle)
            cbUse = itemView.findViewById(R.id.btn_use)
            ivLogo = itemView.findViewById(R.id.iv_logo)
            clCoupon = itemView.findViewById(R.id.cl_coupon)
        }
    }

    fun addItem(ticket: List<TicketBean>?) {
        tickets.clear()
        tickets.addAll(ticket!!)
        checkedArray = BooleanArray(ticket.size)
        notifyDataSetChanged()
    }

    fun addTempItem() {
        if (OrderManager.getInstance().tickets.size > 0) {
            OrderManager.getInstance().addAllTempTickets(OrderManager.getInstance().tickets)
            tempTickets.addAll(OrderManager.getInstance().tickets)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 連結項目布局檔list_item
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_coupon, parent, false)

        val height: Int = parent.getMeasuredHeight() / 4
        view.setMinimumHeight(height)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        // 設置tvTitle要顯示的內容
        holder.tvTitle.text = tickets[position].name
        holder.tvSubTitle.text = String.format("有效日期：%s ~ %s", tickets[position].begin_date, tickets[position].end_date)
        CommonUtils.loadImage(mContext, holder.ivLogo, R.drawable.icon_liangshehan)
        showTicketInfo(holder, position)
        holder.cbUse.setOnClickListener {
            val number = Order.TicketsBean()
            number.number = tickets[position].id
            number.price = tickets[position].price
            if ((it as CheckBox).isChecked) {
                if ((tempPrice + tickets[position].price) > order.totalFee) {
                    Kiosk.showMessageDialog(mContext, mContext.getString(R.string.coupon_price_large_than_cart))
                    it.isChecked = false
                    return@setOnClickListener
                } else {
                    tempPrice += tickets[position].price
                    checkedArray[position] = true
                    holder.cbUse.setChecked(it.isChecked)
                    holder.cbUse.setBackgroundResource(R.drawable.cb_checked)
                    holder.tvUse.text = "使用"
                    holder.tvUse.setTextColor(Color.parseColor("#FF0057"))
                    order.addTempTickets(number)
                    order.removeTickets(number)
                }
            } else {
                if (tickets[position].price > 0) {
                    tempPrice = tempPrice - tickets[position].price
                }
                checkedArray[position] = false
                setDefaultCheckbox(holder, position)
                order.removeTempTickets(number)
                order.removeTickets(number)
            }
        }
        checkCouponIsUsed(tickets[position].id, holder, position, if (order.tickets.size == 0) order.tempTickets else order.tickets)
        checkCurrentCheckedValue(position, holder)
    }

    /**
     * 用一個boolean陣列暫存checkbox的數值，以免滑動列表導致出現已勾選的老問題.
     * */
    private fun checkCurrentCheckedValue(position: Int, holder: ViewHolder) {
        if (checkedArray[position]) {
            holder.cbUse.setChecked(true)
            holder.cbUse.setBackgroundResource(R.drawable.cb_checked)
            holder.tvUse.text = "使用"
            holder.tvUse.setTextColor(Color.parseColor("#FF0057"))
        } else {
            setDefaultCheckbox(holder, position)
        }
    }

    /**
     * 點擊可顯示目前票券資訊
     * */
    private fun showTicketInfo(holder: ViewHolder, position: Int) {
        holder.clCoupon.setOnClickListener {
            val alertDialogFragment =
                AlertDialogFragment()
            alertDialogFragment
                    .setTitle(R.string.coupon_title)
                    .setMessage(tickets[position].description)
                    .setConfirmButton {
                        if (!ClickUtil.isFastDoubleClick()) {
                            alertDialogFragment.dismiss()
                        }
                    }
                    .setUnCancelAble()
                    .show((mContext as AppCompatActivity).fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
        }
    }

    /**
     * 設定checkbox初始化到未勾選狀態
     * */
    private fun setDefaultCheckbox(holder: ViewHolder, position: Int) {
        holder.tvUse.text = "未使用"
        holder.cbUse.isChecked = false
        checkedArray[position] = false
        holder.cbUse.setBackgroundResource(R.drawable.cb_unchecked)
        holder.cbUse.setTextColor(Color.BLACK)
        holder.tvUse.setTextColor(Color.parseColor("#7a7a7a"))
    }

    /**
     * 這功能檢查返回此頁面後已勾選的票券要重新在勾選一次.
     * */
    private fun checkCouponIsUsed(id: String, holder: ViewHolder, position: Int, tickets: MutableList<Order.TicketsBean>) {
        if (order.tickets.size > 0) {
            tickets.forEach {
                if ((it.number == id)) {
                    checkedArray[position] = true
                    holder.cbUse.isChecked = true
                    holder.cbUse.setBackgroundResource(R.drawable.cb_checked)
                    holder.tvUse.text = "使用"
                    holder.tvUse.setTextColor(Color.parseColor("#FF0057"))
                }
            }
        }
    }

    fun addTempTicketsToTickets() {
        OrderManager.getInstance().addAllTickets(tempTickets)
    }

    override fun getItemCount(): Int {
        return tickets.size
    }
}
