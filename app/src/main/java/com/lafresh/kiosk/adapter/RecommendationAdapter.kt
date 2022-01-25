package com.lafresh.kiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.R
import com.lafresh.kiosk.model.ProductBean
import com.lafresh.kiosk.utils.CommonUtils
import com.lafresh.kiosk.utils.FormatUtil

/**
 * Created by Kyle on 2020/11/11.
 */

@RequiresApi(Build.VERSION_CODES.M)
class RecommendationAdapter(private val mContext: Context, private val mData: List<ProductBean>) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    var pricePrefix: String? = mContext.getString(R.string.pricePrefix)

    // 建立ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 宣告元件
        val tvProductName: TextView
        val tvCount: TextView
        val ivProduct: ImageView
        val tvSubtract: TextView
        val tvPlus: TextView
        val tvPrice: TextView
        val tvSpecialPrice: TextView
        val tvSplash: TextView

        init {
            tvProductName = itemView.findViewById(R.id.tv_product_name)
            tvCount = itemView.findViewById(R.id.tv_count)
            ivProduct = itemView.findViewById(R.id.iv_product)
            tvPlus = itemView.findViewById(R.id.tv_plus)
            tvSubtract = itemView.findViewById(R.id.tv_subtract)
            tvSpecialPrice = itemView.findViewById(R.id.tv_special_price)
            tvPrice = itemView.findViewById(R.id.tv_price)
            tvSplash = itemView.findViewById(R.id.tv_splash)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 連結項目布局檔list_item
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recommendation, parent, false)

        val height: Int = parent.getMeasuredHeight() / 4
        view.setMinimumHeight(height)
        return ViewHolder(view)
    }

    fun getProductsData(): List<ProductBean> {
        return mData
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        // 設置tvTitle要顯示的內容
        holder.tvProductName.text = mData[position].title

        CommonUtils.loadImage(mContext, R.drawable.get_img_error, holder.ivProduct, mData[position].img)

        checkSpecialPriceExist(position, holder)

        holder.tvPlus.setOnClickListener {
            mData[position].count++
            holder.tvCount.text = mData[position].count.toString()
        }

        holder.tvSubtract.setOnClickListener {
            if (mData[position].count > 0) {
                mData[position].count--
                holder.tvCount.text = mData[position].count.toString()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkSpecialPriceExist(position: Int, holder: ViewHolder) {
        if (mData[position].special_price != null) {
            holder.tvSpecialPrice.visibility = View.VISIBLE
            holder.tvSplash.visibility = View.VISIBLE
            holder.tvSpecialPrice.text = pricePrefix + String.format(mContext.getString(R.string.piece_of_price), FormatUtil.removeDot(mData[position].special_price!!))
            holder.tvPrice.text = pricePrefix + String.format(mContext.getString(R.string.piece_of_price), mData[position].price)
            addDeleteLine(holder)
        } else {
            holder.tvSpecialPrice.visibility = View.GONE
            holder.tvSplash.visibility = View.GONE
            holder.tvPrice.text = pricePrefix + String.format(mContext.getString(R.string.piece_of_price), mData[position].price)
        }
    }

    private fun addDeleteLine(holder: ViewHolder) {
        val paint: Paint = holder.tvPrice.getPaint()
        paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG)
        paint.setAntiAlias(true)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}
