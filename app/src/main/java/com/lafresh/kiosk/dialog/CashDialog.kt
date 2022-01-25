package com.lafresh.kiosk.dialog

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import com.lafresh.kiosk.*
import com.lafresh.kiosk.activity.HomeActivity
import com.lafresh.kiosk.activity.ShopActivity
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.httprequest.model.GetOrderInfoRes
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.manager.OrderManager.OrderListener
import com.lafresh.kiosk.model.Order
import com.lafresh.kiosk.model.Payment
import com.lafresh.kiosk.printer.DataTransformer
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.printer.KioskPrinter.PrinterCheckFlowListener
import com.lafresh.kiosk.printer.model.ReceiptDetail
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.type.PaymentsType
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.GZipUtils
import com.lafresh.kiosk.utils.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.droidsonroids.gif.GifImageView
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.M)
class CashDialog(context: Context) : Dialog(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen) {

    private var order: Order? = null
    private val activity: Activity
    private var btnBack: Button? = null
    private var tvTitle: TextView? = null
    private var tvSubTitle: TextView? = null
    private var pgb: ProgressBar? = null
    private var ivCenterleft: ImageView? = null
    private var payForCash: GifImageView? = null
    private var afterPrintTimer: CountDownTimer? = null
    private var detail: ReceiptDetail? = null

    private fun findViews() {
        btnBack = findViewById(R.id.btn_back)
        btnBack!!.visibility = View.GONE
        tvTitle = findViewById(R.id.tv_title1)
        payForCash = findViewById(R.id.pay_for_cash)
        tvSubTitle = findViewById(R.id.tv_title2)
        ivCenterleft = findViewById(R.id.iv_centerLeft)
        pgb = findViewById(R.id.pgb)
        pgb!!.visibility = View.GONE
        payForCash!!.visibility = View.GONE

//        btnPlay = findViewById(R.id.btnPlay);
//        wvGame = findViewById(R.id.wvGame);
        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        Kiosk.checkAndChangeUi(context, Config.titleLogoPath, ivLogo)
        val ivAd = findViewById<ImageView>(R.id.ivAd)
        Kiosk.checkAndChangeUi(context, Config.bannerImg, ivAd)
    }

    private fun setAction() {
        HomeActivity.now!!.stopIdleProof()
        afterPrintTimer = object : CountDownTimer((Config.idleCount_BillOK * 1000).toLong(), 1000) {
            override fun onTick(l: Long) {}
            override fun onFinish() {
                btnBack!!.performClick()
            }
        }
        btnBack!!.setOnClickListener {
            if (ClickUtil.isFastDoubleClick()) {
                return@setOnClickListener
            }
            if (afterPrintTimer != null) {
                afterPrintTimer!!.cancel()
                afterPrintTimer = null
            }
            KioskPrinter.addLog("櫃檯結帳正常返回")
            dismiss()
            ShopActivity.now!!.Finish()
            HomeActivity.now!!.closeAllActivities()
        }
    }

    private fun getPrinterCheckListener(context: Context): PrinterErrorDialog.Listener {
        return PrinterErrorDialog.Listener { KioskPrinter.getPrinter().beforePrintCheckFlow(context, getPrinterCheckListener(context), getAfterCheckListener(context)) }
    }

    private fun getAfterCheckListener(context: Context): PrinterCheckFlowListener {
        return PrinterCheckFlowListener {
            if (Config.isNewOrderApi) {
                KioskPrinter.getPrinter().printBill(getContext(), detail)
            } else {
                Bill.fromServer.print(context)
            }
            KioskPrinter.getPrinter().laterPrinterCheckFlow(context, getPrinterCheckListener(context), afterFlowListener, 5)
        }
    }

    private val afterFlowListener: PrinterCheckFlowListener
        get() = PrinterCheckFlowListener {
            KioskPrinter.addLog("印單完成")
            btnBack!!.visibility = View.VISIBLE
            afterPrintTimer!!.start()
        }

    private fun submitOrder(context: Context) {
        Bill.fromServer.submit(context, Bill.S_PAY_MENT_CASH)
        Bill.fromServer.lsn = Bill.LSN { response: String? ->
            pgb!!.visibility = View.GONE
            // 只取code, message 用任何一個有這倆屬性的class映射即可
            val res = Json.fromJson(response, GetOrderInfoRes::class.java)
            if (FlavorType.brogent.name == BuildConfig.FLAVOR) {
                tvSubTitle!!.text = context.getString(R.string.takeBill)
            } else {
                val params = LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT)
                params.setMargins(350, 10, 0, 0)
                tvTitle!!.layoutParams = params
                tvSubTitle!!.layoutParams = params
                ivCenterleft!!.visibility = View.GONE
                payForCash!!.visibility = View.VISIBLE
                tvSubTitle!!.visibility = View.GONE
            }
            if (res.code == 0) {
                ivCenterleft!!.visibility = View.INVISIBLE
                tvTitle!!.visibility = View.GONE
                if (!Config.isUseEmulator) {
                    Bill.fromServer.print(context)
                    KioskPrinter.getPrinter().laterPrinterCheckFlow(context, getPrinterCheckListener(context), afterFlowListener, 5)
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(5000)
                        btnBack!!.performClick()
                    }
                }
                //                        setExhibitionAction(cdt);
            } else {
                var codeMsg = context.getString(R.string.paidErrorCode)
                codeMsg = String.format(codeMsg, res.code)
                var msg = res.message
                var orderIdInfo = context.getString(R.string.orderIdIs)
                orderIdInfo = String.format(orderIdInfo, Bill.fromServer.worder_id)
                if (msg == null || "" == msg) {
                    msg = """
                        ${context.getString(R.string.errorOccur)}
                        ${context.getString(R.string.connectionError)}
                        """.trimIndent()
                }
                msg = codeMsg + msg
                msg += """

                    $orderIdInfo
                    """.trimIndent()
                tvTitle!!.text = "送單失敗"
                tvSubTitle!!.text = msg
                val addOrderBean = Bill.fromServer.constructAddOrderData()
                val beanStr = Json.toJson(addOrderBean)
                val encodeStr = GZipUtils.compress(beanStr)
                val dialogMsg = """
                    ${context.getString(R.string.orderFailMsg)}
                    ${context.getString(R.string.qrCodeHandleMsg)}
                    $orderIdInfo
                    """.trimIndent()
                val alertDialogFragment =
                    AlertDialogFragment()
                alertDialogFragment.setTitle(codeMsg)
                        .setMessage(dialogMsg)
                        .setConfirmButton(R.string.retry) {
                            if (!ClickUtil.isFastDoubleClick()) {
                                alertDialogFragment.dismiss()
                                Bill.fromServer.retrySubmit(context)
                                KioskPrinter.addLog("櫃檯結帳重試")
                            }
                        }
                        .setCancelButton(R.string.returnHomeButton) {
                            if (!ClickUtil.isFastDoubleClick()) {
                                KioskPrinter.addLog("櫃檯結帳失敗返回")
                                alertDialogFragment.dismiss()
                                ShopActivity.now!!.Finish()
                                HomeActivity.now!!.closeAllActivities()
                            }
                        }
                        .setQrCode(encodeStr)
                        .setUnCancelAble()
                        .show((context as Activity).fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
            }
        }
    }

    private fun addPayment() {
        val manager = OrderManager.getInstance()
        val payment = Payment()
        payment.type = PaymentsType.CASH.name
        payment.payment_amount = manager.totalFee.toInt()
        manager.addPayment(payment)
    }

    private fun confirmOrder() {
        val manager = OrderManager.getInstance()
        manager.confirmOrder(false, object : OrderListener {
            override fun onSuccess(response: Response<*>?) {
                orderDetailAfterConfirm
            }

            override fun onRetry() {
                confirmOrder()
            }
        }, activity)
    }

    private val orderDetailAfterConfirm: Unit
        get() {
            val manager = OrderManager.getInstance()
            manager.getOrderDetail(object : OrderListener {
                override fun onSuccess(response: Response<*>) {
                    order = response.body() as Order?
                    if (order != null) {
                        finishOrder(order!!)
                    } else {
                        orderDetailAfterConfirm
                    }
                }

                override fun onRetry() {
                    orderDetailAfterConfirm
                }
            }, activity)
        }

    private val orderDetail: Unit
        get() {
            val manager = OrderManager.getInstance()
            manager.getOrderDetail(object : OrderListener {
                override fun onSuccess(response: Response<*>) {
                    order = response.body() as Order?
                    if (order != null) {
                        manager.addTicketPayment(order!!)
                        confirmOrder()
                    } else {
                        orderDetail
                    }
                }

                override fun onRetry() {
                    orderDetail
                }
            }, activity)
        }

    private fun finishOrder(order: Order) {
        val params = LinearLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT)
        params.setMargins(350, 10, 0, 0)
        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
        }else{
            payForCash!!.visibility = View.VISIBLE
            tvSubTitle!!.visibility = View.GONE
            tvTitle!!.visibility = View.GONE
            tvTitle!!.layoutParams = params
            tvSubTitle!!.layoutParams = params
        }
        order.id = OrderManager.getInstance().orderId
        val productList = DataTransformer.generateProductList(order)
        detail = DataTransformer.generateReceiptDetail(order, productList)

        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
            OrderManager.getInstance().printReceipt(activity, order)
            afterPrintTimer!!.start()
        }else{
            KioskPrinter.getPrinter()?.let {
                if (it.isConnect) {
                    KioskPrinter.addLog("開始印單")
                    it.printBill(context, detail)
                    if (order.activityMessage != null && "" != order!!.activityMessage) {
                        KioskPrinter.getPrinter().printActivityMessage(context, order!!.activityMessage)
                    }
                    it.laterPrinterCheckFlow(context,
                        getPrinterCheckListener(context), afterFlowListener, 5)
                } else {
                    KioskPrinter.addLog("出單機在印單時沒有連線")
                }
            } ?: run {
                KioskPrinter.addLog("出單機在印單時沒有連線")
                GlobalScope.launch(Dispatchers.Main) {
                    delay(5000)
                    btnBack!!.performClick()
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Kiosk.idleCount = Config.idleCount
        return super.dispatchTouchEvent(ev)
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        ShopActivity.AL_DialogToClose!!.remove(this)
    } //    public void setExhibitionAction(final CountDownTimer cdt) {

    //        btn_back.setVisibility(View.GONE);
    //        btnPlay.setVisibility(View.VISIBLE);
    //        btnPlay.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                btnPlay.setVisibility(View.GONE);
    //                rlCenter.setVisibility(View.GONE);
    //                rlFail.setVisibility(View.GONE);
    //                btn_back.setVisibility(View.VISIBLE);
    //                btn_back.setOnClickListener(new View.OnClickListener() {
    //                    @Override
    //                    public void onClick(View view) {
    //                        if (cdt != null) {
    //                            cdt.cancel();
    //                        }
    //                        dismiss();
    //                        if (Act_Shop.Now != null) {
    //                            Act_Shop.Now.Finish();
    //                        }
    //                        Act_AD.Now.Act_CloseAll();
    //                    }
    //                });
    //
    //                wvGame.setVisibility(View.VISIBLE);
    //                wvGame.getSettings().setJavaScriptEnabled(true);
    //                wvGame.setWebViewClient(new WebViewClient());
    //
    //                String url = getContext().getResources().getString(R.string.gameUrl);
    //                wvGame.loadUrl(url);
    //            }
    //        });
    //
    //    }
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Kiosk.hidePopupBars(this)
        setContentView(R.layout.d_cash)
        ShopActivity.AL_DialogToClose!!.add(this)
        activity = context as Activity
        findViews()
        setAction()
        if (Config.isNewOrderApi) {
            addPayment()
            orderDetail
        } else {
            submitOrder(context)
        }
    }
}
