package com.lafresh.kiosk.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lafresh.kiosk.*
import com.lafresh.kiosk.dialog.NoInvoiceDialog
import com.lafresh.kiosk.dialog.PaidDialog
import com.lafresh.kiosk.easycard.EasyCard
import com.lafresh.kiosk.easycard.EasyCardPay
import com.lafresh.kiosk.easycard.EcResTag
import com.lafresh.kiosk.easycard.ErrorCode
import com.lafresh.kiosk.easycard.model.EcPayData
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.httprequest.model.CardInfo
import com.lafresh.kiosk.httprequest.model.OrderResponse
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.manager.EasyCardManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.manager.OrderManager.OrderListener
import com.lafresh.kiosk.model.LogParams
import com.lafresh.kiosk.model.Order
import com.lafresh.kiosk.model.Payment
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.type.EasyCardTransactionType
import com.lafresh.kiosk.type.PaymentsType
import com.lafresh.kiosk.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.M)
class EasyCardActivity : BaseActivity(), View.OnClickListener {

    private var rlQnA: RelativeLayout? = null
    private var btnBack: Button? = null
    private var btnYes: Button? = null
    private var tvQuestion: TextView? = null
    private var tvTitle: TextView? = null
    private var tvMessage: TextView? = null
    private var easyCardPay: EasyCardPay? = null
    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_easycard)
        HomeActivity.now!!.activities.add(this)
        easyCardPay = EasyCardPay(this)
        orderId = if (Config.isNewOrderApi) OrderManager.getInstance().orderId else Bill.fromServer.worder_id
        setUI()
        setActions()
        startEzCard()
        setAllBtnVisibility(false)
    }

    override fun setUI() {
        rlQnA = findViewById(R.id.rlQnA)
        tvQuestion = findViewById(R.id.tvQuestion)
        btnYes = findViewById(R.id.btnYes)
        btnBack = findViewById(R.id.btn_back)
        tvTitle = findViewById(R.id.tvTitle)
        tvMessage = findViewById(R.id.tvMessage)
        initLogo()
    }

    override fun setActions() {
        btnYes!!.setOnClickListener(this)
        btnBack!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        Kiosk.hidePopupBars(this)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    public override fun onDestroy() {
        if (easyCardPay != null) {
            easyCardPay!!.release()
            easyCardPay = null
        }
        HomeActivity.now!!.activities.remove(this)
        super.onDestroy()
    }

    override fun onUserInteraction() {
        Kiosk.idleCount = Config.idleCount
    }

    override fun onClick(v: View) {
        if (ClickUtil.isFastDoubleClick()) {
            return
        }
        when (v.id) {
            R.id.btnYes -> {
                if (Config.easyCardTransactionType != EasyCardTransactionType.LOGIN) {
                    Config.easyCardTransactionType = EasyCardTransactionType.RETRY
                }
                startEzCard()
                setAllBtnVisibility(false)
            }
            R.id.btn_back -> finish()
            else -> {
            }
        }
    }

    private fun startEzCard() {
        val price = if (Config.isNewOrderApi) FormatUtil.removeDot(OrderManager.getInstance().totalFee) else Bill.fromServer.total.toString()
        when (Config.easyCardTransactionType) {
            EasyCardTransactionType.PAY -> {
                easyCardPay!!.setListener(payListener)
                easyCardPay!!.deduct(price)
            }
            EasyCardTransactionType.LOGIN -> {
                easyCardPay!!.setListener(cardNumberListener)
                easyCardPay!!.checkPayment()
            }
            EasyCardTransactionType.RETRY -> {
                val retryTime = EasyCardManager.easyCardTryTime[orderId]!!
                EasyCardManager.easyCardTryTime[orderId] = retryTime + 1
                easyCardPay!!.retryApi(price)
            }
            else -> {}
        }
    }

    // 檢查是否有自動加值
    private val payListener: EasyCardPay.Listener
        get() = object : EasyCardPay.Listener {
            override fun onResult(result: String) {
                val logMsg = """悠遊卡扣款
                     authKey:${Config.authKey}
                    shopId:${Config.shop_id}
                    kioskId:${Config.kiosk_id}
                    worderId:${Bill.fromServer.worder_id}
                    悠遊卡卡機回傳:$result"""
                LogUtil.writeLogToLocalFile(logMsg)
                val t3901 = easyCardPay!!.findTag(result, EcResTag.T3901)
                val t3904 = easyCardPay!!.findTag(result, EcResTag.T3904)
                var message = ErrorCode.getMessage(t3901, t3904, true)
                var status = ErrorCode.getStatus(t3901, t3904)
                val easyCardRetryTime = EasyCardManager.easyCardTryTime[orderId]
                if (status == ErrorCode.NEED_RETRY && easyCardRetryTime!! > 2) {
                    status = ErrorCode.FAIL
                    message = getString(R.string.ezCardRetryToLimit)
                }
                when (status) {
                    ErrorCode.SUCCESS -> {
                        val ecPayData = EasyCard.parsePayData(result, easyCardPay)
                        if (Config.isNewOrderApi) {
                            val manager = OrderManager.getInstance()
                            val version: String = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
                            CommonUtils.setLog(
                                LogParams(
                                    "kiosk",
                                    Config.shop_id,
                                    "order",
                                    manager.orderId,
                                    "KIOSK",
                                    version,
                                    "悠遊卡付款成功",
                                    "",
                                    logMsg
                                )
                            )
                            addEzCardPayToServer(ecPayData)
                            addPayment(ecPayData)
                            writeLostOrderInfoWhenOffline()
                            orderDetail
                        } else {
                            addEzCardPayToServer(ecPayData)
                            Bill.fromServer.ecPayData = ecPayData
                            submitBill()
                        }
                        easyCardPay!!.release()
                    }
                    ErrorCode.BROKEN -> {
                        tvTitle!!.text = getString(R.string.ezCardPayErrorTitle)
                        tvTitle!!.visibility = View.VISIBLE
                        btnBack!!.visibility = View.VISIBLE
                        CommonUtils.setLog(
                            LogParams(
                                "kiosk",
                                Config.shop_id,
                                "order",
                                OrderManager.getInstance().orderId,
                                "KIOSK",
                                getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")",
                                "悠遊卡交易失敗",
                                "",
                                logMsg
                            )
                        )
                    }
                    ErrorCode.NEED_RETRY -> {
                        setAllBtnVisibility(true)
                        checkAutoDeposit(result, easyCardPay)
                    }
                    ErrorCode.FAIL -> {
                        // 檢查是否有自動加值
                        tvTitle!!.text = getString(R.string.ezCardPayErrorTitle)
                        tvTitle!!.visibility = View.VISIBLE
                        checkAutoDeposit(result, easyCardPay)
                        btnBack!!.visibility = View.VISIBLE
                        CommonUtils.setLog(
                            LogParams(
                                "kiosk",
                                Config.shop_id,
                                "order",
                                OrderManager.getInstance().orderId,
                                "KIOSK",
                                getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")",
                                "悠遊卡交易失敗",
                                "",
                                logMsg
                            )
                        )
                    }
                    else -> {
                        tvTitle!!.text = getString(R.string.ezCardPayErrorTitle)
                        tvTitle!!.visibility = View.VISIBLE
                        checkAutoDeposit(result, easyCardPay)
                        btnBack!!.visibility = View.VISIBLE
                        CommonUtils.setLog(
                            LogParams(
                                "kiosk",
                                Config.shop_id,
                                "order",
                                OrderManager.getInstance().orderId,
                                "KIOSK",
                                getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")",
                                "悠遊卡交易失敗",
                                "",
                                logMsg
                            )
                        )
                    }
                }
                tvMessage!!.text = message
                tvMessage!!.visibility = View.VISIBLE
            }

            override fun onNoICERINI() {}
        } // 檢查是否有自動加值

    // 字串不足10碼前面補0
    // 判斷若為柯文哲市長的測試卡號，則直接顯示並跳頁
    private val cardNumberListener: EasyCardPay.Listener
        get() = object : EasyCardPay.Listener {
            override fun onResult(result: String) {
                val logMsg = """悠遊卡卡號查詢
                 authKey:${Config.authKey}
                shopId:${Config.shop_id}
                kioskId:${Config.kiosk_id}
                worderId:${Bill.fromServer.worder_id}
                悠遊卡卡機回傳:$result"""
                LogUtil.writeLogToLocalFile(logMsg)
                val t3901 = easyCardPay!!.findTag(result, EcResTag.T3901)
                val t3904 = easyCardPay!!.findTag(result, EcResTag.T3904)
                val message = ErrorCode.getMessage(t3901, t3904, false)
                when (ErrorCode.getStatus(t3901, t3904)) {
                    ErrorCode.SUCCESS -> {
                        val t0200 = easyCardPay!!.findTag(result, EcResTag.T0200)
                        @SuppressLint("DefaultLocale") val t0200Format = String.format("%010d", t0200.toLong()) // 字串不足10碼前面補0
                        // 判斷若為柯文哲市長的測試卡號，則直接顯示並跳頁
                        when (t0200Format) {
                            "1353067585" -> {
                                setMemberNameAndGoToNextPage("柯市長")
                            }
                            "1353065329" -> {
                                setMemberNameAndGoToNextPage("柯文哲")
                            }
                            "1353084561" -> {
                                setMemberNameAndGoToNextPage("曾燦金")
                            }
                            else -> {
                                val manager = EasyCardManager.getInstance()
                                manager.getUserInfo(t0200Format, object : OnLoginListener {
                                    override fun onSuccess(response: CardInfo) {
                                        Config.acckey = response.acckey
                                        setMemberNameAndGoToNextPage(response.name)
                                    }

                                    override fun onFailure() {
                                        Config.acckey = Config.acckeyDefault
                                        setMemberNameAndGoToNextPage("")
                                    }
                                })
                            }
                        }
                    }
                    ErrorCode.BROKEN -> {
                        tvTitle!!.text = getString(R.string.ezCardPayErrorTitle)
                        tvTitle!!.visibility = View.VISIBLE
                        btnBack!!.visibility = View.VISIBLE
                    }
                    ErrorCode.NEED_RETRY -> {
                        setAllBtnVisibility(true)
                        checkAutoDeposit(result, easyCardPay)
                    }
                    ErrorCode.FAIL -> {
                        // 檢查是否有自動加值
                        tvTitle!!.text = getString(R.string.ezCardPayErrorTitle)
                        tvTitle!!.visibility = View.VISIBLE
                        checkAutoDeposit(result, easyCardPay)
                        btnBack!!.visibility = View.VISIBLE
                    }
                    else -> {
                        tvTitle!!.text = getString(R.string.ezCardPayErrorTitle)
                        tvTitle!!.visibility = View.VISIBLE
                        checkAutoDeposit(result, easyCardPay)
                        btnBack!!.visibility = View.VISIBLE
                    }
                }
                tvMessage!!.text = message
                tvMessage!!.visibility = View.VISIBLE
            }

            private fun setMemberNameAndGoToNextPage(memberName: String) {
                Config.memberName = memberName
                easyCardPay!!.release()
                CommonUtils.intentActivityAndFinish(this@EasyCardActivity, TimeIntervalActivity::class.java)
            }

            override fun onNoICERINI() {}
        }

    private fun addPayment(ecPayData: EcPayData) {
        val manager = OrderManager.getInstance()
        manager.setEcPayData(ecPayData)
        val payment = Payment()
        payment.type = PaymentsType.EASY_CARD.name
            payment.payment_amount = manager.totalFee.toInt()
        val cardNo = ecPayData.mifare_ID
        val beforeAmt = ecPayData.n_CPU_EV_Before_TXN
        val autoLoadAmt = ecPayData.n_AutoLoad_Amount
        val beforeDeduct = beforeAmt.toInt() + autoLoadAmt.toInt()
        val relateId = "$cardNo|$beforeDeduct|$autoLoadAmt|$beforeAmt"
        payment.relate_id = relateId
        manager.addPayment(payment)
    }

    private fun confirmOrder() {
        val manager = OrderManager.getInstance()
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
                        "悠遊卡付款-確認訂單成功",
                        "",
                        response!!.body().toString()
                    )
                )
                orderDetailAfterConfirm
            }

            override fun onRetry() {
                confirmOrder()
            }
        }, this)
    }

    private val orderDetailAfterConfirm: Unit
        get() {
            val manager = OrderManager.getInstance()!!
            manager.getOrderDetail(object : OrderListener {
                override fun onSuccess(response: Response<*>) {
                    val order = response.body() as Order?
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
                    orderDetailAfterConfirm
                }
            }, this)
        }

    private val orderDetail: Unit
        get() {
            val manager = OrderManager.getInstance()!!
            manager.getOrderDetail(object : OrderListener {
                override fun onSuccess(response: Response<*>) {
                    val order = response.body() as Order?
                    if (order != null) {
                        manager.total = order.charges.total.amount
                        manager.discount = order.charges.discount.amount
                        manager.totalFee = order.charges.total_fee.amount
                        manager.setPoints(order.charges.points)
                        manager.addTicketPayment(order)
                        confirmOrder()
                    } else {
                        orderDetail
                    }
                }

                override fun onRetry() {
                    writeLostOrderInfoWhenOffline()
                    orderDetail
                }
            }, this)
        }

    private fun finishOrder(order: Order) {
        OrderManager.getInstance().printReceipt(this, order)
        PaidDialog(this, order).show()
    }

    fun submitBill() {
        Bill.fromServer.weborder02_setEzCardPay()
        Bill.fromServer.submit(this, "ezCard")
        Bill.fromServer.lsn = Bill.LSN { response: String? ->
            try {
                val orderResponse = Json.fromJson(response, OrderResponse::class.java)
                if (orderResponse.code == 0) {
                    tvTitle!!.text = getString(R.string.paid)
                    tvTitle!!.visibility = View.VISIBLE
                    tvMessage!!.text = getString(R.string.takeReceipts)
                    tvMessage!!.visibility = View.VISIBLE
                    Bill.fromServer.print(this@EasyCardActivity)
                    if (Bill.fromServer.orderResponse.invoice == null && !Config.disableReceiptModule) {
                        NoInvoiceDialog(this@EasyCardActivity).show()
                    } else {
                        PaidDialog(this@EasyCardActivity).show()
                    }
                } else {
                    var codeMsg = getString(R.string.paidErrorCode)
                    codeMsg = String.format(codeMsg, orderResponse.code)
                    var message = orderResponse.message
                    if (message == null) {
                        message = getString(R.string.scanPayError)
                    }
                    tvMessage!!.text = message
                    tvMessage!!.visibility = View.VISIBLE
                    //                        String errMsg = codeMsg + getString(R.string.paidError) + Bill.fromServer.worder_id;
                    val addOrderBean = Bill.fromServer.constructAddOrderData()
                    val beanStr = Json.toJson(addOrderBean)
                    val encodeStr = GZipUtils.compress(beanStr)
                    var orderIdInfo = getString(R.string.orderIdIs)
                    orderIdInfo = String.format(orderIdInfo, Bill.fromServer.worder_id)
                    val dialogMsg = """
                    ${getString(R.string.orderFailMsg)}
                    ${getString(R.string.qrCodeHandleMsg)}
                    $orderIdInfo
                    """.trimIndent()
                    val alertDialogFragment =
                        AlertDialogFragment()
                    alertDialogFragment.setTitle(codeMsg)
                            .setMessage(dialogMsg)
                            .setConfirmButton(R.string.retry) {
                                if (!ClickUtil.isFastDoubleClick()) {
                                    alertDialogFragment.dismiss()
                                    Bill.fromServer.retrySubmit(this@EasyCardActivity)
                                    KioskPrinter.addLog("悠遊卡付款，失敗重試")
                                }
                            }
                            .setCancelButton(R.string.returnHomeButton) {
                                if (!ClickUtil.isFastDoubleClick()) {
                                    KioskPrinter.addLog("悠遊卡付款，失敗返回")
                                    alertDialogFragment.dismiss()
                                    ShopActivity.now!!.Finish()
                                    HomeActivity.now!!.closeAllActivities()
                                }
                            }
                            .setQrCode(encodeStr)
                            .setUnCancelAble()
                            .show(fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
                    HomeActivity.now!!.stopIdleProof()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    private fun addEzCardPayToServer(ecPayData: EcPayData) {
        val apiService = RetrofitManager.instance!!.getApiServices(Config.cacheUrl)
        val parameter = EasyCard.parseEcStmc3Parameter(ecPayData)
        apiService.setEcStmc3(Config.token, parameter).enqueue(object : Callback<Response<Void>> {
            override fun onResponse(call: Call<Response<Void>>, response: Response<Response<Void>>) {
                if (!response.isSuccessful) {
                    val ecDataString = Json.toJson(parameter)
                    LogUtil.writeEcDataToLocalFile(ecDataString)
                    EasyCard.startUploadTimer()
                }
            }

            override fun onFailure(call: Call<Response<Void>>, t: Throwable) {
                val ecDataString = Json.toJson(parameter)
                LogUtil.writeEcDataToLocalFile(ecDataString)
                EasyCard.startUploadTimer()
            }
        })
    }

    private fun writeLostOrderInfoWhenOffline() {
        if (!NetworkUtils.isOnline(this)) {
            val manager = OrderManager.getInstance()
            manager.writeLostOrderInfo()
        }
    }

    private fun checkAutoDeposit(result: String, easyCardPay: EasyCardPay?) {
        val t0409 = easyCardPay!!.findTag(result, EcResTag.T0409)
        if (t0409 != null && t0409.isNotEmpty()) {
            val ecPayData = EasyCard.parsePayData(result, easyCardPay)
            addEzCardPayToServer(ecPayData)
        }
    }

    private fun setAllBtnVisibility(visible: Boolean) {
        if (visible) {
            rlQnA!!.visibility = View.VISIBLE
            btnBack!!.visibility = View.VISIBLE
        } else {
            rlQnA!!.visibility = View.GONE
            btnBack!!.visibility = View.GONE
        }
    }

    interface OnLoginListener {
        fun onSuccess(response: CardInfo)
        fun onFailure()
    }
}
