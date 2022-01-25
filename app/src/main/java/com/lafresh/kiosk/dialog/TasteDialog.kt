package com.lafresh.kiosk.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.lafresh.kiosk.*
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.httprequest.ApiRequest
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.utils.*
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.M)
class TasteDialog(context: Context, product: Product) : Dialog(context, android.R.style.Theme_Translucent_NoTitleBar) {

    private var ct: Context
    private var btnOk: Button? = null
    private var btnLess: Button? = null
    private var btnMore: Button? = null
    private var tbRedeemPoint: ToggleButton? = null
    private var btnBack: Button? = null
    private var imageBtn_restart: ImageButton? = null
    private var add_btn: Button? = null
    private var reduce_btn: Button? = null
    private var btn1: Button? = null
    private var btn2: Button? = null
    private var btn3: Button? = null
    private var btn4: Button? = null
    private var btn5: Button? = null
    private var tv_number: TextView? = null
    private var tvTitle: TextView? = null
    private var tvCount: TextView? = null
    private var tvTotal: TextView? = null
    private var tvPoint: TextView? = null
    private var tvProdname: TextView? = null
    private var tvUnitprice: TextView? = null
    private var tvPointRedeem: TextView? = null
    private var tvContent: TextView? = null
    private var llTaste: LinearLayout? = null
    private var rlKeyboard: RelativeLayout? = null
    private var product: Product
    private lateinit var onRefreshListener: OnRefreshListener

    interface ClickOkListener {
        fun onOK()
    }

    @JvmField
    var clickOkListener: ClickOkListener? = null

    fun setUI() {
        tv_number = findViewById(R.id.tv_number)
        tvTitle = findViewById(R.id.tv_title)
        tvContent = findViewById(R.id.tv_content)
        tvUnitprice = findViewById(R.id.tv_unitPrice)
        tvTotal = findViewById(R.id.tv_total)
        tvProdname = findViewById(R.id.tv_prodname)
        btnOk = findViewById(R.id.btn_ok)
        btnBack = findViewById(R.id.btn_back)
        imageBtn_restart = findViewById(R.id.imageBtn_restart)
        llTaste = findViewById(R.id.ll_taste)
        btnLess = findViewById(R.id.btn_less)
        btnMore = findViewById(R.id.btn_more)
        tvPoint = findViewById(R.id.tv_point)
        tvPointRedeem = findViewById(R.id.tvPointRedeem)
        tbRedeemPoint = findViewById(R.id.tb_redeem_point)
        tvCount = findViewById(R.id.tv_count)
        tv_number!!.text = "${Bill.Now.AL_Order.size}"
        tvTitle!!.text = ct.resources.getString(R.string.chooseFavor)
        tvCount!!.text = product.count.toString()
        if (tvProdname != null) {
            tvProdname!!.text = product.prod_name1
        }
        if (tvUnitprice != null) {
            try {
                var pricePrefix = "";
                if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
                    pricePrefix = ct.resources.getString(R.string.priceMoneyUnit)
                }else{
                    pricePrefix = ct.resources.getString(R.string.pricePrefix)
                }
                val unitPrice = pricePrefix + product._priceStr
                tvUnitprice!!.text = unitPrice
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (llTaste != null) {
            llTaste!!.removeAllViews()
        }
        if (tvContent != null) {
            tvContent!!.text = if (product.prod_content == "null") "" else Html.fromHtml(product.prod_content)
        }
        add_btn = findViewById(R.id.add_btn)
        reduce_btn = findViewById(R.id.reduce_btn)
        btn1 = findViewById(R.id.btn_1)
        btn2 = findViewById(R.id.btn_2)
        btn3 = findViewById(R.id.btn_3)
        btn4 = findViewById(R.id.btn_4)
        btn5 = findViewById(R.id.btn_5)
        rlKeyboard = findViewById(R.id.rl_keyboard)
        val imageView = findViewById<ImageView>(R.id.iv_comb)
        // 活動改圖
        if (imageView != null && product.productVO.imgfile1 != null) {
            val path = Config.productImgPath + product.productVO.imgfile1
            CommonUtils.loadImage(ct, imageView, path)
        }

        if ("1".equals(product.productVO.isredeem) && OrderManager.getInstance().isLogin()) {
            tbRedeemPoint!!.visibility = View.VISIBLE
            tvPointRedeem!!.visibility = View.VISIBLE
            tbRedeemPoint!!.setOnClickListener {
                if (tbRedeemPoint!!.isChecked) {
                    product.productVO.usePoint = true
                    tvPoint!!.visibility = View.VISIBLE
                    updateUI()
                } else {
                    product.productVO.usePoint = false
                    tvPoint!!.visibility = View.GONE
                    updateUI()
                }
            }
        }
    }

    fun setActions() {
        btnOk!!.setOnClickListener {
            // 目前選擇總數
            val productCount = product.count
            var redeemPoint = 0
            // 目前選擇商品點數
            if (product.productVO.redeem_point != null) {
                redeemPoint = product.productVO.redeem_point.toInt()
            }
            // 總共使用的點數
            val totalPoint = productCount * redeemPoint
            // 全部點數可購買數量
            if (OrderManager.getInstance().isLogin.and(product.productVO.redeem_point != "0").and(product.productVO.usePoint)) {
                val totalCountByPoints = CommonUtils.removeDot(OrderManager.getInstance().member.points!!).toInt() / product.productVO.redeem_point.toInt()
                // 點數是否足夠
                val isPointNotEnough = totalPoint > OrderManager.getInstance().member.points!!.toDouble()
                // 剩餘需用現金購買的數量
                val productCountByCash = productCount - totalCountByPoints
                val productCountByPoint = productCount - productCountByCash
                // 如果點數不足購買全部只能買部分商品
                (totalCountByPoints > 0).and(productCount > totalCountByPoints).let {
                    when (it) {
                        true -> {
                            showNotEnoughDialog(productCountByCash.toString(), productCountByPoint, redeemPoint, product.productVO.prod_name1)
                        }
                        false -> {
                            // 判斷如果點數不足則顯示錯誤視窗
                            OrderManager.getInstance().isLogin.and(isPointNotEnough).and(product.productVO.usePoint).let {
                                when (it) {
                                    true -> showErrorDialog()
                                    false -> addItem("0", productCount, redeemPoint)
                                }
                            }
                        }
                    }
                }
            } else {
                addItem("0", productCount, redeemPoint)
            }
        }
        val oclMoreorless = View.OnClickListener { view ->
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
        btnMore!!.setOnClickListener(oclMoreorless)
        btnLess!!.setOnClickListener(oclMoreorless)
        btnBack!!.setOnClickListener { dismiss() }
        imageBtn_restart!!.setOnClickListener { dismiss() }
        if (product.comb_type != 2) {
            val oclAddReduce = View.OnClickListener { view ->
                if (view.id == R.id.reduce_btn) {
                    if (product.count > 1) {
                        product.count--
                    }
                }
                if (view.id == R.id.add_btn) {
                    product.count++
                }
                updateUI()
            }
            add_btn!!.setOnClickListener(oclAddReduce)
            reduce_btn!!.setOnClickListener(oclAddReduce)

            val oclSetnumber = View.OnClickListener { view: View ->
                product.count = (view.tag.toString() + "").toInt()
                updateUI()
            }
            btn1!!.setOnClickListener(oclSetnumber)
            btn2!!.setOnClickListener(oclSetnumber)
            btn3!!.setOnClickListener(oclSetnumber)
            btn4!!.setOnClickListener(oclSetnumber)
            btn5!!.setOnClickListener(oclSetnumber)
        }
        if (rlKeyboard != null) {
            rlKeyboard!!.setOnClickListener {
                if (ClickUtil.isFastDoubleClick()) {
                    return@setOnClickListener
                }
                val keyboardDialog = KeyboardDialog(ct)
                keyboardDialog.centerText.setText(R.string.input_product_number)
                keyboardDialog.show()
                keyboardDialog.onEnterListener =
                    KeyboardDialog.OnEnterListener { count1: Int, _: String? ->
                        product.count = count1
                        updateUI()
                    }
            }
        }
    }

    private fun addItem(productCountByCash: String, productCountByPoints: Int, redeemPoint: Int) {
        if (product.comb_type != 2) {

            if (productCountByCash != "0") {
                product.count = productCountByCash.toInt()
                product.productVO.redeem_point = "0"
                product.productVO.usePoint = false
                addOrder(product)

                product.count = productCountByPoints
                product.productVO.usePoint = true
                product.productVO.redeem_point = redeemPoint.toString()
                addOrder(product)
            } else {
                addOrder(product)
            }

            val totalPoint = productCountByPoints * redeemPoint

            if (OrderManager.getInstance().isLogin && product.productVO.usePoint) {
                OrderManager.getInstance().member.appliedPoints += totalPoint
                OrderManager.getInstance().member.points = (CommonUtils.removeDot(OrderManager.getInstance().member.points!!).toInt() - totalPoint).toString()
            }
        } else {
            try {
                clickOkListener!!.onOK()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        onRefreshListener = this.ct as OnRefreshListener
        onRefreshListener.onRefresh()
        dismiss()
    }

    private fun addOrder(product: Product) {
        val order = Order(product)
        order.updateUI(ct)
        Bill.Now.order_add(order)
        OrderManager.getInstance().addItem(order)
    }

    private fun getTastes() {
        val post = Post(ct, ApiRequest.PRODUCT_SERVICE_URL)
        post.addField("authkey", Config.authKey)
        post.addField("op", "get_taste")
        post.addField("prod_id", product.prod_id)
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
                            }
                            if (jaTaste.length() == 0) {
                                tvTitle!!.text = ct.resources.getString(R.string.noTasteOption)
                            }
                        } else {
                            tvTitle!!.text = ct.resources.getString(R.string.noTasteOption)
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

    @SuppressLint("SetTextI18n")
    fun updateUI() {
        try {
            for (taste in product.al_taste) {
                if (taste.view.parent != null) {
                    (taste.view.parent as ViewGroup).removeAllViews()
                }
                llTaste!!.addView(taste.view)
            }
            var pricePrefix = "";
            if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
                pricePrefix = ct.resources.getString(R.string.priceMoneyUnit)
            }else{
                pricePrefix = ct.resources.getString(R.string.pricePrefix)
            }
            tvCount!!.text = product.count.toString()
            tvUnitprice!!.text = pricePrefix + FormatUtil.removeDot(product.getSpecialPrice())

            // 判斷是否使用點數
            if (product.productVO.usePoint) {
                tvTotal!!.text = "+0 元"
                tvPoint!!.text = String.format(ct.getString(R.string.usedPoint), product.productVO.redeem_point.toInt() * product.count)
            } else {
                if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
                    tvTotal!!.text = pricePrefix + FormatUtil.removeDot(product.getTotalSpecialPrice())
                }else{
                    tvTotal!!.text = pricePrefix + FormatUtil.removeDot(product.getTotalSpecialPrice()) + " 元"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Kiosk.idleCount = Config.idleCount
        return super.dispatchTouchEvent(ev)
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val rContentview = if (product.comb_type == 2) R.layout.d_taste_prodincomb else R.layout.d_taste_single
        setContentView(rContentview)
        Kiosk.hidePopupBars(this)
        this.ct = context
        product._tasteDialog = this
        this.product = product
        setUI()
        setActions()
        if (this.product.selectedDetails_Get().size == 0 || this.product.comb_type == 0) {
            getTastes()
        } else {
            updateUI()
        }
    }

    fun showErrorDialog() {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment
                .setTitle(R.string.no_more_points_title)
                .setMessage(R.string.no_more_points_message)
                .setConfirmButton(View.OnClickListener {
                    alertDialogFragment.dismiss()
                })
                .show((ct as AppCompatActivity).fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }

    fun showNotEnoughDialog(productCountByCash: String, productCountByPoints: Int, redeemPoint: Int, productName: String) {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment
                .setTitle(R.string.not_enough_points_title)
                .setMessage(String.format(ct.getString(R.string.not_enough_points_message), productCountByCash, productName))
                .setConfirmButton(View.OnClickListener {
                    addItem(productCountByCash, productCountByPoints, redeemPoint)
                    alertDialogFragment.dismiss()
                })
                .setCancelButton(R.string.cancel, {
                    alertDialogFragment.dismiss()
                })
                .show((ct as AppCompatActivity).fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }
}
