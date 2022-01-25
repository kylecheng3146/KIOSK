package com.lafresh.kiosk.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.zxing.integration.android.IntentIntegrator
import com.lafresh.kiosk.Bill
import com.lafresh.kiosk.BuildConfig
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.R
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.type.ScanType
import com.lafresh.kiosk.utils.CommonUtils
import com.lafresh.kiosk.activity.ScanActivity
import com.lafresh.kiosk.dialog.PaidDialog
import com.lafresh.kiosk.httprequest.model.LinePayParameter
import com.lafresh.kiosk.httprequest.model.LinePayRes
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.model.LogParams
import com.lafresh.kiosk.model.Order
import com.lafresh.kiosk.model.Payment
import com.lafresh.kiosk.type.PaymentsType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanZxcingAndPayActivity : BaseActivity(), View.OnClickListener {
    lateinit var btnBarcode: Button
    lateinit var textView: TextView

    var timeOutChecker: CountDownTimer? = null
    var retryTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
            Config.scanType == ScanType.LINE_PAY.toString() -> {
                setContentView(R.layout.activity_line_pay)
            }
            Config.scanType == ScanType.EASY_WALLET.toString() -> {
                setContentView(R.layout.activity_easy_card)
            }
        }
//        setContentView(R.layout.act_scan_zxcing_test)
        title = ""
//        btnBarcode = findViewById(R.id.button)
//        textView = findViewById(R.id.txtContent)
//        btnBarcode.setOnClickListener {
//
//        }

        val intentIntegrator = IntentIntegrator(this@ScanZxcingAndPayActivity)
        intentIntegrator.captureActivity = ScanZxcingCapture::class.java
        intentIntegrator.setBeepEnabled(true)
        intentIntegrator.setCameraId(0)
        intentIntegrator.setPrompt("SCAN")
        intentIntegrator.setBarcodeImageEnabled(false)
        intentIntegrator.setOrientationLocked(false);//是否鎖定方向
        intentIntegrator.initiateScan()


    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDestroy() {
        HomeActivity.now!!.activities.remove(this)
        if (timeOutChecker != null) {
            timeOutChecker!!.cancel()
        }

        super.onDestroy()
    }

    override fun setUI() {
    }

    override fun setActions() {
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "已取消", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this,"Scanned -> " + result.contents, Toast.LENGTH_SHORT).show()
                prepareScanCode(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun prepareScanCode(code: String) {
        when {
            Config.scanType == ScanType.VEHICLE.toString() -> {//手機載具
                Bill.fromServer.buyerNumber = code
                OrderManager.getInstance().setCarrier(code)
                finish()
            }
            Config.scanType == ScanType.LINE_PAY.toString() -> {
                setContentView(R.layout.activity_line_pay)
                linePay(code)
            }
            Config.scanType == ScanType.DONATE.toString() -> {//捐贈碼
                Bill.fromServer.npoBan = code
                Bill.fromServer.custCode = ""
                OrderManager.getInstance().setLoveCode(code)
                finish()
            }


//            Config.scanType == ScanType.TAIWAN_PAY.toString() -> {
//                setContentView(R.layout.activity_taiwan_pay)
//            }
//            Config.scanType == ScanType.EASY_WALLET.toString() -> {
//                setContentView(R.layout.activity_easy_pay)
//            }
//            FlavorType.FormosaChang.name == BuildConfig.FLAVOR -> {
//                setContentView(R.layout.act_scan)
//                cl_welcome = findViewById(R.id.cl_welcome)
//                tv_welcome = findViewById(R.id.tv_welcome)
//            }
//            else -> {
//                setContentView(R.layout.act_scan)
//                cl_welcome = findViewById(R.id.cl_welcome)
//                tv_welcome = findViewById(R.id.tv_welcome)
//            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun linePay(oneTimeKey: String) {
        val manager = OrderManager.getInstance()
        val parameter = LinePayParameter()
        parameter.amount = manager.totalFee.toInt()
        parameter.orderId = manager.orderId
        parameter.oneTimeKey = oneTimeKey
        parameter.authKey = Config.authKey
        parameter.profileId = Config.shop_id + "-" + Config.kiosk_id
        parameter.retryTime = retryTime
        val lineApi = RetrofitManager.instance!!.getApiServices(Config.ApiRoot + "/")
        lineApi.linePay(parameter).enqueue(object : Callback<LinePayRes?> {
            override fun onResponse(call: Call<LinePayRes?>, response: Response<LinePayRes?>) {

                if (response.isSuccessful) {
                    val res = response.body()
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
                            "line pay 交易成功",
                            "",
                            res.toString()
                        )
                    )
                    if (res != null && res.returnCode == "0000") {
                        val transactionId = res.info.transactionId
                        addPayment(transactionId)
                        orderDetailAfterConfirm
                    } else {
                        retryTime += 1
                    }
                } else {
                    retryTime += 1
                }
            }

            override fun onFailure(call: Call<LinePayRes?>, t: Throwable) {
                retryTime += 1
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun addPayment(transactionId: String) {
        val manager = OrderManager.getInstance()
        val payment = Payment()
        when (Config.scanType) {
            ScanType.LINE_PAY.name -> {
                payment.type = PaymentsType.LINE_PAY.name
            }
            ScanType.TAIWAN_PAY.name -> {
                payment.type = PaymentsType.TAIWAN_PAY.name
            }
            ScanType.EASY_WALLET.name -> {
                payment.type = PaymentsType.Easy_Wallet.name
            }
        }
        payment.payment_amount = manager.totalFee.toInt()
        payment.transaction_id = transactionId
        manager.addPayment(payment)
    }

    private val orderDetailAfterConfirm: Unit
        @RequiresApi(Build.VERSION_CODES.M)
        get() {
            val manager = OrderManager.getInstance()
            manager.getOrderDetail(object : OrderManager.OrderListener {
                override fun onSuccess(response: Response<*>) {
                    val order = response.body() as Order?
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun finishOrder(order: Order) {
        OrderManager.getInstance().printReceipt(this, order)
        PaidDialog(this, order).show()

        timeOutChecker = object : CountDownTimer(2000, 2000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                backToHome()
            }
        }.start()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun backToHome(){
        ShopActivity.now!!.Finish()
        HomeActivity.now!!.closeAllActivities()
        CommonUtils.intentActivity(this, HomeActivity::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.ib_restart -> CommonUtils.intentActivity(this, CheckOutActivity::class.java)
        }
    }


}