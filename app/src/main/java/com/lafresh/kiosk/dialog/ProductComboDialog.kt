package com.lafresh.kiosk.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.lafresh.kiosk.*
import com.lafresh.kiosk.Kiosk.hidePopupBars
import com.lafresh.kiosk.activity.ShopActivity
import com.lafresh.kiosk.dialog.KeyboardDialog.OnEnterListener
import com.lafresh.kiosk.httprequest.ApiRequest
import com.lafresh.kiosk.httprequest.model.GetCombRes
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.utils.*
import com.lafresh.kiosk.utils.CommonUtils.loadImage
import com.lafresh.kiosk.utils.CommonUtils.showMessage
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.M)
class ProductComboDialog(ct: Context, product: Product) : Dialog(ct, android.R.style.Theme_Translucent_NoTitleBar) {
    var ct: Context
    lateinit var btnOk: Button
    lateinit var btnMore: Button
    lateinit var btnLess: Button
    lateinit var btnBack: Button
    lateinit var btn1: Button
    lateinit var btn2: Button
    lateinit var btn3: Button
    lateinit var btn4: Button
    lateinit var btn5: Button
    lateinit var tvProdname: TextView
    lateinit var tvUnitprice: TextView
    lateinit var tvCount: TextView
    lateinit var tvTotal: TextView
    lateinit var tvProdcontent: TextView
    lateinit var product: Product
    lateinit var llCombitem: LinearLayout
    lateinit var llTastedetail: LinearLayout
    lateinit var llTaste: LinearLayout

    fun setUI() {
        tvProdname = findViewById(R.id.tv_prodname)
        tvUnitprice = findViewById(R.id.tv_unitPrice)
        tvProdcontent = findViewById(R.id.tv_prodContent)
        btnOk = findViewById(R.id.btn_ok)
        btnOk.isEnabled = false
        btnBack = findViewById(R.id.btn_back)
        btnLess = findViewById(R.id.btn_less)
        btnMore = findViewById(R.id.btn_more)
        tvCount = findViewById(R.id.tv_count)
        tvTotal = findViewById(R.id.tv_total)
        llCombitem = findViewById(R.id.ll_combItem)
        llTastedetail = findViewById(R.id.ll_tasteDetail)
        llTaste = findViewById(R.id.ll_taste)
        llTaste.removeAllViews()
        // tv_title.setText(product.prod_name1+" 口味設定");
        tvProdname.setText(product.prod_name1)
        val price = product._priceStr
        tvUnitprice.setText("$$price")
        llCombitem.removeAllViews()
        llTastedetail.removeAllViews()
        btn1 = findViewById(R.id.btn_1)
        btn2 = findViewById(R.id.btn_2)
        btn3 = findViewById(R.id.btn_3)
        btn4 = findViewById(R.id.btn_4)
        btn5 = findViewById(R.id.btn_5)
        val imageView = findViewById<ImageView>(R.id.iv_comb)
        // 活動改圖
        if (imageView != null && product.productVO.imgfile1 != null) {
            val path = Config.productImgPath + product.productVO.imgfile1
            loadImage(ct, imageView, path)
        }
    }

    private fun getMainTastes(productId: String) {
        val post = Post(ct, ApiRequest.PRODUCT_SERVICE_URL)
        post.addField("authkey", Config.authKey)
        post.addField("op", "get_taste")
        post.addField("prod_id", productId)
        post.SetPostListener { Response: String? ->
            try {
                val jo = JSONObject(Response)
                product.al_taste.clear()
                if (jo.getInt("code") == 0) {
                    try {
                        if (jo.has("taste")) {
                            val jaTaste = jo.getJSONArray("taste")
                            for (i in 0 until jaTaste.length()) {
                                val taste = Taste(product)
                                taste.set_byjo(jaTaste.getJSONObject(i))
                                taste.updateUI(ct)
                                product.al_taste.add(taste)
                                if (taste.view.parent != null) {
                                    (taste.view.parent as ViewGroup).removeAllViews()
                                }
                                llTaste.addView(taste.view)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    updateUI()
                } else {
                    CommonUtils.showMessage(ct, jo.getString("message"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        post.GO()
    }

    fun set_actions() {
        btnBack.setOnClickListener { view: View? -> dismiss() }
        btnOk.setOnClickListener { view: View? ->
            val order = Order(product)
            order.updateUI(ct)
            Bill.Now.order_add(order)
            OrderManager.getInstance().addItem(order)
            dismiss()
        }
        val ocl_moreorless = View.OnClickListener { view: View ->
            if (view.id == R.id.btn_less) {
                if (product.count > 1) {
                    product.count--
                }
            }
            if (view.id == R.id.btn_more) {
                product.count++
            }
            updateUI()
        }
        btnMore.setOnClickListener(ocl_moreorless)
        btnLess.setOnClickListener(ocl_moreorless)
        val ocl_setNumber = View.OnClickListener { view: View ->
            product.count = (view.tag.toString() + "").toInt()
            updateUI()
        }
        btn1.setOnClickListener(ocl_setNumber)
        btn2.setOnClickListener(ocl_setNumber)
        btn3.setOnClickListener(ocl_setNumber)
        btn4.setOnClickListener(ocl_setNumber)
        btn5.setOnClickListener(ocl_setNumber)
        findViewById<View>(R.id.rl_keyboard).setOnClickListener { view: View? ->
            if (ClickUtil.isFastDoubleClick()) {
                return@setOnClickListener
            }
            val keyboardDialog = KeyboardDialog(ct)
            keyboardDialog.show()
            keyboardDialog.onEnterListener = OnEnterListener { count: Int, text: String? ->
                product.count = count
                updateUI()
            }
        }
    }

    fun combs_get() {
        product.al_combItem.clear()
        val post = Post(ct, ApiRequest.PRODUCT_SERVICE_URL)
        post.addField("authkey", Config.authKey)
        post.addField("op", "get_comb")
        post.addField("shop_id", Config.shop_id)
        post.addField("prod_id", product.prod_id)
        post.SetPostListener { Response: String? ->
            try {
                val jo = JSONObject(Response)
                val getCombRes = Json.fromJson(Response, GetCombRes::class.java)
                if (getCombRes.code == 0) {
                    if (getCombRes.datacnt != 0) {
                        val combBeanList = getCombRes.comb
                        var i = 0
                        var combBeanListSize = combBeanList!!.size
                        combBeanList.forEach {
                            Thread.sleep(100)
                            val combItem = CombItem()
                            combItem.init(it)
                            i++
                            getComboTastes(ct, it.prod_id!!, it, combItem, false, i, combBeanListSize)
                        }
                    } else {
                        btnOk.isEnabled = true
                    }
                } else {
                    btnOk.isEnabled = true
                    showMessage(ct, jo.getString("message"))
                }
                updateUI()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        post.GO()
    }

    fun updateUI() {
        val pricePrefix = ct.resources.getString(R.string.pricePrefix)
        tvUnitprice.text = pricePrefix + FormatUtil.removeDot(product.specialPrice)
        tvCount.text = product.count.toString() + ""
        tvTotal.text =
            pricePrefix + FormatUtil.removeDot(product.totalSpecialPrice)
        tvProdcontent.text =
            if (product.prod_content == "null") "" else Html.fromHtml(product.prod_content)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Kiosk.idleCount = Config.idleCount
        return super.dispatchTouchEvent(ev)
    }

    private fun getComboTastes(
        context: Context,
        productId: String,
        combBean: GetCombRes.CombBean,
        combItem: CombItem,
        isChange: Boolean,
        i: Int,
        combBeanListSize: Int
    ) {
        val post = Post(context, ApiRequest.PRODUCT_SERVICE_URL)
        post.addField("authkey", Config.authKey)
        post.addField("op", "get_taste")
        post.addField("prod_id", productId)
        post.SetPostListener { Response: String? ->
            try {
                LogUtils.info(Response)
                val jo = JSONObject(Response)
                if (jo.getInt("code") == 0) {
                    try {
                        combItem.updateUI(ct, jo.has("taste"))
                        combItem.onChangeListener = object : CombItem.OnChangeListener {
                            override fun onChange(productId: String) {
                                getComboTastes(
                                    ct,
                                    productId,
                                    combBean,
                                    combItem,
                                    true,
                                    i,
                                    combBeanListSize
                                )
                                updateUI()
                            }
                        }
                        if (!isChange) {
                            product.al_combItem.add(combItem)
                            if (combBean.isHidden) {
                                getMainTastes(productId)
                                return@SetPostListener
                            }
                            llCombitem.addView(combItem.view)
                            llTastedetail.addView(combItem.rowTop)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    // #33009 待所有數值載入完成後在啟用按鈕點擊
                    if (i == combBeanListSize) {
                        btnOk.isEnabled = true
                    }
                } else {
                    btnOk.isEnabled = true
                    CommonUtils.showMessage(this.context, jo.getString("message"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        post.GO()
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        hidePopupBars(this)
        setContentView(R.layout.d_combitemlist)
        this.ct = ct
        ShopActivity.AL_DialogToClose!!.add(this)
        product.d_combItemList = this
        this.product = product
        setUI()
        set_actions()
        updateUI()
        combs_get()
    }
}
