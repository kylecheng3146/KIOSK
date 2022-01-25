package com.lafresh.kiosk.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.lafresh.kiosk.*
import com.lafresh.kiosk.Kiosk.hidePopupBars
import com.lafresh.kiosk.creditcardlib.model.Code
import com.lafresh.kiosk.creditcardlib.model.Message
import com.lafresh.kiosk.creditcardlib.model.Request
import com.lafresh.kiosk.creditcardlib.model.Response
import com.lafresh.kiosk.creditcardlib.nccc.NCCCCode
import com.lafresh.kiosk.creditcardlib.nccc.NCCCTransDataBean
import com.lafresh.kiosk.dialog.CashModuleMessageDialog
import com.lafresh.kiosk.dialog.NoInvoiceDialog
import com.lafresh.kiosk.dialog.PaidDialog
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.httprequest.model.OrderResponse
import com.lafresh.kiosk.lanxin.EzAIOPay
import com.lafresh.kiosk.lanxin.model.EzAIOData
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.manager.OrderManager.OrderListener
import com.lafresh.kiosk.model.LogParams
import com.lafresh.kiosk.model.Order
import com.lafresh.kiosk.model.Payment
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.type.PaymentsType
import com.lafresh.kiosk.utils.*
import com.lafresh.kiosk.utils.LogUtils.info
import com.lafresh.kiosk.utils.NcccUtils.NcccTaskListener
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageView

@RequiresApi(api = Build.VERSION_CODES.M)
class NCCCActivity : BaseActivity(), View.OnClickListener {
    private var transactionType = "信用卡"
    private var givCreditCard: GifImageView? = null
    private lateinit var esvcIndicator: String
    var btnBack: Button? = null
    var tvTitle: TextView? = null
    var tvMessage: TextView? = null
    var ncccThread: Thread? = null
    var timeOutChecker: CountDownTimer? = null
    var date: String? = null
    var time: String? = null
    var inString: String? = null
    var amount = 0
    var pgb: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeActivity.now!!.activities.add(this)
        setContentView(R.layout.activity_nccc)

        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
            if (this.getIntent().getExtras()!!.getBundle("EZAIO") != null) {
                esvcIndicator = this.getIntent().getExtras()!!.getBundle("EZAIO")!!.getString("Response")!!
                addPaymentEzAIO()
                return
            }else{
                runEzAIOPay()
            }
        }else{
            esvcIndicator = this.getIntent().getExtras()!!.getString("esvcIndicator")!!
            setUI()
            setActions()
            startNCCC(esvcIndicator)
        }
    }

    override fun onResume() {
        super.onResume()
        hidePopupBars(this)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onDestroy() {
        Log.i("TAG", "onDestroy")
        HomeActivity.now!!.activities.remove(this)
        if (timeOutChecker != null) {
            timeOutChecker!!.cancel()
        }
        if (ncccThread != null) {
            ncccThread!!.interrupt()
            ncccThread = null
        }
        super.onDestroy()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Kiosk.idleCount = Config.idleCount
        return super.dispatchTouchEvent(ev)
    }

    override fun setUI() {
        btnBack = findViewById(R.id.btnBack)
        btnBack!!.setVisibility(View.GONE)
        enableBtnBack(false)
        pgb = findViewById(R.id.pgb)
        pgb!!.setVisibility(View.GONE) // 依PM需求取消pgb
        tvTitle = findViewById(R.id.tvTitle)
        tvMessage = findViewById(R.id.tvMessage)
        givCreditCard = findViewById(R.id.givCreditCard)
        initLogo()
        checkTransactionType()
    }

    /**
    * 判斷交易類別
    * E -> 悠遊卡
    * N -> 信用卡
    * */
    private fun checkTransactionType() {
        if ("E".equals(esvcIndicator)) {
            givCreditCard!!.setImageResource(R.drawable.gif_easy_card_combine)
            transactionType = "悠遊卡"
            tvTitle!!.text = getString(R.string.pay_by_easycard)
        }
    }

    override fun setActions() {
        btnBack!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        finish()
    }

    private fun enableBtnBack(b: Boolean) {
        if (b) {
            btnBack!!.setOnClickListener(this)
            btnBack!!.visibility = View.VISIBLE
        } else {
            btnBack!!.setOnClickListener(null)
            btnBack!!.visibility = View.GONE
        }
    }

    private fun getDeduceRequest(esvcIndicator: String): Request {
        date = TimeUtil.getNowTime("yyMMdd")
        time = TimeUtil.getNowTime("HHmmss")
        if (Config.isNewOrderApi) {
            amount = FormatUtil.removeDot(OrderManager.getInstance().totalFee).toInt()
            info("" + amount)
        } else {
            amount = Bill.fromServer.total
        }
        inString = NCCCTransDataBean.getTransData(amount.toDouble(), date, time, esvcIndicator)
        return Request(Config.creditCardComportPath, Config.creditCardBaudRate, inString)
    }

    private fun startNCCC(esvcIndicator: String) {
        HomeActivity.now!!.stopIdleProof()
        val request = getDeduceRequest(esvcIndicator)
        request.sessionInterval = 4 * 1000 // 扣款要延長下一次卡機的可用時間
        val deductTask = NcccUtils.ncccTask(request, ncccResListener(), progressListener())
        NcccUtils.getThreadPoolExecutor().execute(deductTask)
    }

    private fun ncccResListener(): NcccTaskListener {
        return NcccTaskListener { response: Response ->
            runOnUiThread {
                val code = response.code
                if (code == Code.SUCCESS) {
                    handleResponse(response.data)
                } else {
                    val msg = Message.getMessage(response.code)
                    val showMsg = "(Code:$code)$msg"
                    tvMessage!!.text = showMsg
                    btnBack!!.visibility = View.VISIBLE
                    showEcrErrorDialog(response)
                }
            }
        }
    }

    private fun showEcrErrorDialog(response: Response) {
        var codeMsg = getString(R.string.paidErrorCode)
        codeMsg = String.format(codeMsg, response.code)
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment.setTitle(codeMsg)
                .setMessage(getString(R.string.ncccErrorMessage))
                .setConfirmButton(R.string.retry) { v: View? ->
                    if (!ClickUtil.isFastDoubleClick()) {
                        alertDialogFragment.dismiss()
                        KioskPrinter.addLog(transactionType + "，卡機錯誤重試")
                        searchPreviousTransaction()
                    }
                }
                .setCancelButton(R.string.back) { v: View? ->
                    if (!ClickUtil.isFastDoubleClick()) {
                        alertDialogFragment.dismiss()
                    }
                }
                .setUnCancelAble()
                .show(fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }

    private fun progressListener(): NcccUtils.ProgressListener {
        return NcccUtils.ProgressListener { second: Int ->
            runOnUiThread {
                tvMessage!!.setTextColor(Color.RED)
                tvMessage!!.text = String.format(getString(R.string.ncccTransactionTime), second)
            }
        }
    }

    private fun handleResponse(res: String) {
        Bill.fromServer.ncccRes = res
        val dataBean = NCCCTransDataBean.generateRes(res)
        if ("0000" == dataBean.ecrResponseCode) { // 成功
            if (Config.isNewOrderApi) {
                addPayment(dataBean)
                writeLostOrderInfoWhenOffline()
                orderDetail
                CommonUtils.setLog(
                    LogParams(
                        "kiosk",
                        Config.shop_id,
                        "order",
                        OrderManager.getInstance().orderId,
                        "KIOSK",
                        getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")",
                        "信用卡交易成功",
                        "",
                        dataBean.toString()
                    )
                )
            } else {
                Bill.fromServer.ncccTransDataBean = dataBean
                Bill.fromServer.setNCCCWebOrder02(dataBean)
                submitOrder()
            }
        } else {
            pgb!!.visibility = View.GONE
            var failMsg = NCCCCode.getResMessage(dataBean.ecrResponseCode)
            val orderIdInfo = String.format(getString(R.string.orderIdIs), Bill.fromServer.worder_id)
            failMsg += """
                
                $orderIdInfo
                """.trimIndent()
            tvMessage!!.setTextColor(Color.RED)
            tvMessage!!.text = failMsg
            enableBtnBack(true)
            HomeActivity.now!!.idleProof()
        }
    }

    private fun writeLostOrderInfoWhenOffline() {
        if (!NetworkUtils.isOnline(this)) {
            val manager = OrderManager.getInstance()
            manager.writeLostOrderInfo()
        }
    }

    private fun addPayment(data: NCCCTransDataBean) {
        val manager = OrderManager.getInstance()
        manager.setNcccDataBean(data)
        val payment = Payment()
        esvcIndicator.let {
            if (it.equals("N")) {
                payment.type = PaymentsType.CREDIT_CARD.name
            } else {
                switchCardType(data, payment)
            }
        }
        payment.payment_amount = manager.totalFee.toInt()
        payment.transaction_id = data.approvalNo
        val detail = Payment.Detail()
        detail.resource_number = data.cardNo
        detail.auth_code = data.approvalNo
        detail.reference_no = data.originRrn
        detail.terminal_data = data.receiptNo + "|" + data.terminalId
        payment.detail = detail
        manager.addPayment(payment)
    }

    private fun addPaymentEzAIO() {
        val ncccObject = JSONObject(esvcIndicator)

        if ("0000" == ncccObject.getString("ecrResponseCode")) { // 成功
            val manager = OrderManager.getInstance()

            val payment = Payment()

            payment.type = PaymentsType.CREDIT_CARD.name
            payment.payment_amount = manager.totalFee.toInt()
            payment.transaction_id = ncccObject.getString("approvalCode")
            val detail = Payment.Detail()
            detail.resource_number = ncccObject.getString("cardNumber")
            detail.auth_code = ncccObject.getString("approvalCode")
            detail.reference_no = ncccObject.getString("referenceNo")
            detail.terminal_data =
                ncccObject.getString("receiptNo") + "|" + ncccObject.isNull("terminalId")
            payment.detail = detail
            manager.addPayment(payment)

            setContentView(R.layout.act_printing)
            orderDetailAfterConfirm
        }else{
            setContentView(R.layout.act_pay_fail)
            val btnBackHome = findViewById<Button>(R.id.btn_back_home)
            val imageBtnRestart = findViewById<ImageButton>(R.id.imageBtn_restart)
            val btnBackLast = findViewById<Button>(R.id.btn_back_last)
            btnBackHome.setOnClickListener{
                backToHome()
            }
            imageBtnRestart.setOnClickListener{
                backToCheckOutActivity()
            }
            btnBackLast.setOnClickListener{
                backToCheckOutActivity()
            }
        }
    }

    private fun backToHome(){
        ShopActivity.now!!.Finish()
        HomeActivity.now!!.closeAllActivities()
        CommonUtils.intentActivity(this@NCCCActivity, HomeActivity::class.java)
    }

    private fun backToCheckOutActivity(){
        CommonUtils.intentActivity(this@NCCCActivity, CheckOutActivity::class.java)
    }


    private fun switchCardType(data: NCCCTransDataBean, payment: Payment) {
        when (data.cardType) {
            "11" -> {
                payment.type = PaymentsType.NCCC_EASY_CARD.name
            }

            "12" -> {
                payment.type = PaymentsType.NCCC_IPASS.name
            }

            "13" -> {
                payment.type = PaymentsType.NCCC_ICASH.name
            }

            "14" -> {
                payment.type = PaymentsType.NCCC_HAPPY_CASH.name
            }
        }
    }

    private fun confirmOrder() {
        val manager = OrderManager.getInstance()
        manager.confirmOrder(true, object : OrderListener {
            override fun onSuccess(response: retrofit2.Response<*>?) {
                val version: String = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
                CommonUtils.setLog(
                    LogParams(
                        "kiosk",
                        Config.shop_id,
                        "order",
                        manager.orderId,
                        "KIOSK",
                        version,
                        "信用卡交易-確認訂單成功",
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
        private get() {
            val manager = OrderManager.getInstance()
            manager.getOrderDetail(object : OrderListener {
                override fun onSuccess(response: retrofit2.Response<*>) {
                    val order = response.body() as Order?
                    if (order != null) {
                        manager.total = order.charges.total.amount
                        manager.discount = order.charges.discount.amount
                        manager.totalFee = order.charges.total_fee.amount
                        manager.points = order.charges.points
                        finishOrder(order)
                        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
                            timeOutChecker = object : CountDownTimer(2000, 2000) {
                                override fun onTick(millisUntilFinished: Long) {
                                }
                                override fun onFinish() {
                                    backToHome()
                                }
                            }.start()
                        }
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
        private get() {
            val manager = OrderManager.getInstance()
            manager.getOrderDetail(object : OrderListener {
                override fun onSuccess(response: retrofit2.Response<*>) {
                    val order = response.body() as Order?
                    if (order != null) {
                        manager.total = order.charges.total.amount
                        manager.discount = order.charges.discount.amount
                        manager.totalFee = order.charges.total_fee.amount
                        manager.points = order.charges.points
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

    private fun submitOrder() {
        Bill.fromServer.submit(this, Bill.S_PAYMENT_NCCC)
        Bill.fromServer.lsn = Bill.LSN { response: String? ->
            val orderResponse = Json.fromJson(response, OrderResponse::class.java)
            if (orderResponse.code == 0) {
                tvTitle!!.text = getString(R.string.paid)
                tvMessage!!.text = getString(R.string.takeReceipts)
                Bill.fromServer.print(this@NCCCActivity)
                if (orderResponse.invoice == null && !Config.disableReceiptModule) {
                    NoInvoiceDialog(this@NCCCActivity).show()
                } else {
                    PaidDialog(this@NCCCActivity).show()
                }
            } else {
                pgb!!.visibility = View.GONE
                var codeMsg = getString(R.string.paidErrorCode)
                codeMsg = String.format(codeMsg, orderResponse.code)
                var failMsg: String? = getString(R.string.errorOccur)
                if (orderResponse.message != null) {
                    failMsg = orderResponse.message
                }
                tvMessage!!.setTextColor(Color.RED)
                tvMessage!!.text = failMsg
                //                        String errMsg = codeMsg + getString(R.string.paidError) + Bill.fromServer.worder_id;
                var orderIdInfo = getString(R.string.orderIdIs)
                orderIdInfo = String.format(orderIdInfo, Bill.fromServer.worder_id)
                val addOrderBean = Bill.fromServer.constructAddOrderData()
                val beanStr = Json.toJson(addOrderBean)
                val encodeStr = GZipUtils.compress(beanStr)
                val dialogMsg = """
                ${getString(R.string.orderFailMsg)}
                ${getString(R.string.qrCodeHandleMsg)}
                $orderIdInfo
                """.trimIndent()
                val alertDialogFragment =
                    AlertDialogFragment()
                alertDialogFragment.setTitle(codeMsg)
                        .setMessage(dialogMsg)
                        .setConfirmButton(R.string.retry) { v: View? ->
                            if (!ClickUtil.isFastDoubleClick()) {
                                alertDialogFragment.dismiss()
                                Bill.fromServer.retrySubmit(this@NCCCActivity)
                                KioskPrinter.addLog(transactionType + "，失敗重試")
                            }
                        }
                        .setCancelButton(R.string.returnHomeButton) { v: View? ->
                            if (!ClickUtil.isFastDoubleClick()) {
                                KioskPrinter.addLog(transactionType + "，失敗返回")
                                alertDialogFragment.dismiss()
                                ShopActivity.now!!.Finish()
                                HomeActivity.now!!.closeAllActivities()
                            }
                        }
                        .setQrCode(encodeStr)
                        .setUnCancelAble()
                        .show(fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
            }
        }
    }

    private fun searchPreviousTransaction() {
        val inputString = NCCCTransDataBean.getSearchTransData(amount.toDouble(), date, time, esvcIndicator)
        val request = Request(Config.creditCardComportPath, Config.creditCardBaudRate, inputString)
        val searchPreviousTask = NcccUtils.ncccTask(request, ncccResListener(), progressListener())
        NcccUtils.getThreadPoolExecutor().execute(searchPreviousTask)
    }

    private fun runEzAIOPay(){
        val manager = OrderManager.getInstance()
        val transAmount = manager.totalFee.toString()
        val orderID = manager.orderId.toString()
        val ezAIOData = EzAIOData()

        ezAIOData.packageName = "tw.lafreash.app.kiosk"
        ezAIOData.packagePage = NCCCActivity::class.java.name
        ezAIOData.transType = "01"
        ezAIOData.hostID = "01"
        ezAIOData.posID = "0001"
//        ezAIOData.transAmount = "100"
//        ezAIOData.orderID = "order0001"
        ezAIOData.transAmount = FormatUtil.removeDot(transAmount).toString()
        ezAIOData.orderID = orderID

        val ezAIOPay = EzAIOPay(this)
        ezAIOPay.ezAIOData = ezAIOData
        ezAIOPay.runEzAIOPay()
    }
}
