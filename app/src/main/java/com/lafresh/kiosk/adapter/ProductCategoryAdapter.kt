package com.lafresh.kiosk.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.BuildConfig
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.R
import com.lafresh.kiosk.activity.HomeActivity
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.model.Datum
import com.lafresh.kiosk.model.GetProducts
import com.lafresh.kiosk.model.GetProductsParams
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils
import com.lafresh.kiosk.utils.TimeUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Kyle on 2021/3/2.
 */
@RequiresApi(Build.VERSION_CODES.M)
class ProductCategoryAdapter(private val mContext: Context) : RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder>() {
    private var isInit = true
    private var adapter: ProductAdapter? = null
    private var rvProduct: RecyclerView? = null
    private var serno: String? = null
    private var category: MutableList<Datum> = mutableListOf()
    private var selectedPosition = RecyclerView.NO_POSITION
    private lateinit var preHolder :ViewHolder

    // 建立ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_bg: ImageView
        val tv_subject: TextView
        val btn_prodcate: Button

        init {
            iv_bg = itemView.findViewById(R.id.iv_bg)
            tv_subject = itemView.findViewById(R.id.tv_subject)
            btn_prodcate = itemView.findViewById(R.id.btn_prodcate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 連結項目布局檔list_item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
            CommonUtils.loadImage(mContext, holder.iv_bg, R.drawable.btn_selector)
        }else{
            CommonUtils.loadImage(mContext, holder.iv_bg, R.drawable.shop_catebg)
        }
        initFirstCategorySelected(position, holder)
        holder.tv_subject.text = category[position].subject
        holder.btn_prodcate.setOnClickListener({
            if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
            preHolder.tv_subject.setTextColor(Color.GRAY)
            preHolder = holder
            vaildateSelected(position)
            getProduct(category[position].serno, holder)
            selectedPosition = position
        })
    }

    /**
     * 驗證已選狀態更改
     * */
    private fun vaildateSelected(position: Int) {
        if (selectedPosition != position) {
            notifyItemChanged(selectedPosition)
        }
    }

    /**
     * 首次進入初始化
     * */
    private fun initFirstCategorySelected(position: Int, holder: ViewHolder) {
        if (position == 0 && isInit) {
            preHolder = holder
            getProduct(category[0].serno, holder)
            selectedPosition = position
            isInit = false
        }
    }

    fun addItem(data: List<Datum>) {
        category.clear()
        data.forEachIndexed { index, it ->
            category.add(it)
            notifyItemChanged(index)
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemCount() = category.size

    private fun getProduct(serno: String, holder: ViewHolder) {
        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
            CommonUtils.loadImage(mContext, holder.iv_bg, R.drawable.btn_selector_pressed)
        }else{
            CommonUtils.loadImage(mContext, holder.iv_bg, R.drawable.shop_catebg_select)
        }
        HomeActivity.now!!.idleProof()
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).getProducts(
            GetProductsParams(
                "get_product",
                Config.authKey,
                Config.acckey,
                Config.shop_id,
                Config.saleType,
                serno,
                TimeUtil.getNowTime(),
                Config.group_id
            )
        )
            .enqueue(object : Callback<GetProducts> {
                override fun onResponse(call: Call<GetProducts>, response: Response<GetProducts>) {
                    if (response.isSuccessful) {
                        Config.productImgPath = response.body()!!.imgpath
                        adapter = ProductAdapter(mContext)
                        response.body()!!.product?.let {
                            adapter!!.addItem(it)
                        }
                        // 設置adapter給recycler_view
                        when (BuildConfig.FLAVOR) {
                            FlavorType.FormosaChang.name,
                            FlavorType.hongya.name,
                            FlavorType.mwd.name -> {
                                rvProduct!!.layoutManager = GridLayoutManager(mContext, 3)
                            }

                            FlavorType.lanxin.name -> {
                                rvProduct!!.layoutManager = GridLayoutManager(mContext, 1)
                            }

                            else -> {
                                rvProduct!!.layoutManager = GridLayoutManager(mContext, 4)
                            }
                        }
                        rvProduct!!.adapter = adapter
                    } else {
                        showErrorDialog(serno, holder)
                    }
                }

                override fun onFailure(call: Call<GetProducts>, t: Throwable) {
                    HomeActivity.now!!.idleProof()
                }
            })

        if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name)) {
            holder.tv_subject.setTextColor(Color.WHITE)
        }
    }

    fun addProductView(rvProduct: RecyclerView) {
        this.rvProduct = rvProduct
    }

    fun showErrorDialog(serno: String, holder: ViewHolder) {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment
            .setTitle(R.string.connectionError)
            .setMessage(R.string.connectionError_message)
            .setConfirmButton({
                getProduct(serno, holder)
                alertDialogFragment.dismiss()
            })
            .setUnCancelAble()
            .setCancelButton(R.string.cancel) {
                if (!ClickUtil.isFastDoubleClick()) {
                    alertDialogFragment.dismiss()
                }
            }
            .show((mContext as AppCompatActivity).fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }
}
