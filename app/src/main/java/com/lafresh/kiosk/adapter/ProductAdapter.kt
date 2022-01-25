package com.lafresh.kiosk.adapter

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.BuildConfig
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.Kiosk
import com.lafresh.kiosk.R
import com.lafresh.kiosk.dialog.ProductComboDialog
import com.lafresh.kiosk.dialog.TasteDialog
import com.lafresh.kiosk.manager.BasicSettingsManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.Product
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils
import com.lafresh.kiosk.utils.FormatUtil

/**
 * Created by Kyle on 2021/2/24.
 */
@RequiresApi(Build.VERSION_CODES.M)
class ProductAdapter(private val mContext: Context) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    var pricePrefix: String? = mContext.getString(R.string.pricePrefix)
    var priceMoneyUnit: String? = mContext.getString(R.string.priceMoneyUnit)
    private var products: MutableList<Product> = mutableListOf()

    // 建立ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivSpeprice: ImageView
        val ivRedeemPoint: ImageView
        var rlImgBtn: ImageButton
        val ivProduct: ImageView
        val tvStopSale: TextView
        val tvProductName: TextView
        val tvSpecialPrice: TextView
        val tvPrice: TextView
        val tvSlash: TextView
        val tvRedeemPoint: TextView
        val btnPurchase: Button

        init {
            ivSpeprice = itemView.findViewById(R.id.iv_spePrice)
            ivRedeemPoint = itemView.findViewById(R.id.iv_redeem_point)
            rlImgBtn = itemView.findViewById(R.id.rl_img)
            ivProduct = itemView.findViewById(R.id.iv_product)
            tvStopSale = itemView.findViewById(R.id.tv_stop_sale)
            tvProductName = itemView.findViewById(R.id.tv_product_name)
            tvSpecialPrice = itemView.findViewById(R.id.tv_special_price)
            tvPrice = itemView.findViewById(R.id.tv_price)
            tvSlash = itemView.findViewById(R.id.tv_slash)
            tvRedeemPoint = itemView.findViewById(R.id.tv_redeem_point)
            btnPurchase = itemView.findViewById(R.id.btn_purchase)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 連結項目布局檔list_item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        products[position].imgfile1?.let {
            CommonUtils.loadImage(
                mContext,
                holder.ivProduct,
                Config.productImgPath + it
            )
        } ?: run {
            CommonUtils.loadImage(
                mContext,
                holder.ivProduct,
                R.drawable.cate_sample
            )
        }

        if (FlavorType.lanxin.name == BuildConfig.FLAVOR) {
            holder.tvPrice.setText(priceMoneyUnit + FormatUtil.removeDot(products[holder.getLayoutPosition()].price1))
        }else{
            holder.tvPrice.setText(pricePrefix + FormatUtil.removeDot(products[holder.getLayoutPosition()].price1))
        }
        products[position].combPrice?.let {
            if (products[position].combPrice != 0.0) {
                if (FlavorType.lanxin.name == BuildConfig.FLAVOR) {
                    holder.tvPrice.setText(priceMoneyUnit + FormatUtil.removeDot(it))
                }else{
                    holder.tvPrice.setText(pricePrefix + FormatUtil.removeDot(it))
                }
            } else {
                if (FlavorType.lanxin.name == BuildConfig.FLAVOR) {
                    holder.tvPrice.setText(priceMoneyUnit + FormatUtil.removeDot(products[position].price1))
                }else{
                    holder.tvPrice.setText(pricePrefix + FormatUtil.removeDot(products[position].price1))
                }

            }
        }
        products[position].spe_price?.let {
            if (it > 0.0) {
                if (FlavorType.lanxin.name == BuildConfig.FLAVOR) {
                    holder.tvSpecialPrice.text = priceMoneyUnit + FormatUtil.removeDot(it)
                }else{
                    holder.tvSpecialPrice.text = pricePrefix + FormatUtil.removeDot(it)
                }

                holder.tvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG)
                holder.tvSpecialPrice.visibility = View.VISIBLE
                holder.ivSpeprice.visibility = View.VISIBLE
                holder.tvSlash.visibility = View.VISIBLE
            }
        }

        switchProductNameType(holder, position)
        showPointsWhenLogin(products[position], holder)
        checkProductSoldOut(products[position], holder)

        holder.btnPurchase.setOnClickListener({
            validateProductComboStatus(position)
        })

        holder.rlImgBtn.setOnClickListener({
            validateProductComboStatus(position)
        })

        holder.ivProduct.setOnClickListener({
            validateProductComboStatus(position)
        })

        holder.tvPrice.setOnClickListener({
            validateProductComboStatus(position)
        })

        validateLastProduct(position, holder)
    }

    private fun switchProductNameType(holder: ViewHolder, position: Int) {
        when (BasicSettingsManager.instance.getBasicSetting()?.kiosk_product_name_type) {
            "FIRST_NAME" -> {
                holder.tvProductName.text = products[position].prod_name1
            }

            "SECOND_NAME" -> {
                validateProductNameIsEmpty(position, holder, products[position].prod_name2)
            }

            "ABBREVIATED_NAME" -> {
                validateProductNameIsEmpty(position, holder, products[position].prod_shortname)
            }

            else -> {
                holder.tvProductName.text = products[position].prod_name1
            }
        }
    }

    private fun validateProductNameIsEmpty(position: Int, holder: ViewHolder, productName: String) {
        if (productName == null || "".equals(productName)) {
            holder.tvProductName.text = products[position].prod_name1
        } else {
            holder.tvProductName.text = productName
        }
    }

    private fun validateLastProduct(position: Int, holder: ViewHolder) {
        if (position == products.lastIndex) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 180
            holder.itemView.layoutParams = params
        }
    }

    private fun validateProductComboStatus(position: Int) {
        if (ClickUtil.isFastDoubleClick()) return
        if (products[position].iscomb == "1") {
            combItemList_Show(products[position])
        } else {
            taste_showDialog(products[position])
        }
    }

    private fun showPointsWhenLogin(product: Product, holder: ViewHolder) {
        if ("1" == product.isredeem && OrderManager.getInstance().isLogin &&
            OrderManager.getInstance().member.points!!.toDouble() > product.redeem_point
                .toInt()
        ) {
            holder.ivRedeemPoint.setVisibility(View.VISIBLE)
            holder.tvRedeemPoint.setVisibility(View.VISIBLE)
            holder.tvRedeemPoint.setText(product.redeem_point + "點")
            holder.ivSpeprice.setVisibility(View.INVISIBLE)
        }
    }

    private fun checkProductSoldOut(product: Product, holder: ViewHolder) {
        if (product.stop_sell) {
            holder.tvStopSale.visibility = View.VISIBLE
            holder.tvStopSale.bringToFront()
            holder.btnPurchase.isEnabled = false
            holder.ivProduct.isEnabled = false
        }
    }

    fun taste_showDialog(productData: Product) {
        val product = com.lafresh.kiosk.Product()
        product.set_byjo(CommonUtils.convertObjectToJson(productData))
        val _tasteDialog = TasteDialog(mContext, product)
        _tasteDialog.show()
    }

    fun combItemList_Show(productData: Product) {
        val product = com.lafresh.kiosk.Product()
        product.set_byjo(CommonUtils.convertObjectToJson(productData))
        val d_combItemList = ProductComboDialog(mContext, product)
        d_combItemList.show()
    }

    fun addItem(product: List<Product>) {
        products.clear()
        product.forEachIndexed { index, it ->
            if (it.is_hidden == "0") {
                products.add(it)
                notifyItemChanged(index)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemCount() = products.size
}
