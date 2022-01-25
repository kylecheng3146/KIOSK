package com.lafresh.kiosk.activity

import android.os.*
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.lafresh.kiosk.*
import com.lafresh.kiosk.dialog.CashModuleMessageDialog
import com.lafresh.kiosk.dialog.PaidDialog
import com.lafresh.kiosk.factory.CashModuleFactory
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.*
import com.lafresh.kiosk.model.Order
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.test.CashModuleServer
import com.lafresh.kiosk.test.ClientThread
import com.lafresh.kiosk.type.PaymentsType
import com.lafresh.kiosk.utils.*
import com.lafresh.kiosk.utils.CommonUtils.setLog
import java.lang.Exception

/**
 * Created by Kyle on 2021/4/26.
 */
@RequiresApi(Build.VERSION_CODES.M)
class CashPaymentActivity : BaseActivity(), CashModuleClientListener, CashModuleServerListener,
    View.OnClickListener {
    private lateinit var cashModuleServer: CashModuleServer
    private lateinit var tvCurrentCash: TextView
    private lateinit var rlBody: ConstraintLayout
    private lateinit var clError: ConstraintLayout
    private lateinit var tvTotalCash: TextView
    private lateinit var btnBack: Button
    private lateinit var tvCountdownTime: TextView
    private lateinit var tvTotalAmount: TextView
    val cashModuleFactory = CashModuleFactory()
    var cashModuleClientListener: CashModuleClientListener? = null
    var cashModuleServerListener: CashModuleServerListener? = null
    private lateinit var countDownTimer: CountDownTimer
    val gson = Gson()
    var currentAmount = 0
    var amount = 0
    var transcation: ClientThread? = null
    var resetAll: ClientThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeActivity.now!!.activities.add(this)
        HomeActivity.now!!.stopIdleProof()
        setContentView(R.layout.activity_cashmodule_payment)
        cashModuleClientListener = this
        cashModuleServerListener = this

        validateNewApiFlow()
        HomeActivity.now!!.activities.add(this)
        Kiosk.hidePopupBars(this)
        cashModuleServer = CashModuleServer()
        cashModuleServer.initCashModuleServer(cashModuleServerListener)
        setUI()
        setActions()
        ClientThread(cashModuleClientListener, cashModuleFactory.registerCallBack()).start()
        countDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvCountdownTime.text = String.format(getString(R.string.cash_payment_countdown_time), millisUntilFinished / 1000)
            }
            override fun onFinish() {
                HomeActivity.now!!.stopIdleProof()
                if (!isFinishing()) {
                    val cash = CashModuleMessageDialog(this@CashPaymentActivity)
                    cash.show()
                }
                rlBody.visibility = View.GONE
                clError.visibility = View.VISIBLE
            }
        }.start()

        initLogo()
    }

    fun validateNewApiFlow() {
        if (Config.isNewOrderApi) {
            amount = FormatUtil.removeDot(OrderManager.getInstance().totalFee).toInt()
        } else {
            amount = Bill.fromServer.total
        }
    }

    override fun setUI() {
        tvCurrentCash = findViewById(R.id.tv_current_cash)
        tvTotalCash = findViewById(R.id.tv_total_cash)
        tvTotalAmount = findViewById(R.id.tv_total_amount)
        btnBack = findViewById(R.id.btnBack)
        tvCountdownTime = findViewById(R.id.tv_countdown_time)
        rlBody = findViewById(R.id.rl_body)
        clError = findViewById(R.id.cl_error)
        var s = getString(R.string.cash_module_total_money)
        var totalCash = amount.toString()
        tvTotalAmount.text = String.format(getString(R.string.cash_payment_total_amount), totalCash)
        s = String.format(s, totalCash)
        val start = 7
        val end = start + totalCash.length
        val spannable = SpannableStringBuilder(s)
        val redColorSpan = ForegroundColorSpan(ContextCompat.getColor(this, R.color.red))
        spannable.setSpan(redColorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(AbsoluteSizeSpan(80), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvTotalCash.text = spannable
    }

    override fun setActions() {
        btnBack.setOnClickListener(this)
    }

    override fun onReceived(response: String) {
        LogUtils.info(response)
        KioskPrinter.addLog("shop_id:" + Config.shop_id)
        KioskPrinter.addLog("worderId:" + OrderManager.getInstance().orderId)
        KioskPrinter.addLog("CASH_MODULE:" + response)
        if (response.contains("ResetAll")) {
            return
        }
        val transactionModel: TransactionModel = gson.fromJson(response, TransactionModel::class.java)
        when (transactionModel.command) {
            CashModuleFactory.Commend.ADD_MONEY.commend -> {
            }

            CashModuleFactory.Commend.TRANSACTION.commend -> {
                if (transactionModel.status == "OK") {
                    countDownTimer.cancel()
                    addPayment()
                    orderDetail
                }
            }

            CashModuleFactory.Commend.REGISTER_CALLBACK.commend -> {
                transcation = ClientThread(cashModuleClientListener, cashModuleFactory.transaction(OrderManager.getInstance().orderId, amount))
                transcation!!.start()
            }
        }
    }

    override fun onServerReceived(response: String) {
        LogUtils.info(response)
        KioskPrinter.addLog("shop_id:" + Config.shop_id)
        KioskPrinter.addLog("worderId:" + OrderManager.getInstance().orderId)
        KioskPrinter.addLog("CASH_MODULE:" + response)
        var dialog = AlertDialogFragment()
        val serverReceived: ServerReceivedModel = gson.fromJson(response, ServerReceivedModel::class.java)

        if (serverReceived.command.equals("WrongInput")) {
            if (!isFinishing) {
                dialog = showDialog(
                    R.string.cash_wrong_input_title,
                    R.string.cash_wrong_input_message,
                    false)
                dialog.show(this.fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
            }
        }

        if (serverReceived.command.equals("CoinReceived")) {
            runOnUiThread {
                tvCurrentCash.text =
                    (tvCurrentCash.text.toString().toInt() + serverReceived.amount).toString()
            }
        }

        if (serverReceived.command.equals("DeviceError")) {
            if (!isFinishing) {
                dialog = showDialog(
                    R.string.cash_device_eror_title,
                    R.string.cash_device_eror_message,
                    true)
                dialog.show(this.fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
            }
        }

        if (serverReceived.command.equals("ErrorRecovered")) {
            if (!isFinishing) {
                dialog = showDialog(
                    R.string.cash_error_received_title,
                    R.string.cash_error_received_message,
                    false
                )
                dialog.show(this.fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
            }
        }

        if (serverReceived.result == "OK") {
            if (!isFinishing) {
                dialog.dismiss()
            }
        }
    }

    fun addPayment() {
        val manager = OrderManager.getInstance()
        val payment = Payment()
        payment.type = PaymentsType.CASH_MODULE.name
        payment.payment_amount = manager.totalFee.toInt()
        manager.addPayment(payment)
    }

    fun confirmOrder() {
        val manager = OrderManager.getInstance()
        manager.confirmOrder(true, object : OrderManager.OrderListener {
            override fun onSuccess(response: retrofit2.Response<*>?) {
                val version: String = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
                setLog(LogParams(
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
                confirmOrder()
            }
        }, this)
    }

    private val orderDetailAfterConfirm: Unit
        private get() {
            val manager = OrderManager.getInstance()
            manager.getOrderDetail(object : OrderManager.OrderListener {
                override fun onSuccess(response: retrofit2.Response<*>) {
                    val order = response.body() as Order?
                    val version: String = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
                    setLog(LogParams(
                        "kiosk",
                        Config.shop_id,
                        "order",
                        manager.orderId,
                        "KIOSK",
                        version,
                        "取得訂單明細成功",
                        "",
                        response.body().toString()
                    )
                    )
                    if (order != null) {
                        manager.total = order.charges.total.amount
                        manager.discount = order.charges.discount.amount
                        manager.totalFee = order.charges.total_fee.amount
                        manager.points = order.charges.points
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
        private get() {
            val manager = OrderManager.getInstance()
            manager.getOrderDetail(object : OrderManager.OrderListener {
                override fun onSuccess(response: retrofit2.Response<*>) {
                    val order = response.body() as Order?
                    if (order != null) {
                        manager.total = order.charges.total.amount
                        manager.discount = order.charges.discount.amount
                        manager.totalFee = order.charges.total_fee.amount
                        manager.points = order.charges.points
                        confirmOrder()
                    } else {
                        orderDetail
                    }
                }

                override fun onRetry() {
                    orderDetail
                }
            }, this)
        }

    fun finishOrder(order: Order) {
        OrderManager.getInstance().printReceipt(this, order)
        PaidDialog(this, order).show()
        cashModuleServer.interrupt()
        transcation!!.shutdown()
        transcation!!.interrupt()
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastDoubleClick()) return
        when (v!!.id) {
            R.id.btnBack -> {
                interruptAllThread()
            }
        }
    }

    fun interruptAllThread() {
        try {
            transcation?.let {
                if (!it.isInterrupted) {
                    it.interrupt()
                }
                it.shutdown()
            }
            cashModuleServer?.let {
                cashModuleServer.interrupt()
            }
            countDownTimer.cancel()
            if (!isFinishing) {
                finish()
            }
        } catch (e: Exception) {
            LogUtils.info(e.message)
        } finally {
            Thread.currentThread().interrupt()
            HomeActivity.now!!.stopIdleProof()
        }
    }

    fun showDialog(title: Int, message: Int, isDeviceError: Boolean): AlertDialogFragment {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment
            .setTitle(title)
            .setMessage(message)
            .setConfirmButton({
                if (isDeviceError) {
                    resetAll = ClientThread(cashModuleClientListener, cashModuleFactory.resetAll())
                    resetAll!!.start()
                }
                alertDialogFragment.dismiss()
            })
            .setUnCancelAble()
        return alertDialogFragment
    }

    override fun onDestroy() {
        interruptAllThread()
        HomeActivity.now!!.activities.remove(this)
        super.onDestroy()
    }
}
