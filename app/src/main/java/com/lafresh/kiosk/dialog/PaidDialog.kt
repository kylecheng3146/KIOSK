package com.lafresh.kiosk.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import com.lafresh.kiosk.*
import com.lafresh.kiosk.activity.HomeActivity
import com.lafresh.kiosk.activity.ShopActivity
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.Order
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.printer.KioskPrinter.PrinterCheckFlowListener
import com.lafresh.kiosk.type.FlavorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.M)
class PaidDialog(ct: Context) : Dialog(ct, android.R.style.Theme_Light_NoTitleBar_Fullscreen) {

    private var btnPlay: Button? = null
    private var btnBack: Button? = null
    private var rlCenter: RelativeLayout? = null
    private var wvGame: WebView? = null
    private var afterPrintTimer: CountDownTimer? = null
    private var order: Order? = null

    constructor(ct: Context, order: Order?) : this(ct) {
        this.order = order
    }

    private fun findViews() {
        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        Kiosk.checkAndChangeUi(context, Config.titleLogoPath, ivLogo)
        val ivAd = findViewById<ImageView>(R.id.ivAd)
        Kiosk.checkAndChangeUi(context, Config.bannerImg, ivAd)
        btnBack = findViewById(R.id.btn_back)
        btnBack!!.visibility = View.INVISIBLE
        btnPlay = findViewById(R.id.btnPlay)
        rlCenter = findViewById(R.id.rl_center)
        wvGame = findViewById(R.id.wvGame)
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
            KioskPrinter.addLog("付款完成後返回")
            if (afterPrintTimer != null) {
                afterPrintTimer!!.cancel()
            }
            dismiss()
            if(ShopActivity.now != null){
                ShopActivity.now!!.Finish()
            }
            if(HomeActivity.now != null){
                HomeActivity.now!!.closeAllActivities()
            }
        }

//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rlCenter.setVisibility(View.GONE);
//                btnPlay.setVisibility(View.GONE);
//                btnBack.setVisibility(View.VISIBLE);
//                wvGame.setVisibility(View.VISIBLE);
//                wvGame.getSettings().setJavaScriptEnabled(true);
//                wvGame.setWebViewClient(new WebViewClient());
//
//                String url = getContext().getResources().getString(R.string.gameUrl);
//                wvGame.loadUrl(url);
//            }
//        });
    }

    private val printerCheckListener: PrinterErrorDialog.Listener
        get() = PrinterErrorDialog.Listener { KioskPrinter.getPrinter().beforePrintCheckFlow(context, printerCheckListener, getAfterCheckListener(context)) }

    private fun getAfterCheckListener(context: Context): PrinterCheckFlowListener {
        return PrinterCheckFlowListener {
            // 改為不印發票，只印收據
            val printer = KioskPrinter.getPrinter()
            if (FlavorType.brogent.name == BuildConfig.FLAVOR) {
                printer.printGamePoint(context)
            } else {
                if (Config.isNewOrderApi) {
                    OrderManager.getInstance().printOnlyReceipt(context, order)
                } else {
                    printer.printReceipt(context)
                }
            }
            printer.laterPrinterCheckFlow(context, printerCheckListener, afterFlowListener, 5)
        }
    }

    private val afterFlowListener: PrinterCheckFlowListener
        get() = PrinterCheckFlowListener {
            KioskPrinter.addLog("印單完成")
            btnBack!!.visibility = View.VISIBLE
            afterPrintTimer!!.start()
        }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Kiosk.idleCount = Config.idleCount
        return super.dispatchTouchEvent(ev)
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        ShopActivity.AL_DialogToClose!!.remove(this)
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Kiosk.hidePopupBars(this)
        setContentView(R.layout.d_paid)
        ShopActivity.AL_DialogToClose!!.add(this)
        findViews()
        setAction()
        KioskPrinter.getPrinter()
            ?.let {
            it.laterPrinterCheckFlow(ct, printerCheckListener, afterFlowListener, 5)
        } ?: run {
            KioskPrinter.addLog("出單機在印單時沒有連線")
            GlobalScope.launch(Dispatchers.Main) {
                KioskPrinter.addLog("印單完成")
                btnBack!!.visibility = View.VISIBLE
                afterPrintTimer!!.start()
              }
        }
    }
}
