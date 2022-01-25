package com.lafresh.kiosk.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import com.lafresh.kiosk.*
import com.lafresh.kiosk.dialog.KeyboardDialog
import com.lafresh.kiosk.dialog.LoadingDialog
import com.lafresh.kiosk.dialog.NoInvoiceDialog
import com.lafresh.kiosk.dialog.PaidDialog
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.httprequest.*
import com.lafresh.kiosk.httprequest.ApiRequest.ApiListener
import com.lafresh.kiosk.httprequest.model.*
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager.Companion.instance
import com.lafresh.kiosk.manager.LoginManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.manager.OrderManager.OrderListener
import com.lafresh.kiosk.model.*
import com.lafresh.kiosk.model.Order
import com.lafresh.kiosk.pipay.AskOrder
import com.lafresh.kiosk.pipay.PayOrder
import com.lafresh.kiosk.pipay.PiRes
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.type.PaymentsType
import com.lafresh.kiosk.type.ScanType
import com.lafresh.kiosk.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.M)
class ScanActivity : BaseActivity(), View.OnClickListener {
    var order: Order? = null
    var btn_back: Button? = null
    var btn_paste: Button? = null
    var btnInputCode: Button? = null
    var et_code: EditText? = null
    var countDownTimer: CountDownTimer? = null
    var tv_title1: TextView? = null
    var tv_title2: TextView? = null
    var tv_fail: TextView? = null
    var tv_code: TextView? = null
    var rl_pasteCover: RelativeLayout? = null
    var rl_body: RelativeLayout? = null
    var cl_welcome: ConstraintLayout? = null
    var tv_welcome: TextView? = null
    var pgb: ProgressBar? = null
    private var loading: LoadingDialog? = null
    var scanTime: Long = 0
    var title1: String? = null
    var title2: String? = null
    var scanListener: ScanListener? = null
    var retryTime = 0
    var timeOutChecker: CountDownTimer? = null

    interface ScanListener {
        fun onScan(code: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeActivity.now!!.activities.add(this)

        when {
            Config.scanType == ScanType.LINE_PAY.toString() -> {
                setContentView(R.layout.activity_line_pay)
            }
            Config.scanType == ScanType.TAIWAN_PAY.toString() -> {
                setContentView(R.layout.activity_taiwan_pay)
            }
            Config.scanType == ScanType.EASY_WALLET.toString() -> {
                setContentView(R.layout.activity_easy_pay)
            }
            FlavorType.FormosaChang.name == BuildConfig.FLAVOR -> {
                setContentView(R.layout.act_scan)
                cl_welcome = findViewById(R.id.cl_welcome)
                tv_welcome = findViewById(R.id.tv_welcome)
            }
            else -> {
                setContentView(R.layout.act_scan)
                cl_welcome = findViewById(R.id.cl_welcome)
                tv_welcome = findViewById(R.id.tv_welcome)
            }
        }

        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.captureActivity = ScanZxcingCapture::class.java
            intentIntegrator.setBeepEnabled(true)
            intentIntegrator.setCameraId(0)
            intentIntegrator.setPrompt("SCAN")
            intentIntegrator.setBarcodeImageEnabled(false)
            intentIntegrator.setOrientationLocked(false);//是否鎖定方向
            intentIntegrator.initiateScan()
        }else{
            setType()
            setUI()
            setActions()
        }

    }

    override fun onResume() {
        super.onResume()
        Kiosk.hidePopupBars(this)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    fun setType() {
        if (Config.scanType == ScanType.LOGIN.toString()) {
            scanTime = 250 * 20.toLong()
            title1 = getString(R.string.member_login_title)
            title2 = getString(R.string.member_login_subtitle)
            scanListener = object : ScanListener {
                override fun onScan(code: String) {
                    if (Config.isNewOrderApi) {
                        loginByCardNumber(code)
                    } else {
                        loading!!.show()
                        login(code)
                    }
                }
            }
        }
        if (Config.scanType == ScanType.VEHICLE.toString()) {
            Config.intentFrom = ""
            scanTime = 50 * 20.toLong()
            title1 = getString(R.string.scanVehicle)
            title2 = getString(R.string.scanVehicleMsg)
            val ivIcon = findViewById<ImageView>(R.id.iv_cellPhone)
            ivIcon.setImageDrawable(getDrawable(R.drawable.login_cellphone))
            scanListener = object : ScanListener {
                override fun onScan(code: String) {
                    if (VerifyUtil.isPhoneCarrierNumber(code)) {
                        Bill.fromServer.buyerNumber = code
                        Bill.fromServer.npoBan = ""
                        Bill.fromServer.custCode = ""
                        OrderManager.getInstance().setCarrier(code)
                        finish()
                    } else {
                        showErrorDialog(
                            R.string.scan_vehicle_error_title,
                            R.string.scan_vehicle_error_message
                        )
                    }
                }
            }
        }
        if (Config.scanType == ScanType.DONATE.toString()) {
            Config.intentFrom = ""
            scanTime = 50 * 20.toLong()
            title1 = getString(R.string.scanDonateNo)
            title2 = getString(R.string.scanDonateNoMsg)
            val ivIcon = findViewById<ImageView>(R.id.iv_cellPhone)
            ivIcon.setImageDrawable(getDrawable(R.drawable.login_cellphone))
            scanListener = object : ScanListener {
                override fun onScan(code: String) {
                    if (VerifyUtil.isLoveCode(code)) {
                        Bill.fromServer.npoBan = code
                        Bill.fromServer.custCode = ""
                        Bill.fromServer.buyerNumber = ""
                        OrderManager.getInstance().setLoveCode(code)
                        finish()
                    } else {
                        showErrorDialog(
                            R.string.scan_vehicle_error_title,
                            R.string.scan_vehicle_error_message
                        )
                    }
                }
            }
        }
        if (Config.scanType == ScanType.TABLE_NO.toString()) {
            scanTime = 250 * 20.toLong()
            title1 = getString(R.string.scanTableNoCode)
            title2 = getString(R.string.scanTableNoMsg)
            val ivIcon = findViewById<ImageView>(R.id.iv_cellPhone)
            ivIcon.setImageDrawable(getDrawable(R.drawable.icon_qrcode))
            scanListener = object : ScanListener {
                override fun onScan(code: String) {
                    enableBtnBack(false)
                    tv_fail!!.text = ""
                    checkTableNoCode(code)
                }
            }
        }
        if (Config.scanType == ScanType.LINE_PAY.toString()) {
            Config.intentFrom = ""
            if (Config.isUseEmulator) {
                if (Config.isNewOrderApi) {
                    linePay(Config.linePayPaymentCode)
                } else {
                    linePayOldVer(Config.linePayPaymentCode)
                }
            } else {
                scanTime = 150 * 20.toLong()
                title1 = getString(R.string.scanLinePay)
                title2 = getString(R.string.scanLinePayMsg)
                scanListener = object : ScanListener {
                    override fun onScan(code: String) {
                        enableBtnBack(false)
                        loading!!.show()
                        if (code.length >= 18) {
                            if (Config.isNewOrderApi) {
                                linePay(code.substring(0, 18))
                            } else {
                                linePayOldVer(code.substring(0, 18))
                            }
                        } else {
                            showErrorDialog(
                                R.string.scan_vehicle_error_title,
                                R.string.scan_vehicle_error_message
                            )
                        }
                    }
                }
            }
        }
        if (Config.scanType == ScanType.PI_PAY.toString()) {
            Config.intentFrom = ""
            scanTime = 50 * 20.toLong()
            title1 = getString(R.string.scanPiPay)
            title2 = getString(R.string.scanPiPayMsg)
            scanListener = object : ScanListener {
                override fun onScan(code: String) {
                    enableBtnBack(false)
                    loading!!.show()
                    if (code.length > 18) {
                        piPay(code.substring(0, 18))
                    }
                }
            }
        }
        if (Config.scanType == ScanType.EASY_WALLET.toString()) {
            Config.intentFrom = ""
            scanTime = 50 * 20.toLong()
            title1 = getString(R.string.scanPayTitleMessage)
            title2 = getString(R.string.scanPayInstruction)
            //            findViewById(R.id.iv_cellPhone).setBackgroundResource(R.drawable.kiosk_scan);
            scanListener = object : ScanListener {
                override fun onScan(code: String) {
                    enableBtnBack(false)
                    easyPay(code)
                }
            }
        }
        if (Config.scanType == ScanType.MWD_PAY.toString()) {
            Config.intentFrom = ""
            scanTime = 50 * 20.toLong()
            title1 = getString(R.string.scanPayTitleMessage)
            title2 = getString(R.string.scanPayInstruction)
            //            findViewById(R.id.iv_cellPhone).setBackgroundResource(R.drawable.kiosk_scan);
            scanListener = object : ScanListener {
                override fun onScan(code: String) {
                    enableBtnBack(false)
                    easyPay(code)
                }
            }
        }
        if (Config.scanType == ScanType.TAIWAN_PAY.toString()) {
            Config.intentFrom = ""
            scanTime = 1000 * 3.toLong()
            title1 = getString(R.string.scanPayTitleMessage)
            title2 = getString(R.string.scanPayInstruction)
            //            findViewById(R.id.iv_cellPhone).setBackgroundResource(R.drawable.kiosk_scan);
            scanListener = object : ScanListener {
                override fun onScan(code: String) {
                    enableBtnBack(false)
                    taiwanPay(code)
                }
            }
        }
    }

    override fun setUI() {
        btnInputCode = findViewById(R.id.btnInputCode)
        btn_back = findViewById(R.id.btn_back)
        btn_paste = findViewById(R.id.btn_paste)
        et_code = findViewById(R.id.et_code)
        tv_title1 = findViewById(R.id.tv_title1)
        tv_title2 = findViewById(R.id.tv_title2)
        tv_code = findViewById(R.id.tv_code)
        tv_fail = findViewById(R.id.tv_fail)
        rl_pasteCover = findViewById(R.id.rl_pasteCover)
        rl_body = findViewById(R.id.rl_body)
        pgb = findViewById(R.id.pgb)
        tv_fail!!.setVisibility(View.INVISIBLE)
        pgb!!.setVisibility(View.INVISIBLE)
        tv_title1!!.text = title1
        tv_title2!!.text = title2
        loading = LoadingDialog(this@ScanActivity)
        if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name)) {
            btn_back!!.setBackgroundResource(R.drawable.btn_main_pressed)
            btn_back!!.text = getText(R.string.back)
        }
        initLogo()
        et_code!!.requestFocus()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setActions() {
        if (Config.scanType == ScanType.DONATE.toString()) {
            btnInputCode!!.visibility = View.VISIBLE
            btnInputCode!!.setOnClickListener(this)
        }
        enableBtnBack(true)
        et_code!!.setOnTouchListener { view: View?, motionEvent: MotionEvent? ->
            val imm = et_code!!.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm != null && et_code!!.hasSelection()) {
                imm.hideSoftInputFromWindow(et_code!!.windowToken, 0)
            }
            true
        }

        et_code!!.textSize = 1f
        et_code!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                if (start == 0 && after == 1) {
                    countDownTimer = object : CountDownTimer(scanTime, scanTime) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            CommonUtils.hideSoftKeyboard(this@ScanActivity)
                            val code = et_code!!.text.toString().trim { it <= ' ' }
                            et_code!!.setText("")
                            if (scanListener != null) {
                                pgb!!.visibility = View.GONE
                                scanListener!!.onScan(code)
                            }
                        }
                    }.start()
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    pgb!!.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onClick(view: View) {
        if (ClickUtil.isFastDoubleClick()) {
            return
        }
        when (view.id) {
            R.id.btnInputCode -> {
                val keyboard = KeyboardDialog(this@ScanActivity)
                keyboard.type = KeyboardDialog.DONATE_CODE
                keyboard.onEnterListener =
                    KeyboardDialog.OnEnterListener { count: Int, text: String ->
                        scanListener!!.onScan(text)
                    }
                keyboard.show()
            }
            R.id.btn_back -> {
                scanListener = null
                if (Config.intentFrom.equals("DiningOptionActivity")) {
                    CommonUtils.intentActivityAndFinish(this, DiningOptionActivity::class.java)
                } else {
                    finish()
                }
            }
            else -> {
            }
        }
    }

    fun login(code: String?) {
        val post = Post(this@ScanActivity, Config.ApiRoot + "/webservice/member/index.php")
        post.addField("authkey", Config.authKey)
        post.addField("op", "DecVirtualCard")
        post.addField("card_no", code)
        post.SetPostListener { Response: String? ->
            try {
                val jo = JSONObject(Response)
                if (jo.getInt("code") == 0 && jo.getString("acckey") != "null") {
                    Config.acckey = jo.getString("acckey")
                    Bill.Now.isMember = true
                    finish()
                } else {
                    tv_fail!!.text = getString(R.string.loginFail)
                    tv_fail!!.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (loading != null && loading!!.isShowing) {
                    loading!!.cancel()
                }
            }
        }
        post.GO()
    }

    private fun loginByCardNumber(cardNo: String) {
        ProgressUtils.instance!!.showProgressDialog(this)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        val loginManager = LoginManager()
        loginManager.setListener(object : LoginManager.Listener {
            override fun onSuccess(member: Member?) {
                ProgressUtils.instance!!.hideProgressDialog()
                Config.token = member!!.token
                Config.group_id = member!!.group_id
                OrderManager.getInstance().isLogin = true
                OrderManager.getInstance().member = member
                GlobalScope.launch(Dispatchers.Main) {
                    if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name)) {
                        val name = OrderManager.getInstance().member.name!!.toCharArray()
                        for (i in name.indices) {
                            if (i != 0) {
                                name[i] = '*'
                            }
                        }
                        tv_welcome!!.text = String(name) + getString(R.string.scanWelcomeMsg)
                        rl_body!!.visibility = View.GONE
                        cl_welcome!!.visibility = View.VISIBLE
                        delay(3000)
                    }
                    returnPreviousPage()
                }
            }

            private fun returnPreviousPage() {
                 if (Config.intentFrom.equals("DiningOptionActivity") && BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name)) {
                    CommonUtils.intentActivityAndFinish(
                        this@ScanActivity,
                        ShopActivity::class.java
                    )
                } else if (Config.intentFrom.equals("DiningOptionActivity")) {
                    CommonUtils.intentActivityAndFinish(
                        this@ScanActivity,
                        DiningOptionActivity::class.java
                    )
                } else if (Config.intentFrom.equals("InvoiceActivity")) {
                    CommonUtils.intentActivityAndFinish(
                        this@ScanActivity,
                        InvoiceActivity::class.java
                    )
                } else {
                     CommonUtils.intentActivityAndFinish(
                         this@ScanActivity,
                         CheckOutActivity::class.java
                     )
                 }
            }

            override fun onFail() {
                ProgressUtils.instance!!.hideProgressDialog()
                val alertDialogFragment = AlertDialogFragment()
                if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name)) {
                    CommonUtils.showDialog(getString(R.string.scan_login_error_title), getString(R.string.scan_login_error_message), fragmentManager, R.string.scanRetry)
                } else {
                    alertDialogFragment
                        .setTitle(R.string.scan_login_error_title)
                        .setMessage(getString(R.string.scan_login_error_message))
                        .setConfirmButton {
                            if (!ClickUtil.isFastDoubleClick()) {
                                alertDialogFragment.dismiss()
                            }
                        }
                        .setCancelButton(R.string.back) {
                            alertDialogFragment.dismiss()
                            returnPreviousPage()
                        }
                        .setUnCancelAble()
                        .show(fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
                }
            }
        })
        loginManager.loginByCardNumber(cardNo)
    }

    fun checkTableNoCode(code: String) {
        var failMsg: String? = """
               ${getString(R.string.scanFail)}

               """.trimIndent()
        val isGoodCode =
            code.contains("authkey") && code.contains("table_no") && code.contains("shop_id")
        if (isGoodCode) {
            val authKey = getDataFromUrl("authkey", code)
            val shopId = getDataFromUrl("shop_id", code)
            val tableNo = getDataFromUrl("table_no", code)
            if (tableNo.length > 5) {
                failMsg += getString(R.string.reScanMsg)
                showFailMessage(failMsg)
                return
            }
            if (Config.authKey == authKey && Config.shop_id == shopId) {
                val listener: ApiListener = object : ApiListener {
                    override fun onSuccess(apiRequest: ApiRequest, body: String) {
                        try {
                            val jsonObject = JSONObject(body)
                            if (jsonObject.getInt("code") == 0) {
                                OrderManager.getInstance().setTableNumber(tableNo)
                                Bill.Now.setTableNo(tableNo)
                                val intent = Intent(this@ScanActivity, ShopActivity::class.java)
                                startActivity(intent)
                            } else {
                                onFail()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            FirebaseCrashlytics.getInstance().recordException(e)
                        }
                    }

                    override fun onFail() {
                        runOnUiThread {
                            showFailMessage(getString(R.string.tableNoError))
                            CommonUtils.hideSoftKeyboard(this@ScanActivity)
                        }
                    }
                }
                CheckTableApiRequest(tableNo).setApiListener(listener).go()
            } else {
                failMsg += getString(R.string.tableNoCodeError)
                showFailMessage(failMsg)
            }
        } else {
            failMsg += getString(R.string.reScanMsg)
            showFailMessage(failMsg)
        }
        enableBtnBack(true)
    }

    fun getDataFromUrl(key: String, url: String): String {
        var url = url
        return if (url.contains(key)) {
            val begin = url.indexOf(key)
            url = if (begin > 0) {
                url.substring(begin + key.length + 1)
            } else {
                return ""
            }
            val end = url.indexOf("&")
            if (end > 0) {
                url.substring(0, end)
            } else {
                url
            }
        } else {
            ""
        }
    }

    fun showFailMessage(msg: String?) {
        tv_fail!!.text = msg
        tv_fail!!.visibility = View.VISIBLE
        CommonUtils.hideSoftKeyboard(this@ScanActivity)
    }

    private fun setErrorMessage(message: String?) {
        val scanError = getString(R.string.scanError)
        message?.let { CommonUtils.showDialog("", message, this.fragmentManager) }
        tv_fail!!.visibility = View.VISIBLE
        tv_fail!!.text = scanError
        enableBtnBack(true)
    }

    private fun linePay(oneTimeKey: String) {
        val manager = OrderManager.getInstance()
        val parameter = LinePayParameter()
        parameter.amount = manager.totalFee.toInt()
        parameter.orderId = manager.orderId
        parameter.oneTimeKey = oneTimeKey
        parameter.authKey = Config.authKey
        parameter.profileId = Config.shop_id + "-" + Config.kiosk_id
        parameter.retryTime = retryTime
        val lineApi = instance!!.getApiServices(Config.ApiRoot + "/")
        lineApi.linePay(parameter).enqueue(object : Callback<LinePayRes?> {
            override fun onResponse(call: Call<LinePayRes?>, response: Response<LinePayRes?>) {
                if (loading != null && loading!!.isShowing) {
                    loading!!.cancel()
                }
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
                        orderDetail
                    } else {
                        retryTime += 1
                        setErrorMessage(null)
                    }
                } else {
                    retryTime += 1
                    setErrorMessage(null)
                }
            }

            override fun onFailure(call: Call<LinePayRes?>, t: Throwable) {
                retryTime += 1
                if (loading != null && loading!!.isShowing) {
                    loading!!.cancel()
                }
                enableBtnBack(true)
            }
        })
    }

    // 舊api呼叫
    fun linePayOldVer(oneTimeKey: String?) {
        val httpRequest = HttpRequest(Config.linePayUrl)
        try {
            val currencySymbol = getString(R.string.currencySymbol)
            httpRequest.jo.put("amount", Bill.fromServer.total)
            httpRequest.jo.put("orderId", Bill.fromServer.worder_id)
            httpRequest.jo.put("currency", currencySymbol)
            httpRequest.jo.put("oneTimeKey", oneTimeKey)
            httpRequest.jo.put("authkey", Config.authKey)
            httpRequest.jo.put("MerchantDeviceProfileId", Config.shop_id + "-" + Config.kiosk_id)
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        httpRequest.lsn = HttpRequest.LSN { response: String ->
            try {
                val jo = JSONObject(response)
                if (jo.getString("returnCode") == "0000") {
                    val jo_info = jo.getJSONObject("info")
                    val transactionId = jo_info.getString("transactionId")
                    Bill.fromServer.weborder02_setLinePay(transactionId)
                    submitOrder(Bill.S_PAY_MENT_LINE_PAY)
                } else {
                    val scanError = getString(R.string.scanError)
                    tv_fail!!.visibility = View.VISIBLE
                    tv_fail!!.text = scanError
                    enableBtnBack(true)
                    FirebaseCrashlytics.getInstance().setUserId(Bill.fromServer.worder_id)
                    FirebaseCrashlytics.getInstance()
                        .log("E/ScanActivity: LINE Response = $response")
                    FirebaseCrashlytics.getInstance()
                        .log("E/ScanActivity: OrderId = " + Bill.fromServer.worder_id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
                enableBtnBack(true)
            } finally {
                if (loading != null && loading!!.isShowing) {
                    loading!!.cancel()
                }
            }
        }
        httpRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    // todo 若還有要用，要換成新訂單流程
    private fun piPay(barcode: String) {
        val payOrder = PayOrder()
        payOrder.authkey = Config.authKey
        payOrder.shop_id = Config.shop_id
        payOrder.reg_id = Config.kiosk_id
        payOrder.bill_id = Bill.fromServer.worder_id
        payOrder.seq_no = Bill.fromServer.worder_id
        payOrder.barcode = barcode
        payOrder.amt = Bill.fromServer.total
        payOrder.items = piPayItems
        payOrder.create_time = TimeUtil.getNowTime("yyyyMMddHHmmss")
        val listener: ApiListener = object : ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {
                LogUtil.writeLogToLocalFile(
                    """
    訂單編號 = ${Bill.fromServer.worder_id}
    Pi 拍錢包 回傳 = $body
    """.trimIndent()
                )
                val piRes = Json.fromJson(body, PiRes::class.java)
                if (piRes.wallet_transaction_id != null) {
                    // 直接查訂單
                    piAskOrder(piRes.wallet_transaction_id)
                } else {
                    piErrorHandle(piRes)
                }
            }

            override fun onFail() {
                piErrorHandle(null)
            }
        }
        PayOrderApiRequest(Json.toJson(payOrder)).setApiListener(listener).go()
    }

    private fun taiwanPay(code: String) {
        val manager = OrderManager.getInstance()
        val parameter = TaiwanPayReq()
        parameter.amount = manager.totalFee.toInt()
        parameter.qrcode = code
        parameter.orderNumber = manager.orderId
        parameter.terminalId = Config.kiosk_id
        parameter.shopId = Config.shop_id
        parameter.retryTime = retryTime
        val taiwanPayApi = instance!!.getApiServices(Config.cacheUrl + "/")
        taiwanPayApi.taiwanPay(Config.token, parameter).enqueue(object : Callback<TaiwanPayRes?> {
            override fun onResponse(call: Call<TaiwanPayRes?>, response: Response<TaiwanPayRes?>) {
                if (loading != null && loading!!.isShowing) {
                    loading!!.cancel()
                }
                if (response.isSuccessful) {
                    val res = response.body()
                    val version: String =
                        getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
                    CommonUtils.setLog(
                        LogParams(
                            "kiosk", Config.shop_id,
                            "order", manager.orderId,
                            "KIOSK", version, "TAIWAN PAY 交易成功",
                            manager.toString(),
                            res.toString()
                        )
                    )
                    if (res != null && res.respCode == "4001") {
                        addPayment("", code)
                        orderDetail
                    } else {
                        retryTime += 1
                        // 付款失敗時取得新orderId
                        manager.orderId = ""
                        createOrUpdateOrder()
                        res?.respDesc?.let { setErrorMessage(it) }
                    }
                } else {
                    retryTime += 1
                    val res = Gson().fromJson(response.errorBody()!!.string(), TaiwanPayRes::class.java)

                    // 付款失敗時取得新orderId
                    manager.orderId = ""
                    createOrUpdateOrder()
                    CommonUtils.setLog(
                        LogParams(
                            "kiosk", Config.shop_id,
                            "order", manager.orderId,
                            "KIOSK", getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")", res.respDesc,
                            manager.toString(),
                            res.toString()
                        )
                    )
                    res?.respDesc?.let { setErrorMessage(it) }
                }
            }

            override fun onFailure(call: Call<TaiwanPayRes?>, t: Throwable) {
                retryTime += 1
                if (loading != null && loading!!.isShowing) {
                    loading!!.cancel()
                }
                enableBtnBack(true)
            }
        })
    }

    private fun createOrUpdateOrder() {
        pgb!!.visibility = View.VISIBLE
        val manager = OrderManager.getInstance()
        val totalPrice = Bill.Now.PriceBeforeDiscount_Get()
        manager.createOrUpdateOrder(totalPrice, object : OrderListener {
            override fun onSuccess(response: Response<*>) {
                pgb!!.visibility = View.GONE
                val res = response.body() as CreateOrderRes?
                if (res != null) {
                    // 如orderId已清空重新設定orderId並更新訂單，若已有orderId則確認訂單
                    if (manager.orderId == "") {
                        manager.orderId = res.id
                        manager.orderTime = res.create_at
                        createOrUpdateOrder()
                    } else {
                        pgb!!.visibility = View.VISIBLE
                        tv_fail!!.text = "${tv_fail!!.text}\n請重新掃描"
                        tv_fail!!.gravity = Gravity.CENTER
                        pgb!!.visibility = View.GONE
                    }
                } else {
                    createOrUpdateOrder()
                }
            }

            override fun onRetry() {
                if (loading != null && loading!!.isShowing) {
                    loading!!.cancel()
                }
                createOrUpdateOrder()
            }
        }, this)
    }

    private fun easyPay(code: String) {
        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){

        }else{
            btn_back!!.visibility = View.GONE
        }
        val easyPay = getEasyPayBean(code)
        val apiRequest = EasyPayApiRequest(easyPay)
        val listener: ApiListener = object : ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {
                runOnUiThread {
//                        CommonUtils.cancelLoadingDialog(progressDialog);
                    val response = Json.fromJson(body, EasyPayResponse::class.java)
                    if (response.code == 0) {
                        val transactionId = response.transaction_id
                        addPayment(transactionId, code)
                        orderDetail
//                        Bill.fromServer.setWeborder02ByEasyPay(response)
//                        submitOrder(Bill.S_PAYMENT_EASYPAY)
                    } else {
                        onFail()
                    }
                }
            }

            override fun onFail() {
                retryTime += 1
                runOnUiThread {
                    CommonUtils.hideSoftKeyboard(this@ScanActivity)
                    enableBtnBack(true)
                    showFailMessage(getString(R.string.scanError))
                }
            }
        }
        apiRequest.setApiListener(listener).go()
    }

    private fun piAskOrder(transactionId: String) {
        val askOrder = AskOrder()
        askOrder.authkey = Config.authKey
        askOrder.shop_id = Config.shop_id
        askOrder.bill_id = Bill.fromServer.worder_id
        askOrder.wallet_transaction_id = transactionId
        val listener: ApiListener = object : ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {
                LogUtil.writeLogToLocalFile(
                    """
    訂單編號 = ${Bill.fromServer.worder_id}
    Pi 拍錢包 回傳 = $body
    """.trimIndent()
                )
                val piRes = Json.fromJson(body, PiRes::class.java)
                if ("success" == piRes.status && "accepted" == piRes.trans_status) {
                    Bill.fromServer.setPiWebOrder02(piRes)
                    submitOrder(Bill.S_PAYMENT_PI)
                } else {
                    piErrorHandle(piRes)
                }
            }

            override fun onFail() {
                piErrorHandle(null)
            }
        }
        AskOrderApiRequest(Json.toJson(askOrder)).setApiListener(listener).go()
    }

    private fun submitOrder(type: String) {
        if (loading!!.isShowing) {
            loading!!.cancel()
        }
        // 處理執行緒問題
        runOnUiThread {
            Bill.fromServer.lsn = Bill.LSN { response: String? ->
                try {
                    val orderResponse = Json.fromJson(response, OrderResponse::class.java)
                    if (orderResponse.code == 0) {
                        tv_fail!!.visibility = View.INVISIBLE
                        tv_title1!!.text = getString(R.string.paid)
                        tv_title2!!.text = getString(R.string.takeReceipts)
                        Bill.fromServer.print(this@ScanActivity)
                        if (orderResponse.invoice == null && !Config.disableReceiptModule) {
                            NoInvoiceDialog(this@ScanActivity).show()
                        } else {
                            PaidDialog(this@ScanActivity).show()
                        }
                    } else {
                        var codeMsg = getString(R.string.paidErrorCode)
                        codeMsg = String.format(codeMsg, orderResponse.code)
                        var message = orderResponse.message
                        if (message == null) {
                            message = getString(R.string.scanPayError)
                        }
                        tv_fail!!.visibility = View.VISIBLE
                        tv_fail!!.text = message

//                                String errMsg = codeMsg + getString(R.string.paidError)
//                                        + Bill.fromServer.worder_id;
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
                            .setConfirmButton(R.string.retry) { v: View? ->
                                if (!ClickUtil.isFastDoubleClick()) {
                                    alertDialogFragment.dismiss()
                                    Bill.fromServer.retrySubmit(this@ScanActivity)
                                    KioskPrinter.addLog("掃碼付款，失敗重試")
                                }
                            }
                            .setCancelButton(R.string.returnHomeButton) { v: View? ->
                                if (!ClickUtil.isFastDoubleClick()) {
                                    KioskPrinter.addLog("掃碼付款，失敗返回")
                                    alertDialogFragment.dismiss()
                                    ShopActivity.now!!.Finish()
                                    HomeActivity.now!!.closeAllActivities()
                                }
                            }
                            .setQrCode(encodeStr)
                            .setUnCancelAble()
                            .show(fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    FirebaseCrashlytics.getInstance().recordException(e)
                    enableBtnBack(true)
                }
            }
            HomeActivity.now!!.stopIdleProof()
            Bill.fromServer.submit(this@ScanActivity, type)
        }
    }

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

    private fun addPayment(transactionId: String, relateId: String) {
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
        payment.relate_id = relateId
        manager.addPayment(payment)
    }

    private fun confirmOrder(method: () -> Unit) {
        val manager = OrderManager.getInstance()
        manager.confirmOrder(true, object : OrderListener {
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
                method()
            }

            override fun onRetry() {
                confirmOrder { method() }
            }
        }, this)
    }

    private val orderDetailAfterConfirm: Unit
        get() {
            val manager = OrderManager.getInstance()
            manager.getOrderDetail(object : OrderListener {
                override fun onSuccess(response: Response<*>) {
                    val order = response.body() as Order?
                    if (order != null) {
                        manager.total = order.charges.total.amount
                        manager.discount = order.charges.discount.amount
                        manager.totalFee = order.charges.total_fee.amount
                        manager.points = order.charges.points
                        finishOrder(order)

                        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
                            setContentView(R.layout.act_printing)
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
        get() {
            val manager = OrderManager.getInstance()
            manager.getOrderDetail(object : OrderListener {
                override fun onSuccess(response: Response<*>) {
                    val order = response.body() as Order?
                    if (order != null) {
                        manager.addTicketPayment(order)
                        manager.total = order.charges.total.amount
                        manager.discount = order.charges.discount.amount
                        manager.totalFee = order.charges.total_fee.amount
                        manager.points = order.charges.points
                        confirmOrder { orderDetailAfterConfirm }
                    } else {
                        orderDetail
                    }
                }

                override fun onRetry() {
                    orderDetail
                }
            }, this)
        }

    private fun finishOrder(order: Order) {
        OrderManager.getInstance().printReceipt(this, order)
        PaidDialog(this, order).show()
    }

    private fun getEasyPayBean(code: String): EasyPay {
        val totalPrice = if (Config.isNewOrderApi) {
            OrderManager.getInstance().totalFee
        } else {
            Bill.fromServer.total.toDouble()
        }
        val easyPay = EasyPay()
        easyPay.authkey = Config.authKey
        easyPay.shop_id = Config.shop_id
        easyPay.amount = FormatUtil.removeDot(totalPrice).toInt()
        easyPay.order_id = OrderManager.getInstance().orderId
        easyPay.qr_code = code
        easyPay.retryTime = retryTime
        return easyPay
    }

    private val piPayItems: String
        get() {
            var items = ""
            val weborder01List = Bill.fromServer.orderResponse.weborder01
            for (webOrder01 in weborder01List) {
                val productName = webOrder01.prod_name1
                val qty = webOrder01.qty
                val salePrice = webOrder01.sale_price
                val q = ComputationUtils.parseDouble(qty)
                val p = ComputationUtils.parseDouble(salePrice)
                val price = Arith.mul(q, p).toString()
                val item = "$productName*$q $$price"
                if (items.length > 0) {
                    items += ","
                }
                items += item
            }
            return items
        }

    private fun piErrorHandle(piRes: PiRes?) {
        runOnUiThread {
            enableBtnBack(true)
            tv_fail!!.visibility = View.VISIBLE
            var errMsg = getString(R.string.scanError)
            if (piRes != null) {
                errMsg = piRes.error_desc
                FirebaseCrashlytics.getInstance().log("Pi 拍錢包 回傳 = " + Json.toJson(piRes))
            }
            FirebaseCrashlytics.getInstance().setUserId(Bill.fromServer.worder_id)
            FirebaseCrashlytics.getInstance().log("PI 拍錢包 Error")
            FirebaseCrashlytics.getInstance().log("訂單編號 = " + Bill.fromServer.worder_id)
            val logMsg = """
                訂單編號 = ${Bill.fromServer.worder_id}
                Pi 拍錢包 回傳 = ${Json.toJson(piRes)}
                """.trimIndent()
            LogUtil.writeLogToLocalFile(logMsg)
            tv_fail!!.text = errMsg
            if (loading!!.isShowing) {
                loading!!.cancel()
            }
        }
    }

    public override fun onDestroy() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        if (timeOutChecker != null) {
            timeOutChecker!!.cancel()
        }
        HomeActivity.now!!.activities.remove(this)
        super.onDestroy()
    }

    private fun enableBtnBack(b: Boolean) {
        if (b) {
            btn_back!!.setOnClickListener(this)
            btn_back!!.visibility = View.VISIBLE
        } else {
            btn_back!!.setOnClickListener(null)
            btn_back!!.visibility = View.INVISIBLE
        }
    }

    override fun onUserInteraction() {
        Kiosk.idleCount = Config.idleCount
    }

    fun showErrorDialog(title: Int, message: Int) {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment
            .setTitle(title)
            .setMessage(message)
            .setConfirmButton({
                alertDialogFragment.dismiss()
            })
            .setUnCancelAble()
            .show(this.fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
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
//                Toast.makeText(this, "已取消", Toast.LENGTH_SHORT).show()
                finish()
            } else {
//                Toast.makeText(this,"Scanned -> " + result.contents, Toast.LENGTH_SHORT).show()
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
            Config.scanType == ScanType.EASY_WALLET.toString() -> {
                setContentView(R.layout.activity_easy_card)
                easyPay(code)
            }
//            Config.scanType == ScanType.TAIWAN_PAY.toString() -> {
//                setContentView(R.layout.activity_taiwan_pay)
//            }
        }
    }

    private fun backToHome(){
        if(ShopActivity.now != null){
            ShopActivity.now!!.Finish()
        }
        if(HomeActivity.now != null){
            HomeActivity.now!!.closeAllActivities()
        }

        CommonUtils.intentActivity(this@ScanActivity, HomeActivity::class.java)
    }
}
