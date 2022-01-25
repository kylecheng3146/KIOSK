package com.lafresh.kiosk

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.activity.HomeActivity
import com.lafresh.kiosk.activity.ShopActivity
import com.lafresh.kiosk.adapter.ProductAdapter
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.model.GetProducts
import com.lafresh.kiosk.model.GetProductsParams
import com.lafresh.kiosk.shoppingcart.model.ProductCate
import com.lafresh.kiosk.utils.TimeUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.M)
class ProductCategory {
    private var adapter: ProductAdapter? = null
    private var rvProduct: RecyclerView? = null
    private var subject: String? = null
    private var serno: String? = null
    var viewTop: View? = null
    private var context: Context? = null
    var btnProductCategory: Button? = null
    private val me = this
    private var ivBackgroundTop: ImageView? = null
    fun init(productCate: ProductCate) {
        subject = productCate.subject
        serno = productCate.serno
    }

    fun updateTopUI(context: Context?, rvProduct: RecyclerView?) {
        this.context = context
        val ift_01 = LayoutInflater.from(context)
        viewTop = ift_01.inflate(R.layout.v_prodcate_top, null)
        val tvSubject = viewTop!!.findViewById<TextView>(R.id.tv_subject)
        btnProductCategory = viewTop!!.findViewById(R.id.btn_prodcate)
        ivBackgroundTop = viewTop!!.findViewById(R.id.iv_bg)
        tvSubject.text = subject
        this.rvProduct = rvProduct
    }

    fun setActions(shopActivity: ShopActivity) {
        btnProductCategory!!.setOnClickListener { view: View? ->
            if (selected != null) {
                selected!!.ivBackgroundTop!!.setImageDrawable(shopActivity.getDrawable(R.drawable.shop_catebg))
            }
            selected = me
            ivBackgroundTop!!.setImageDrawable(shopActivity.getDrawable(R.drawable.shop_catebg_select))
            shopActivity.tv_prodtitle!!.visibility = View.INVISIBLE
            shopActivity.gl_product!!.removeAllViews()
            getProduct(shopActivity)
        }
    }

    private fun getProduct(shopActivity: ShopActivity) {
        HomeActivity.now!!.idleProof()
//        shopActivity.pgb!!.visibility = View.VISIBLE
//        val listener: ApiListener = object : ApiListener {
//            override fun onSuccess(apiRequest: ApiRequest, body: String) {
//                shopActivity.runOnUiThread {
//                    HomeActivity.now!!.idleProof()
//                    shopActivity.btn_restart!!.setOnClickListener(shopActivity)
//                    shopActivity.pgb!!.visibility = View.GONE
//                    try {
//                        val jo = JSONObject(body)
//                        if (jo.getInt("code") == 0) {
//                            if (jo.getString("datacnt") == "0") {
//                                shopActivity.tv_prodtitle!!.visibility = View.VISIBLE
//                                shopActivity.tv_prodtitle!!.text = shopActivity.getString(R.string.noProductInCate)
//                            } else {
//                                Config.productImgPath = jo.getString("imgpath")
//                                val ja_product = jo.getJSONArray("product")
//                                for (i in 0 until ja_product.length()) {
//                                    val product = Product(me)
//                                    product.set_byjo(ja_product.getJSONObject(i))
//                                    product.updateUI(context)
//                                    shopActivity.gl_product!!.addView(product.v)
//                                }
//                                if (ja_product.length() == 0) {
//                                    shopActivity.tv_prodtitle!!.visibility = View.VISIBLE
//                                    shopActivity.tv_prodtitle!!.text = shopActivity.getString(R.string.noProductInCate)
//                                }
//                            }
//                        } else {
//                            context?.let {
//                                CommonUtils.showMessage(it, jo.getString("message"))
//                            }
//                        }
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                        FirebaseCrashlytics.getInstance().recordException(e)
//                    } finally {
//                        shopActivity.enableProdCateButton(true)
//                    }
//                }
//            }
//
//            override fun onFail() {
//                shopActivity.runOnUiThread {
//                    shopActivity.pgb!!.visibility = View.GONE
//                    shopActivity.enableProdCateButton(true)
//                    shopActivity.btn_restart!!.setOnClickListener(shopActivity)
//                    HomeActivity.now!!.idleProof()
//                    val alertDialogFragment =
//                        AlertDialogFragment()
//                    alertDialogFragment.setTitle(R.string.connectionError)
//                            .setMessage(R.string.tryLater)
//                            .setConfirmButton(R.string.retry) { v: View? ->
//                                if (!ClickUtil.isFastDoubleClick()) {
//                                    alertDialogFragment.dismiss()
//                                    getProduct(shopActivity)
//                                }
//                            }
//                            .setCancelButton(R.string.returnHomeButton) { v: View? -> shopActivity.finish() }
//                            .setUnCancelAble()
//                            .show(shopActivity.fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
//                }
//            }
//        }
//        val getProductApiRequest = GetProductApiRequest(serno)
//        getProductApiRequest.setConnectTimeout(3)
//        getProductApiRequest.setReadTimeout(3)
//        getProductApiRequest.setApiListener(listener).setRetry(3).go()

        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).getProducts(GetProductsParams(
            "get_product",
            Config.authKey,
            Config.acckey,
            Config.shop_id,
            Config.saleType,
            serno!!,
            TimeUtil.getNowTime(),
            Config.group_id
        ))
            .enqueue(object : Callback<GetProducts> {
                override fun onResponse(call: Call<GetProducts>, response: Response<GetProducts>) {
                    if (response.isSuccessful) {
                        Config.productImgPath = response.body()!!.imgpath
                        adapter = ProductAdapter(shopActivity)
                        response.body()!!.product?.let {
                            adapter!!.addItem(it)
                        }
                        // 設置adapter給recycler_view
                        rvProduct!!.adapter = adapter
                    } else {
//                        showErrorDialog()
                    }
                }

                override fun onFailure(call: Call<GetProducts>, t: Throwable) {
                    HomeActivity.now!!.idleProof()
                }
            })
    }

    companion object {
        var selected: ProductCategory? = null
    }
}
