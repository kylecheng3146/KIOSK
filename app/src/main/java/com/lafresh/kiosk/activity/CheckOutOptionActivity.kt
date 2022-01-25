package com.lafresh.kiosk.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lafresh.kiosk.*
import com.lafresh.kiosk.dialog.BackHomeDialog
import com.lafresh.kiosk.dialog.CashDialog
import com.lafresh.kiosk.dialog.PaidDialog
import com.lafresh.kiosk.dialog.TicketDialog
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.fragment.NumberKeyboardFragment
import com.lafresh.kiosk.httprequest.AddOrderApiRequest
import com.lafresh.kiosk.httprequest.ApiRequest
import com.lafresh.kiosk.httprequest.ApiRequest.ApiListener
import com.lafresh.kiosk.httprequest.model.CreateOrderRes
import com.lafresh.kiosk.httprequest.model.OrderResponse
import com.lafresh.kiosk.manager.BasicSettingsManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.manager.OrderManager.OrderListener
import com.lafresh.kiosk.model.LogParams
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.type.PaymentsType
import com.lafresh.kiosk.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageButton
import retrofit2.Response
import java.util.*
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.M)
class CheckOutOptionActivity : BaseActivity(), View.OnClickListener {

    companion object {
        val barCodeMap = mutableMapOf<String, com.lafresh.kiosk.model.Product>()
    }

    private lateinit var order: com.lafresh.kiosk.model.Order
    private var tv_number: TextView? = null
    private var gifBtnConfirmOrder: GifImageButton? = null
    private var ib_restart: ImageButton? = null
    private var btnPointDiscount: Button? = null
    private var btnCouponDiscount: Button? = null
    private var btnCancel: Button? = null
    private var tvMsg: TextView? = null
    private var tvTotal: TextView? = null
    private var llOrder: LinearLayout? = null
    private var rlDiscount1: RelativeLayout? = null
    private var rlDiscount: RelativeLayout? = null
    private var rlTicketDiscount: RelativeLayout? = null
    private var rlOriginPrice: RelativeLayout? = null
    private var tvDiscount: TextView? = null
    private var tvTicketDiscount: TextView? = null
    private var tvOriginPrice: TextView? = null
    private var btnRemoveTicket: Button? = null
    private var progressDialog: ProgressDialog? = null
    private var clBarcode: ConstraintLayout? = null
    private var etBarcode: EditText? = null
    private var btnNoBarcode: Button? = null
    private var btnRestart: Button? =null
    var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_checkoutoption)
        HomeActivity.now!!.activities.add(this)
        Kiosk.hidePopupBars(this)
        setUI()
        setActions()
    }

    override fun onResume() {
        super.onResume()
        validateIsNewApiFlow()
    }

    fun validateIsNewApiFlow() {
        if (Config.isNewOrderApi) {
            checkOrderByTickets()
            if (BuildConfig.FLAVOR!=FlavorType.liangpin.name||OrderManager.getInstance().hasCartItem()){
                createOrUpdateOrder(false)
            }else{
                enableConfirmButton(false)
                tvTotal!!.text = "${getText(R.string.pricePrefix)}0"
                tvMsg!!.visibility = View.INVISIBLE
            }
            // 判斷是否為新API流程顯示優惠券按鈕
            checkCouponButtonVisibility()
        } else {
            getOrdersFromServer(Bill.fromServer.worder_id)
        }
    }

    fun checkCouponButtonVisibility() {
        if (Config.isNewOrderApi) {
            btnCouponDiscount!!.visibility = if (OrderManager.getInstance().isLogin) View.VISIBLE else View.INVISIBLE
            rlDiscount1!!.visibility = if (OrderManager.getInstance().isLogin) View.VISIBLE else View.GONE
        } else {
            btnCouponDiscount!!.visibility = if (Bill.Now.isMember) View.VISIBLE else View.INVISIBLE
            rlDiscount1!!.visibility = if (Bill.Now.isMember) View.VISIBLE else View.GONE
        }
        if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name)) {
            rlDiscount1!!.visibility = View.GONE
        }
    }

    fun checkOrderByTickets() {

        if (Bill.Now.AL_Order != null && Bill.Now.AL_Order.size > 0) {
            val iterator: MutableIterator<Order> = Bill.Now.AL_Order.iterator()
            while (iterator.hasNext()) {
                val value = iterator.next()
                if (value.product.ticketNo != null) {
                    iterator.remove()
                }
            }
        }
    }

    override fun setUI() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        tvMsg = findViewById(R.id.tv_msg)
        tvTotal = findViewById(R.id.tv_total)
        llOrder = findViewById(R.id.ll_order)
        btnPointDiscount = findViewById(R.id.Btn_PointDiscount)
        btnCouponDiscount = findViewById(R.id.Btn_CouponDiscount)
        rlDiscount1 = findViewById(R.id.rl_discount)
        rlDiscount = findViewById(R.id.rlDiscount)
        rlTicketDiscount = findViewById(R.id.rlTicketDiscount)
        tvDiscount = findViewById(R.id.tvDiscount)
        tvTicketDiscount = findViewById(R.id.tvTicketDiscount)
        tvOriginPrice = findViewById(R.id.tvOriginPrice)
        btnRemoveTicket = findViewById(R.id.btnRemoveTicket)
        rlOriginPrice = findViewById(R.id.rlOriginPrice)
        gifBtnConfirmOrder = findViewById(R.id.gifBtnConfirmOrder)
        ib_restart = findViewById(R.id.ib_restart)
        tv_number = findViewById(R.id.tv_number)
        progressDialog = ProgressDialog(this)
        gifBtnConfirmOrder!!.requestFocus()
        clBarcode = findViewById(R.id.cl_barcode)
        etBarcode = findViewById(R.id.et_barcode)
        btnNoBarcode = findViewById(R.id.btn_no_barcode)
        btnRestart = findViewById(R.id.btn_restart)
        btnCancel = findViewById(R.id.btn_cancel)
        tv_number!!.text = "${Bill.Now.AL_Order.size}"
        gifBtnConfirmOrder!!.visibility = View.INVISIBLE
        if (BuildConfig.FLAVOR==FlavorType.liangpin.name){
            clBarcode!!.visibility=View.VISIBLE
            btnNoBarcode!!.visibility=View.VISIBLE
            etBarcode!!.requestFocus()
            gifBtnConfirmOrder!!.visibility = View.VISIBLE
            btnCancel!!.visibility=View.GONE
            btnRestart!!.visibility=View.GONE
        }else{
            clBarcode!!.visibility=View.GONE
            btnNoBarcode!!.visibility=View.GONE
        }
        llOrder!!.removeAllViews()
        replaceCheckoutButtonBackground()
        initLogo()
    }

    /**
     * 依照不同更換按鈕樣式
     * */
    fun replaceCheckoutButtonBackground() {
        when (BuildConfig.FLAVOR) {
            FlavorType.mwd.name -> {
                gifBtnConfirmOrder!!.setBackgroundResource(R.drawable.selector_btn_confirm_order)
            }

            else -> {
            }
        }
    }

    override fun setActions() {
        gifBtnConfirmOrder!!.setOnClickListener(this)
        ib_restart!!.setOnClickListener(this)
        btnCancel!!.setOnClickListener(this)
        btnCouponDiscount!!.setOnClickListener(this)
        findViewById<View>(R.id.btn_restart).setOnClickListener(this)
        btnRemoveTicket!!.setOnClickListener(this)
        btnNoBarcode!!.setOnClickListener(this)
        etBarcode!!.setOnClickListener(this)
        etBarcode!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (start == 0 && after == 1) {
                    countDownTimer = object : CountDownTimer(200, 200) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            CommonUtils.hideSoftKeyboard(this@CheckOutOptionActivity)
                            val code = etBarcode!!.text.toString().trim { it <= ' ' }
                            GlobalScope.launch(Dispatchers.Main) {
                                delay(300)
                                etBarcode!!.setText("")
                                etBarcode!!.requestFocus()
                            }
                            addOrder(barCodeMap[code])
                        }
                    }.start()
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun addOrder(productData: com.lafresh.kiosk.model.Product?) {
        if (productData != null) {
            val product = Product()
            product.set_byjo(CommonUtils.convertObjectToJson(productData))
            val order = Order(product)
            order.updateUI(this)
            Bill.Now.order_add(order)
            OrderManager.getInstance().addItem(order)
            val pricePrefix = getString(R.string.pricePrefix)
            updateUI()
            tvTotal!!.text = pricePrefix + Bill.Now.PriceBeforeDiscount_Get().toInt()
            enableConfirmButton(OrderManager.getInstance().hasCartItem())
        } else {
            showScanErrorDialog()
            GlobalScope.launch(Dispatchers.Main) {
                delay(500)
                tvMsg!!.visibility = View.GONE
            }
        }
    }

    fun showScanErrorDialog() {
        val alertDialogFragment = AlertDialogFragment()
        alertDialogFragment
            .setTitle(R.string.scanBarCodeError)
            .setMessage(R.string.noSuchProduct)
            .setConfirmButton {
                alertDialogFragment.dismiss()
            }
            .setUnCancelAble()
            .show(this.fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }

    override fun onClick(v: View) {
        if (ClickUtil.isFastDoubleClick()) {
            return
        }
        when (v.id) {
            R.id.et_barcode-> {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(this.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
            R.id.btn_no_barcode -> finish()
            R.id.gifBtnConfirmOrder -> {
                createOrUpdateOrder(true)
            }
            R.id.ib_restart -> finish()
            R.id.btn_cancel -> finish()
            R.id.btn_restart -> BackHomeDialog(this@CheckOutOptionActivity).show()
            R.id.Btn_CouponDiscount -> {
                if (Config.isNewOrderApi) {
                    CommonUtils.intentActivity(this, CouponActivity::class.java)
                } else {
                    showTicketDialog()
                }
            }
            R.id.btnRemoveTicket -> {
                Bill.Now.al_ticketSelected.clear()
                rlTicketDiscount!!.visibility = View.GONE
                Bill.Now.setTicket_weborder02()
                getOrdersFromServer(Bill.fromServer.worder_id)
            }
            else -> {
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
                manager.confirmOrder(true, object : OrderListener {
                    override fun onSuccess(response: Response<*>?) {
                        val version: String = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
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
                Kiosk.showMessageDialog(this@CheckOutOptionActivity, errMsg)
                enableConfirmButton(false)
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

    fun checkCashTicketExist(manager: OrderManager, order: com.lafresh.kiosk.model.Order) {
        order.payments.forEach {
            if (it.type.equals(PaymentsType.CASH_TICKET.name)) {
                manager.addPayment(it)
            }
        }
    }

    private val orderDetailAfterConfirm: Unit
        get() {
            val manager = OrderManager.getInstance()!!
            manager.getOrderDetail(object : OrderListener {
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

    fun finishOrder(order: com.lafresh.kiosk.model.Order) {
        ProgressUtils.instance!!.hideProgressDialog()
        OrderManager.getInstance().printReceipt(this, order)
        PaidDialog(this, order).show()
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
                if(BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name)) {
                    CommonUtils.intentActivity(this, InvoiceActivity::class.java)
                }else{
                    CommonUtils.intentActivity(this, CheckOutActivity::class.java)
                }
            }
        }
    }

    fun showTicketDialog() {
        val d_ticket =
            TicketDialog(this@CheckOutOptionActivity)
        d_ticket.show()
        d_ticket.lsn = TicketDialog.LSN {
            var hasRedeemTicket = false

            // remove repeat product about exchange ticket
            val al_orderToRemove = ArrayList<Order>()
            for (ticket in Bill.Now.al_ticketSelected) {
                if (ticket.KindType == Ticket.PRODUCT_TYPE) {
                    hasRedeemTicket = true
                    for (order in Bill.Now.AL_Order) {
                        if (order.product.prod_id == ticket.ProdID) {
                            al_orderToRemove.add(order)
                        }
                    }
                }
            }
            for (order in al_orderToRemove) {
                Bill.Now.AL_Order.remove(order)
            }
            Bill.Now.setTicket_weborder02() // set discount json
            d_ticket.dismiss()
            // 無選擇商品也無兌換商品則不連API
            validateCartNotEmpty(hasRedeemTicket)
        }
    }

    fun validateCartNotEmpty(hasRedeemTicket: Boolean) {
        if (Bill.Now.AL_Order.size > 0 || hasRedeemTicket) {
            getOrdersFromServer(Bill.fromServer.worder_id)
        } else {
            enableConfirmButton(false)
            Bill.Now.al_ticketSelected.clear()
            Bill.fromServer.orderResponse.weborder02 = null
            Bill.fromServer.orderResponse.discount = 0
            updateUI()
        }
    }

    fun enableConfirmButton(b: Boolean) {
        if (b) {
            gifBtnConfirmOrder!!.setOnClickListener(this)
            gifBtnConfirmOrder!!.setBackgroundResource(R.drawable.gif_confirm_order)
            when (BuildConfig.FLAVOR) {
                FlavorType.FormosaChang.name, FlavorType.mwd.name -> {
                    gifBtnConfirmOrder!!.setBackgroundResource(R.drawable.selector_btn_confirm_order)
                }
                FlavorType.liangpin.name->{
                    gifBtnConfirmOrder!!.setBackgroundResource(R.drawable.btn_checkout)
                }
            }
        } else {
            gifBtnConfirmOrder!!.setOnClickListener(null)
            gifBtnConfirmOrder!!.setBackgroundResource(R.drawable.checkout_disabled)
            when (BuildConfig.FLAVOR) {
                FlavorType.FormosaChang.name, FlavorType.mwd.name -> {
                    gifBtnConfirmOrder!!.setBackgroundResource(R.drawable.btn_confirm_order_disable)
                }
                FlavorType.liangpin.name ->{
                    gifBtnConfirmOrder!!.setBackgroundResource(R.drawable.btn_checkout_disable)
                }
            }
        }
    }

    fun createOrUpdateOrder(confirmFlag: Boolean) {
        ProgressUtils.instance!!.showProgressDialog(this)
        val manager = OrderManager.getInstance()
        val totalPrice = Bill.Now.PriceBeforeDiscount_Get()
        manager.createOrUpdateOrder(totalPrice, object : OrderListener {
            override fun onSuccess(response: Response<*>) {
                val version: String = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
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
                    getOrderDetail(res,confirmFlag)
                } else {
                    createOrUpdateOrder(confirmFlag)
                }
            }

            private fun getOrderDetail(res: CreateOrderRes,confirmFlag: Boolean) {
                manager.orderId = res.id
                manager.orderTime = res.create_at
                orderDetail(confirmFlag)
            }

            override fun onRetry() {
                ProgressUtils.instance!!.hideProgressDialog()
                createOrUpdateOrder(confirmFlag)
            }
        }, this)
    }

    private fun orderDetail(confirmFlag: Boolean){
            val manager = OrderManager.getInstance()
            manager.getOrderDetail(object : OrderListener {
                override fun onSuccess(response: Response<*>) {
                    order = response.body() as com.lafresh.kiosk.model.Order
                    validateOrderNotEmpty(manager,confirmFlag)
                }

                override fun onRetry() {
                    orderDetail(confirmFlag)
                }
            }, this)
        }

    fun validateOrderNotEmpty(manager: OrderManager,confirmFlag: Boolean) {
        if (order != null) {
            manager.total = order.charges.total.amount
            manager.discount = order.charges.discount.amount
            manager.totalFee = order.charges.total_fee.amount
            manager.setPoints(order.charges.points)
            val basicSettings = BasicSettingsManager.instance.getBasicSetting()
            // 初次建立訂單時若門市基本設定預帶會員載具==true 帶入會員載具設定
            if (basicSettings?.use_member_carrier == true) {
            }
            updateUI()
            tvMsg!!.visibility = View.INVISIBLE
            gifBtnConfirmOrder!!.visibility = View.VISIBLE
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
            if (confirmFlag){
                confirmOrder()
            }
        } else {
            orderDetail(confirmFlag)
        }
    }

    /**
     * @param orderId 空字串會取新的worderId,傳入則會沿用。
     */
    fun getOrdersFromServer(orderId: String?) = try {
        val joWeborder01 = JSONObject()
        val joWeborder011 = JSONObject()
        val joWeborder021 = JSONObject()
        val jaWeborder01 = JSONArray()
        val jaWeborder011 = JSONArray()
        for (i in Bill.Now.AL_Order.indices) {
            val product = Bill.Now.AL_Order[i].product
            // construct the weborder01
            product.worder_sno = jaWeborder01.length() + 1
            // 新增comb_sale_sno 欄位 套餐主項第一個為null
            val joDataWeborder01 = Weborder01.setJObyProduct(product, product.iscomb, null)
            jaWeborder01.put(joDataWeborder01)
            validateTypeIsCombo(product, jaWeborder01)
            // construct the weborder011
            val products = ArrayList<Product>()
            if (product.comb_type == 0) {
                products.add(product)
            }
            if (product.comb_type == 1) {
                for (combItem in product.al_combItem) {
                    products.add(combItem.productSelected!!)
                }
            }
            for (product_ in products) {
                for (taste in product_.al_taste) {
                    for (detail in taste.details) {
                        if (detail.selected) {
                            val joDataWeborder011 = JSONObject()
                            joDataWeborder011.put("worder_sno", product_.worder_sno)
                            joDataWeborder011.put("taste_id", taste.tasteId.toString() + "")
                            joDataWeborder011.put("taste_sno", detail.tasteSno.toString() + "")
                            joDataWeborder011.put("name", detail.name)
                            joDataWeborder011.put("price", detail.price.toString() + "")
                            jaWeborder011.put(joDataWeborder011)
                        }
                    }
                }
            }
        }
        joWeborder01.put("data", jaWeborder01)
        joWeborder011.put("data", jaWeborder011)
        val joDataEmpty = JSONObject()
        joWeborder021.put("data", joDataEmpty)
        Bill.Now.addTicketWebOrder021(Bill.Now.webOrder021Array)
        val addOrderApiRequest = AddOrderApiRequest(orderId, "0", "0",
                if (BuildConfig.FLAVOR.equals(FlavorType.TFG.name)) Config.mealDate else Bill.getTime_forSubmit(), joWeborder01.toString(), joWeborder011.toString(),
                JSONObject().put("data", Bill.Now.ja_data_wo02).toString(),
                JSONObject().put("data", Bill.Now.webOrder021Array).toString(), Bill.Now.getjo_ticket().toString())
        addOrder(addOrderApiRequest)
    } catch (e: Exception) {
        e.printStackTrace()
        FirebaseCrashlytics.getInstance().recordException(e)
    }

    fun validateTypeIsCombo(product: Product, jaWeborder01: JSONArray) {
        if (product.iscomb == 1) {
            for (j in product.al_combItem.indices) {
                val subProduct = product.al_combItem[j].productSelected
                // 套餐子項數量  跟主項一樣
                subProduct!!.count = product.count
                subProduct!!.worder_sno = jaWeborder01.length() + 1
                // 新增comb_sale_sno 欄位 套餐子項為主項的worder_sno
                val joData =
                    Weborder01.setJObyProduct(subProduct, 2, product.worder_sno.toString())
                jaWeborder01.put(joData)
            }
        }
    }

    private fun addOrder(addOrderApiRequest: AddOrderApiRequest) {
        ProgressUtils.instance!!.showProgressDialog(this)
        addOrderApiRequest.setApiListener(addOrderListener()).setRetry(3).go()
    }

    private fun addOrderListener(): ApiListener {
        return object : ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {
                runOnUiThread {
                    ProgressUtils.instance!!.hideProgressDialog()
                    val orderResponse = Json.fromJson(body, OrderResponse::class.java)
                    validateAddOrderIsSuccess(orderResponse)
                }
            }

            override fun onFail() {
                runOnUiThread {
                    ProgressUtils.instance!!.hideProgressDialog()
                    tvTotal!!.text = getString(R.string.connectionError)
                    val alertDialogFragment =
                        AlertDialogFragment()
                    alertDialogFragment.setTitle(R.string.connectionError)
                            .setMessage(R.string.tryLater)
                            .setConfirmButton(R.string.retry) {
                                if (!ClickUtil.isFastDoubleClick()) {
                                    alertDialogFragment.dismiss()
                                    getOrdersFromServer(Bill.fromServer.worder_id)
                                }
                            }
                            .setCancelButton(R.string.returnHomeButton) {
                                if (!ClickUtil.isFastDoubleClick()) {
                                    BackHomeDialog(this@CheckOutOptionActivity).show()
                                }
                            }
                            .setUnCancelAble()
                            .show(fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
                }
            }
        }
    }

    fun validateAddOrderIsSuccess(orderResponse: OrderResponse) {
        if (orderResponse.code == 0) {
            // 新增orderId，讓Firebase Log可以查詢。此ID會以最後設定的覆蓋
            FirebaseCrashlytics.getInstance().setUserId(orderResponse.worder_id)
            billUpdate(orderResponse)
            tvMsg!!.visibility = View.INVISIBLE
            gifBtnConfirmOrder!!.visibility = View.VISIBLE
        } else {
            val msg = orderResponse.message
            val errorShow = getString(R.string.errorOccur) + msg
            tvTotal!!.text = msg
            tvMsg!!.text = errorShow
        }
    }

    fun billUpdate(orderResponse: OrderResponse) {
        try {
            Bill.fromServer = Bill()
            Bill.fromServer.worder_id = orderResponse.worder_id
            Bill.fromServer.total = orderResponse.total
            Bill.fromServer.orderResponse = orderResponse
            Bill.fromServer.AL_Order = Bill.Now.AL_Order
            updateUI()
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateUI() {
        llOrder!!.removeAllViews()
        for (order in Bill.Now.AL_Order) {
            order.updateCheckoutOptionsUI(this)
            order.onRemoveListener = Order.OnRemoveListener {
                // 判斷是否使用票券
                validateOrderIsUseCoupon(order)

                // 有折價券不可清空購物車,檢查剩餘一個產品
//                if (productLeftInCart() == 1) {
//                    if(!order.isUseCoupon && hasTicket()){
//                        // do nothing
//                        Kiosk.showMessageDialog(this@CheckOutOptionActivity, getString(R.string.removeCouponFirst))
//                        return@LSN_CCO
//                    }
//                }

                val price =
                    if (order.me.product.spe_price > 0.0) (order.me.product.spe_price * order.me.product.count) else (order.me.product.price1 * order.me.product.count)
                if (validateTicketUsable(price, order)) return@OnRemoveListener
                Bill.Now.order_remove(order.me)
                OrderManager.getInstance().removeItem(order.me)
//                val order_Shop = Bill.Now.getOrderBy_worder_sno(order.product.worder_sno)
//                Bill.Now.order_remove(order_Shop)
//                OrderManager.getInstance().removeItem(order_Shop)
                if (Bill.Now.AL_Order.size > 0 || hasTicket()) {
                    if (Config.isNewOrderApi) {
                        createOrUpdateOrder(false)
                    } else {
                        getOrdersFromServer(Bill.fromServer.worder_id)
                    }
                } else {
                    llOrder!!.removeView(order.v_checkOutOptions)
                    rlDiscount!!.visibility = View.GONE
                    rlOriginPrice!!.visibility = View.GONE
                    val pricePrefix = getString(R.string.pricePrefix)
                    tvTotal!!.text = pricePrefix + "0"
                    tv_number!!.text = "${Bill.Now.AL_Order.size}"
                    enableConfirmButton(false)

                    // 檢查有無使用兌換券
                    if (hasTicket()) {
                        enableConfirmButton(true)
                    }
                }
            }
            llOrder!!.addView(order.v_checkOutOptions)
        }
        val totalPrice: Int
        if (Config.isNewOrderApi) {
            // todo 新訂單流程 尚未有票券流程
            totalPrice = OrderManager.getInstance().totalFee.toInt()
            var ticketDiscount = 0
            OrderManager.getInstance().tickets.forEach {
                ticketDiscount += it.price
            }
            if (OrderManager.getInstance().total > OrderManager.getInstance().totalFee && hasDisCountTicket()) {
                val pricePrefix = getString(R.string.pricePrefix)
                rlDiscount!!.visibility = View.VISIBLE
                tvDiscount!!.text = pricePrefix + ticketDiscount.toString()
                rlOriginPrice!!.visibility = View.VISIBLE
                val originPrice = OrderManager.getInstance().total - OrderManager.getInstance().discount
                tvOriginPrice!!.text = pricePrefix + originPrice
                tvOriginPrice!!.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
                if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name)) {
                    rlDiscount1!!.visibility = View.GONE
                }
            } else {
                rlDiscount!!.visibility = View.GONE
                rlOriginPrice!!.visibility = View.GONE
            }
        } else {
            // 票券
            for (ticket in Bill.Now.al_ticketSelected) {
                ticket.updateUI(this)
                llOrder!!.addView(ticket.view)
                if (ticket.KindType == "6") {
                    enableConfirmButton(true)
                }
            }
            val pricePrefix = getString(R.string.pricePrefix)
            totalPrice = Bill.fromServer.total
            val orderResponse = Bill.fromServer.orderResponse
            if (orderResponse.discount != 0) { /*折價紅框*/
                rlDiscount!!.visibility = View.VISIBLE
                tvDiscount!!.text = pricePrefix + orderResponse.discount
                rlOriginPrice!!.visibility = View.VISIBLE
                val originPrice = abs(orderResponse.discount) + orderResponse.tot_sales
                tvOriginPrice!!.text = pricePrefix + originPrice
                tvOriginPrice!!.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                rlDiscount!!.visibility = View.GONE
                rlOriginPrice!!.visibility = View.GONE
            }

            // 票券紅框
            if (orderResponse.weborder02 != null && orderResponse.weborder02.size > 0) {
                var ticketDiscount = Bill.Now.ticketDiscount
                ticketDiscount = "-" + FormatUtil.removeDot(ticketDiscount)
                rlTicketDiscount!!.visibility = View.VISIBLE
                tvTicketDiscount!!.text = pricePrefix + ticketDiscount
            } else {
                rlTicketDiscount!!.visibility = View.GONE
            }
        }
        val pricePrefix = getString(R.string.pricePrefix)
        val priceMoneyUnit = getString(R.string.priceMoneyUnit)
        var total = ""
        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
            total = priceMoneyUnit + FormatUtil.removeDot(totalPrice.toDouble()).toInt()
        }else{
            total = pricePrefix + totalPrice
        }
        tvTotal!!.text = total

        tv_number!!.text = "${Bill.Now.AL_Order.size}"
    }

    fun validateTicketUsable(
        price: Double,
        order: Order
    ): Boolean {
        if (OrderManager.getInstance().totalFee < price && !order.me.isUsePoint && hasDisCountTicket()) {
            Kiosk.showMessageDialog(
                this@CheckOutOptionActivity,
                getString(R.string.removeDiscountFirst)
            )
            return true
        }
        return false
    }

    fun validateOrderIsUseCoupon(order: Order) {
        if (order.me.isUseCoupon) {
            // 新建一個obj來移除tickets
            val ticketsBean = com.lafresh.kiosk.model.Order.TicketsBean()
            ticketsBean.number = order.me.product.ticketNo
            OrderManager.getInstance().removeTickets(ticketsBean)
        }
    }

    fun hasTicket(): Boolean {
        for (order in Bill.Now.AL_Order) {
            if (order.isUseCoupon) {
                return true
            }
        }
        return false
    }

    fun hasDisCountTicket(): Boolean {
        for (ticket in OrderManager.getInstance().tickets) {
            if (ticket.price > 0) {
                return true
            }
        }
        return false
    }

    fun productLeftInCart(): Int {
        var count = 0
        for (order in Bill.Now.AL_Order) {
            if (!order.isUseCoupon) {
                count ++
            }
        }
        return count
    }

    override fun onUserInteraction() {
        Kiosk.idleCount = Config.idleCount
    }

    public override fun onDestroy() {
        HomeActivity.now!!.activities.remove(this)
        super.onDestroy()
    }
}
