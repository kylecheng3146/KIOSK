package com.lafresh.kiosk.activity

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.*
import com.lafresh.kiosk.Order
import com.lafresh.kiosk.Product
import com.lafresh.kiosk.adapter.ProductCategoryAdapter
import com.lafresh.kiosk.dialog.BackHomeDialog
import com.lafresh.kiosk.dialog.CashDialog
import com.lafresh.kiosk.dialog.PaidDialog
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.fragment.NumberKeyboardFragment
import com.lafresh.kiosk.fragment.RecommendationFragment
import com.lafresh.kiosk.httprequest.model.CreateOrderRes
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.manager.BasicSettingsManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.*
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.type.PaymentsType
import com.lafresh.kiosk.utils.*
import java.util.*
import kotlin.collections.ArrayList
import pl.droidsonroids.gif.GifImageButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.M)
class ShopActivity : BaseActivity(), View.OnClickListener, OnRefreshListener {
    private lateinit var order: com.lafresh.kiosk.model.Order
    private lateinit var rvProductCategory: RecyclerView
    private lateinit var adapter: ProductCategoryAdapter

    @JvmField
    var imageBtn_restart: ImageButton? = null
    var btn_restart: Button? = null
    var btn_addBuy: Button? = null
    var btn_unfold: Button? = null
    var btn_fold: Button? = null
    var gifBtnCheckout: GifImageButton? = null
    var btn_scan_barcode: Button? = null

    @JvmField
    var item_number: TextView? = null
    var tv_prodtitle: TextView? = null
    var tvPriceTitle: TextView? = null
    var TV_Price: TextView? = null

    @JvmField
    var ll_order: LinearLayout? = null
    var LL_ProdCate: LinearLayout? = null

    @JvmField
    var ll_orderMore: LinearLayout? = null
    var hsv_prodcate: HorizontalScrollView? = null

    @JvmField
    var gl_product: GridLayout? = null

    @JvmField
    var pgb: ProgressBar? = null
    var rl_unfold: RelativeLayout? = null
    var rvProduct: RecyclerView? = null

    var productCategoryFillter = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_shop)
        HomeActivity.now!!.activities.add(this)
        AL_DialogToClose = ArrayList()
        now = this
        setUI()
        setActions()
        HomeActivity.now!!.stopIdleProof()
        if (FlavorType.liangpin.name.equals(BuildConfig.FLAVOR)){
            CommonUtils.intentActivity(this, CheckOutOptionActivity::class.java)
        }
    }

    override fun setUI() {
        gifBtnCheckout = findViewById(R.id.gifBtnCheckout)
        hsv_prodcate = findViewById(R.id.hsv_prodcate)
        hsv_prodcate!!.bringToFront()
        LL_ProdCate = findViewById(R.id.LL_ProdCate)
        ll_order = findViewById(R.id.LL_Order)
        tvPriceTitle = findViewById(R.id.TV_Price_)
        val selectedOrderTitle = getString(R.string.selectedOrders)
        val totalTitle = getString(R.string.totalAmount)
        val pricePrefix = getString(R.string.pricePrefix)
        tvPriceTitle!!.setText("$selectedOrderTitle / $totalTitle  $pricePrefix ")
        item_number = findViewById(R.id.item_number)
        TV_Price = findViewById(R.id.TV_Price)
        tv_prodtitle = findViewById(R.id.tv_prodtitle)
        gl_product = findViewById(R.id.gl_product)
        pgb = findViewById(R.id.pgd_product)
        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
            TV_Price!!.setText("" + FormatUtil.removeDot(Bill.Now.PriceBeforeDiscount_Get()))
        }else{
            TV_Price!!.setText("" + Bill.Now.PriceBeforeDiscount_Get())
        }
        gl_product!!.removeAllViews()
        pgb!!.setVisibility(View.INVISIBLE)
        imageBtn_restart = findViewById(R.id.imageBtn_restart)
        btn_restart = findViewById(R.id.btn_restart)
        btn_addBuy = findViewById(R.id.btn_addBuy)
        btn_unfold = findViewById(R.id.btn_unfold)
        rl_unfold = findViewById(R.id.rl_unfold)
        rl_unfold!!.setVisibility(View.INVISIBLE)
        btn_fold = findViewById(R.id.btn_fold)
        ll_orderMore = findViewById(R.id.ll_orderMore)
        rvProduct = findViewById(R.id.rv_products)
        rvProductCategory = findViewById(R.id.rv_product_category)
        btn_scan_barcode = findViewById(R.id.btn_scan_barcode)
        initLogo()
        if (FlavorType.mwd.name == BuildConfig.FLAVOR) {
            val ivAd = findViewById<ImageView>(R.id.ivAd)
            Kiosk.checkAndChangeUi(this, Config.bannerImg, ivAd)
        }
        if (FlavorType.jourdeness.name == BuildConfig.FLAVOR) {
            btn_restart!!.setBackgroundResource(R.drawable.btn_main_pressed)
            btn_restart!!.setText(R.string.repurchase)
        }
    }

    fun setPointStatus(usedPoint: Int, remainedPoint: String) {
        var s = getString(R.string.pointStatus)
        s = String.format(s, usedPoint, remainedPoint)
        val remainedPointLen = remainedPoint.length
        val totalLen = s.length - 1
        val startIndex = totalLen - remainedPointLen
        val spannable = SpannableStringBuilder(s)
        val redColorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.red))
        spannable.setSpan(redColorSpan, startIndex, totalLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(AbsoluteSizeSpan(30), startIndex, totalLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val tvPoint = findViewById<TextView>(R.id.tvPoint)
        tvPoint.visibility = View.VISIBLE
        tvPoint.text = spannable
    }

    override fun setActions() {
        gifBtnCheckout!!.setOnClickListener(this)
        btn_addBuy!!.setOnClickListener(this)
        btn_unfold!!.setOnClickListener(this)
        btn_fold!!.setOnClickListener(this)
        imageBtn_restart!!.setOnClickListener(this)
        btn_restart!!.setOnClickListener(this)
        btn_scan_barcode!!.setOnClickListener(this)
        enableCheckoutButton(false)
    }

    fun getRecommendProductsApi() {
        Config.isAlreadyRecommended = true
        ProgressUtils.instance!!.showProgressDialog(this)
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).getRecommendProducts(Config.token, Config.shop_id)
            .enqueue(object : Callback<Products> {
                override fun onResponse(call: Call<Products>, response: Response<Products>) {
                    ProgressUtils.instance!!.hideProgressDialog()
                    if (response.isSuccessful) {
                        showRecommendationFragment(response.body()!!.products)
                    } else {
                        CommonUtils.intentActivity(this@ShopActivity, CheckOutOptionActivity::class.java)
                    }
                }

                override fun onFailure(call: Call<Products>, t: Throwable) {
                    ProgressUtils.instance!!.hideProgressDialog()
                    showRecommendErrorDialog()
                }
            })
    }

    fun showRecommendErrorDialog() {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment
            .setTitle(R.string.connectionError)
            .setMessage(R.string.connectionError_message)
            .setConfirmButton(View.OnClickListener {
                getRecommendProductsApi()
                alertDialogFragment.dismiss()
            })
            .setUnCancelAble()
            .setCancelButton(R.string.cancel) {
                if (!ClickUtil.isFastDoubleClick()) {
                    alertDialogFragment.dismiss()
                }
            }
            .show(this.fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }

    override fun onClick(v: View) {
        if (ClickUtil.isFastDoubleClick()) {
            return
        }
        when (v.id) {
            R.id.btn_scan_barcode -> {
                CommonUtils.intentActivity(this, CheckOutOptionActivity::class.java)
            }
            R.id.gifBtnCheckout -> {
                if (BuildConfig.FLAVOR == FlavorType.liangpin.name) {
                    createOrUpdateOrder()
                } else {
                    validateShowRecommendPConfig()
                }
            }
            R.id.imageBtn_restart -> BackHomeDialog(this@ShopActivity).show()
            R.id.btn_restart -> BackHomeDialog(this@ShopActivity).show()
            R.id.btn_addBuy -> {
            }
            R.id.btn_unfold -> {
                if (BuildConfig.FLAVOR == FlavorType.lanxin.name) {
                    validateShowRecommendPConfig()
                }else{
                    btn_unfold!!.visibility = View.GONE
                    rl_unfold!!.visibility = View.VISIBLE
                }
            }
            R.id.btn_fold -> {
                btn_unfold!!.visibility = View.VISIBLE
                rl_unfold!!.visibility = View.GONE
            }
            R.id.btn_arrow_r -> hsv_prodcate!!.postDelayed({ hsv_prodcate!!.smoothScrollBy(131, 0) }, 100)
            else -> {
            }
        }
    }

    private val orderDetail: Unit
        get() {
            val manager = OrderManager.getInstance()
            manager.getOrderDetail(object : OrderManager.OrderListener {
                override fun onSuccess(response: Response<*>) {
                    order = response.body() as com.lafresh.kiosk.model.Order
                    validateOrderNotEmpty(manager)
                }

                override fun onRetry() {
                    orderDetail
                }
            }, this)
        }

    fun validateOrderNotEmpty(manager: OrderManager) {
        if (order != null) {
            manager.total = order.charges.total.amount
            manager.discount = order.charges.discount.amount
            manager.totalFee = order.charges.total_fee.amount
            manager.setPoints(order.charges.points)
            val basicSettings = BasicSettingsManager.instance.getBasicSetting()
            //初次建立訂單時若門市基本設定預帶會員載具==true 帶入會員載具設定
            if (basicSettings?.use_member_carrier == true) {
            }
            updateUI()
            ProgressUtils.instance!!.hideProgressDialog()
            order.cart.items.forEach {
                if (it.ticket_no != null) {
                    val ticketNo = it.ticket_no
                    Bill.Now.AL_Order.forEach {
                        if (it.product.ticketNo == ticketNo)
                            return
                    }
                    var productVO = com.lafresh.kiosk.shoppingcart.model.Product()
                    val product = Product()
                    productVO.prod_id = it.id
                    productVO.prod_name1 = it.title
                    product.prod_name1 = it.title
                    productVO.price1 = it.price.toString()
                    productVO.redeem_point = "0"
                    productVO.usePoint = false
                    product.count = 1
                    product.ticketNo = it.ticket_no
                    product.productVO = productVO
                    product.price1 = it.price.total_price.amount
                    val order = Order(product)
                    order.isUseCoupon = true
                    Bill.Now.AL_Order.add(order)
                    updateUI()
                }
            }
            confirmOrder()
        } else {
            orderDetail
        }
    }

    fun createOrUpdateOrder() {
        ProgressUtils.instance!!.showProgressDialog(this)
        val manager = OrderManager.getInstance()
        val totalPrice = Bill.Now.PriceBeforeDiscount_Get()
        manager.createOrUpdateOrder(totalPrice, object : OrderManager.OrderListener {
            override fun onSuccess(response: Response<*>) {
                val version: String =
                    getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
                CommonUtils.setLog(
                    LogParams(
                        "kiosk",
                        Config.shop_id,
                        "order",
                        manager.orderId,
                        "KIOSK",
                        version,
                        "INFO",
                        manager.toString(),
                        response.body().toString()
                    )
                )
                ProgressUtils.instance!!.hideProgressDialog()
                val res = response.body() as CreateOrderRes?
                if (res != null) {
                    getOrderDetail(res)
                } else {
                    createOrUpdateOrder()
                }
            }

            private fun getOrderDetail(res: CreateOrderRes) {
                manager.orderId = res.id
                manager.orderTime = res.create_at
                orderDetail
            }

            override fun onRetry() {
                ProgressUtils.instance!!.hideProgressDialog()
                createOrUpdateOrder()
            }
        }, this)
    }

    fun checkCashTicketExist(manager: OrderManager, order: com.lafresh.kiosk.model.Order) {
        order.payments.forEach {
            if (it.type.equals(PaymentsType.CASH_TICKET.name)) {
                manager.addPayment(it)
            }
        }
    }

    fun confirmOrder() {
        // 判斷如果是智葳先不用判斷0元的條件
        val totalPrice = if (Config.isNewOrderApi) OrderManager.getInstance().totalFee else Bill.fromServer.total
        if (FlavorType.brogent.name != BuildConfig.FLAVOR) {
            if (OrderManager.getInstance().isLogin && totalPrice == 0.0) {
                ProgressUtils.instance!!.showProgressDialog(this)
                val manager = OrderManager.getInstance()
                checkCashTicketExist(manager, order)
                manager.confirmOrder(true, object : OrderManager.OrderListener {
                    override fun onSuccess(response: Response<*>?) {
                        val version: String =
                            getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
                        CommonUtils.setLog(
                            LogParams(
                                "kiosk",
                                Config.shop_id,
                                "order",
                                manager.orderId,
                                "KIOSK",
                                version,
                                "確認訂單成功",
                                "",
                                response!!.body().toString()
                            )
                        )
                        orderDetailAfterConfirm
                    }

                    override fun onRetry() {
                        ProgressUtils.instance!!.hideProgressDialog()
                        confirmOrder()
                    }
                }, this)
                return
            } else if (totalPrice == 0.0) {
                // 直接付款，為了可直接使用商品兌換券
//            PrintReceiptDialog dialog = new PrintReceiptDialog(Act_CheckOutOption.this);
//            dialog.show();

                // #6197 因兌換券功能未啟用，擋下金額為0 避免造成錯誤
                val errMsg = getString(R.string.totalIsZero)
                Kiosk.showMessageDialog(this, errMsg)
                enableCheckoutButton(false)
                return
            }
        }
        validateIsOnlyCounterPay()
    }

    fun validateIsOnlyCounterPay() {
        if (Config.isOnlyCounterPay) {
            CashDialog(this).show()
        } else {
            checkFlavorIntentFlow()
        }
    }

    fun checkFlavorIntentFlow() {
        when (BuildConfig.FLAVOR) {
            /*若為伽利略的版本，
            則先跳到填寫聯絡資訊頁面*/
            FlavorType.galileo.name -> {
                val numberKeyboardFragment = NumberKeyboardFragment()
                numberKeyboardFragment.setCancelable(false)
                numberKeyboardFragment.show(this.fragmentManager, "DIALOG")
            }
            else -> {
                CommonUtils.intentActivity(this, CheckOutActivity::class.java)
            }
        }
    }

    fun finishOrder(order: com.lafresh.kiosk.model.Order) {
        ProgressUtils.instance!!.hideProgressDialog()
        OrderManager.getInstance().printReceipt(this, order)
        PaidDialog(this, order).show()
    }

    private val orderDetailAfterConfirm: Unit
        get() {
            val manager = OrderManager.getInstance()!!
            manager.getOrderDetail(object : OrderManager.OrderListener {
                override fun onSuccess(response: Response<*>) {
                    val order = response.body() as com.lafresh.kiosk.model.Order?
                    if (order != null) {
                        manager.total = order.charges.total.amount
                        manager.discount = order.charges.discount.amount
                        manager.totalFee = order.charges.total_fee.amount
                        manager.setPoints(order.charges.points)
                        finishOrder(order)
                    } else {
                        orderDetailAfterConfirm
                    }
                }

                override fun onRetry() {
                    ProgressUtils.instance!!.hideProgressDialog()
                    orderDetailAfterConfirm
                }
            }, this)
        }

    fun validateShowRecommendPConfig() {
        if (BuildConfig.FLAVOR.equals(FlavorType.liangshehan.name)
                .and(!Config.isAlreadyRecommended)
        ) {
            getRecommendProductsApi()
        } else {
            CommonUtils.intentActivity(this, CheckOutOptionActivity::class.java)
        }
    }

    fun showRecommendationFragment(products: List<ProductBean>) {
        val recommendationFragment = RecommendationFragment()
        recommendationFragment.setCancelable(false)
        recommendationFragment.products = products
        recommendationFragment.show(this.fragmentManager, "DIALOG")
    }

    fun updateUI() {
        val totalPrice = FormatUtil.removeDot(Bill.Now.PriceBeforeDiscount_Get())
        val isOverLimit = ComputationUtils.parseInt(totalPrice.toString()) > Config.orderPriceLimit
        item_number!!.text = "${Bill.Now.AL_Order.size}"
        TV_Price!!.text = totalPrice
        enableCheckoutButton(Bill.Now.AL_Order.size > 0 && !isOverLimit)
        validateCartIsOverLimit(isOverLimit)
    }

    fun validateCartIsOverLimit(isOverLimit: Boolean) {
        if (isOverLimit) {
            Kiosk.showMessageDialog(this@ShopActivity, getString(R.string.overPriceLimit))
        }
    }

    fun enableCheckoutButton(b: Boolean) {
        if (b) {
            gifBtnCheckout!!.setBackgroundResource(R.drawable.gif_checkout)
            when (BuildConfig.FLAVOR) {
                FlavorType.FormosaChang.name, FlavorType.mwd.name -> {
                    gifBtnCheckout!!.setBackgroundResource(R.drawable.selector_btn_checkout)
                }
                FlavorType.fafathai.name -> {
                    gifBtnCheckout!!.setBackgroundResource(R.drawable.shop_select_checkout)
                }
                FlavorType.liangpin.name -> {
                    gifBtnCheckout!!.setBackgroundResource(R.drawable.shop_show_detail_btn)
                }
                FlavorType.lanxin.name -> {
                    btn_unfold!!.setBackgroundResource(R.drawable.shop_show_detail_btn)
                }
            }
        } else {
            gifBtnCheckout!!.setBackgroundResource(R.drawable.shop_checkout_disable)
            when (BuildConfig.FLAVOR) {
                FlavorType.FormosaChang.name, FlavorType.mwd.name, FlavorType.liangpin.name -> {
                    gifBtnCheckout!!.setBackgroundResource(R.drawable.btn_checkout_disable)
                }
                FlavorType.lanxin.name -> {
                    btn_unfold!!.setBackgroundResource(R.drawable.shop_show_detail_unclick_btn)
                }
            }
        }
        gifBtnCheckout!!.isEnabled = b
        when (BuildConfig.FLAVOR) {
            FlavorType.lanxin.name -> {
                btn_unfold!!.isEnabled = b
            }
        }
    }

    fun Finish() {
        for (dialog in AL_DialogToClose!!) {
            dialog.dismiss()
        }
        AL_DialogToClose!!.clear()
    }

    override fun onPause() {
        super.onPause()
        btn_unfold!!.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        Kiosk.hidePopupBars(this)
        btn_unfold!!.visibility = View.VISIBLE
        // 判斷登入狀態顯示紅利點數
        validateIsLogged()
        getProductCategory()
//        prodCate
    }

    override fun onDestroy() {
        ProductCategory.selected = null
        HomeActivity.now!!.activities.remove(this)
        AL_DialogToClose = null
        now = null
        super.onDestroy()
    }

    override fun onUserInteraction() {
        Kiosk.idleCount = Config.idleCount
    }

    fun getProductCategory() {
        HomeActivity.now!!.idleProof()
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).getProductCategory(
            GetProductCategoryParams(
                "get_prodcate00",
                Config.authKey,
                Config.shop_id
            )
        )
            .enqueue(object : Callback<GetProductCategory> {
                override fun onResponse(call: Call<GetProductCategory>, response: Response<GetProductCategory>) {
                    if (response.isSuccessful) {
                        Config.productImgPath = response.body()!!.imgpath
                        adapter = ProductCategoryAdapter(this@ShopActivity)
                        adapter.addProductView(rvProduct!!)
                        rvProductCategory.setLayoutManager(
                            LinearLayoutManager(
                                this@ShopActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        )
                        val productCategoryList = arrayListOf<Datum>()
                        if (response.body()?.data != null) {
                            productCategoryList.addAll(response.body()?.data!!)
                        }
//                        if (BuildConfig.FLAVOR == FlavorType.FormosaChang.name) {
//                            productCategoryFillter = if (OrderManager.getInstance().saleMethod.type==SaleType.DINE_IN.name) {
//                                arrayListOf("飯類", "菜類", "肉類", "湯類", "甜點類", "小菜類", "禮盒類")
//                            } else {
//                                arrayListOf("飯類", "菜類", "肉類", "湯類", "甜點類", "小菜類", "便當類", "禮盒類")
//                            }
//                            val iterator = productCategoryList.iterator()
//                            while (iterator.hasNext()) {
//                                val item = iterator.next()
//                                if (productCategoryFillter.indexOf(item.subject) == -1) {
//                                    iterator.remove()
//                                }
//                            }
//                        }
                        adapter.addItem(productCategoryList)
                        // 設置adapter給recycler_view
                        rvProductCategory.adapter = adapter
                    } else {
                        showErrorDialog()
                    }
                }

                override fun onFailure(call: Call<GetProductCategory>, t: Throwable) {
                    HomeActivity.now!!.idleProof()
                }
            })
    }

    fun showErrorDialog() {
        val alertDialogFragment = AlertDialogFragment()
        alertDialogFragment
            .setTitle(R.string.connectionError)
            .setMessage(R.string.connectionError_message)
            .setConfirmButton({
                getProductCategory()
                alertDialogFragment.dismiss()
            })
            .setUnCancelAble()
            .setCancelButton(R.string.cancel) {
                if (!ClickUtil.isFastDoubleClick()) {
                    alertDialogFragment.dismiss()
                }
            }
            .show(this.fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }

    companion object {
        @JvmField
        var AL_DialogToClose: ArrayList<Dialog>? = null

        @JvmField
        var now: ShopActivity? = null
    }

    override fun onRefresh() {
        validateIsLogged()
    }

    fun validateIsLogged() {
        if (OrderManager.getInstance().isLogin&& BasicSettingsManager.instance.getBasicSetting()!!.has_points) {
            setPointStatus(
                OrderManager.getInstance().member.appliedPoints,
                CommonUtils.removeDot(OrderManager.getInstance().member.points!!)
            )
        }
    }
}
