package com.lafresh.kiosk.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.lafresh.kiosk.*
import com.lafresh.kiosk.adapter.BannerPagerAdapter
import com.lafresh.kiosk.adapter.PickupMethodAdapter
import com.lafresh.kiosk.dialog.ConfigDialog
import com.lafresh.kiosk.dialog.ConfigDialog.SettingConfigListener
import com.lafresh.kiosk.dialog.IdleDialog
import com.lafresh.kiosk.factory.CashModuleFactory
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.httprequest.model.Banners
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.manager.BannerManager
import com.lafresh.kiosk.manager.EasyCardManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.*
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.printer.KioskPrinter.PrinterCheckFlowListener
import com.lafresh.kiosk.printer.model.Revenue
import com.lafresh.kiosk.test.ClientThread
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.utils.*
import com.lafresh.kiosk.utils.CommonUtils.hasValue
import com.lafresh.kiosk.utils.TimeUtil.getNowTime
import okhttp3.ResponseBody
import java.util.*
import kotlin.collections.ArrayList
import pl.droidsonroids.gif.GifImageButton
import print.Print
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.M)
class HomeActivity : BaseActivity(), CashModuleClientListener {
    private lateinit var dialog: AlertDialogFragment
    lateinit var gifBtnGoNext: GifImageButton
    lateinit var gibDateRevenue: GifImageButton
    lateinit var ivLogo: ImageView
    lateinit var ivBanner: ImageView
    lateinit var rlNext: RelativeLayout
    lateinit var rlPrevious: RelativeLayout
    lateinit var vLeft: View
    lateinit var vCenter: View
    lateinit var vRight: View
    lateinit var BTAddress: String
    val list = mutableListOf<Int>()
    lateinit var viewPager: ViewPager
    @JvmField
    var activities = ArrayList<Activity>()
    private var bannerList: MutableList<Banners.Banner> = ArrayList()
    var bannerCountDownTimer: CountDownTimer? = null
    var cdtIdle: CountDownTimer? = null
    var bannerIndex = 0
    var bannerTimeCounter = 0
    private var configKey = ""
    private var settingConfigListener: SettingConfigListener? = null
    val database = Firebase.database.reference
    private var getMoneyThread: ClientThread? = null
    private var resetAllThread: ClientThread? = null
    val cashModuleFactory = CashModuleFactory()
    val gson = Gson()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.e(getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.act_ad)
        now = this
        setUI()
        setActions()
        initBannerDisplay()
        Kiosk.idleCount = Config.idleCount
        setLogo()
    }

    override fun onResume() {
//        Log.e(getClass().getSimpleName(), "onResume");
        super.onResume()
        Kiosk.hidePopupBars(this)
        resetDefaultConfig()
        validatePrinterEnabled()
        validateHardwareEnabled()
        // 判斷若為現金模組在啟用韌體server
        if (BuildConfig.FLAVOR.equals(FlavorType.cashmodule.name)) {
            getCashInventory()
        }
    }

    fun validateHardwareEnabled() {
        if (Config.useEasyCardByKiosk && !Config.isTriggerFirebaseDatabase) {
            readFirebaseRealTimeData()
        }
    }

    fun validatePrinterEnabled() {
        if (KioskPrinter.printerStatus != null) {
            if (Bill.fromServer != null) {
                KioskPrinter.addLog("wOrderId=" + Bill.fromServer.worder_id)
            }
            LogUtil.writeLogToLocalFile(KioskPrinter.printerStatus)
            KioskPrinter.printerStatus = null
        }
    }

    fun getCashInventory() {
        getMoneyThread = ClientThread(this, cashModuleFactory.money)
        getMoneyThread!!.start()
    }

    fun readFirebaseRealTimeData() {
        Config.isTriggerFirebaseDatabase = true
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val kiosk_reference = dataSnapshot.child(Config.authKey).child(Config.shop_id).child(Config.kiosk_id.toUpperCase(
                    Locale.ROOT))
                if (kiosk_reference.value == null) {
                    val kioskCheckout = KioskCheckout(true).toMap()
                    database.child(Config.authKey).child(Config.shop_id).child(Config.kiosk_id.toUpperCase()).setValue(kioskCheckout)
                } else if (kiosk_reference.child("has_checked").value == false) {
                    redoEasyCardCheckout()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    private fun redoEasyCardCheckout() {
        ProgressUtils.instance!!.showProgressDialog(this)
        val listener: EasyCardManager.EasyCardListener = object : EasyCardManager.EasyCardListener {
            override fun onSuccess() {
                ProgressUtils.instance!!.hideProgressDialog()
                updateEasyCardDailyCheckoutApi()
            }

            override fun onFailed() {
                ProgressUtils.instance!!.hideProgressDialog()
            }

            override fun onProgress(msg: String) {}
        }
        EasyCardManager.getInstance().setEasyCardListener(listener)
        EasyCardManager.getInstance().initEcParameter(this)

//        val listener: EasyCardManager.EasyCardListener = object : EasyCardManager.EasyCardListener {
//            override fun onSuccess() {
//                ProgressUtils.instance!!.hideProgressDialog()
//                updateEasycardDailyCheckoutApi()
//            }
//
//            override fun onFailed() {
//                ProgressUtils.instance!!.hideProgressDialog()
//            }
//
//            override fun onProgress(msg: String) {
//                ProgressUtils.instance!!.showProgressDialog(this@HomeActivity)
//            }
//        }
//        val manager = EasyCardManager.getInstance()
//        manager.setEasyCardListener(listener)
//        manager.checkoutAllUnCheckout(this@HomeActivity)
    }

    fun resetDefaultConfig() {
        configKey = "?" // 保證不點清空不能進設定
        Config.memberName = ""
        Config.intentFrom = ""
        Config.phoneNumber = ""
        Config.group_id = ""
        Config.address = Config.addressDefault
        Config.isAlreadyRecommended = false
        Config.acckey = Config.acckeyDefault
        Config.token = Config.defaultToken
        activities.clear()
        EasyCardManager.easyCardTryTime.clear()
        OrderManager.getInstance().reset()
    }

    override fun onDestroy() {
//        Log.e(getClass().getSimpleName(), "onDestroy");
        stopIdleProof()
        validateBannerTimerEnabled()
        now = null
        super.onDestroy()
    }

    fun validateBannerTimerEnabled() {
        if (bannerCountDownTimer != null) {
            bannerCountDownTimer!!.cancel()
        }
    }

    override fun setUI() {
        ivLogo = findViewById(R.id.iv_logo)
        gifBtnGoNext = findViewById(R.id.gifBtnGoNext)
        gibDateRevenue = findViewById(R.id.gibDateRevenue)
        ivBanner = findViewById(R.id.iv_banner)
        rlPrevious = findViewById(R.id.rl_previous)
        rlNext = findViewById(R.id.rl_next)
        vLeft = findViewById(R.id.vLeft)
        vCenter = findViewById(R.id.vCenter)
        vRight = findViewById(R.id.vRight)
        viewPager = findViewById(R.id.pager)

        replaceOrderButtonBackground()
    }

    /**
     * 依照不同更換按鈕樣式
     * */
    fun replaceOrderButtonBackground() {
        when (BuildConfig.FLAVOR) {
            FlavorType.mwd.name -> {
                gifBtnGoNext.setBackgroundResource(R.drawable.selector_btn_order_now)
            }

            else -> {
            }
        }
    }

    override fun setActions() {
        gifBtnGoNext.setOnClickListener { v: View? -> checkAndGo() }
        gibDateRevenue.setOnClickListener { v: View? -> printDateRevenue() }
        vCenter.setOnClickListener { view: View? -> configKey = "" }
        vLeft.setOnClickListener { view: View? -> configKey += "0" }
        settingConfigListener = object : SettingConfigListener {
            override fun onOk() {
                val intent = Intent(this@HomeActivity, SplashActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onBack() {}
        }

        vRight.setOnClickListener {
            configKey += "1"
            // 開啟測試dialog
            if (configKey == "010101") {
                val configDialog = ConfigDialog(this@HomeActivity)
                configDialog.show()
                configDialog.setListener(settingConfigListener)
                configKey = ""
            }
        }

        // 底圖測試按鈕
        if (Config.isUseEmulator) {
            findViewById<View>(R.id.rl_bottom).setOnClickListener { showEcCheckoutDialog() }
        }

        rlNext.setOnClickListener {
            if (bannerList.size > 0) {
                bannerIndex = (bannerIndex + 1) % bannerList.size
                changeBannerItem(bannerIndex)
                bannerTimeCounter = 1
            }
        }
        rlPrevious.setOnClickListener {
            if (bannerList.size > 0) {
                bannerIndex = if (bannerIndex == 0) bannerList.size - 1 else (bannerIndex - 1) % bannerList.size
                changeBannerItem(bannerIndex)
                bannerTimeCounter = 1
            }
        }
    }

    fun checkAndGo() {
        if (now == null) {
            now = this
        }
        if (ClickUtil.isFastDoubleClick()) {
            return
        }
        validateDebugMode()
    }

    private fun printDateRevenue() {
        println(getNowTime("yyyy-MM-dd"))
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).getAccountingInformation(Config.token, Config.shop_id, getNowTime("yyyy-MM-dd"), getNowTime("yyyy-MM-dd"),"")
            ?.enqueue(object : Callback<GetAccountingInformation?> {
                override fun onResponse(call: Call<GetAccountingInformation?>, response: Response<GetAccountingInformation?>) {
                    val body = response.body()
                    if (response.isSuccessful) {
                        if (response.body() != null && response.body()?.revenue != null) {
                            var a = response.body()!!.revenue
                            val revenue = Revenue()
                            revenue.startDate = getNowTime("yyyy-MM-dd")
                            revenue.endDate = getNowTime("yyyy-MM-dd")
                            revenue.accountingInformation = response.body()
                            KioskPrinter.getPrinter().printDateRevenue(this@HomeActivity, revenue)
                        } else {
                        }
                    }
                }

                override fun onFailure(call: Call<GetAccountingInformation?>, t: Throwable) {
                }
            })
    }

    private fun validateDebugMode() {
        if (Config.isUseEmulator) toNextPage() else checkPrinter()
    }

    fun checkPrinter() {
        val printer = KioskPrinter.getPrinter()
        if (printer != null && printer.isConnect) {
            KioskPrinter.addLog("點餐前檢查出單機")
            printer.beforePrintCheckFlow(this, null, PrinterCheckFlowListener {
                KioskPrinter.addLog("開始點餐")
                toNextPage()
            })
        } else {
            if (printer == null) {
                KioskPrinter.initKioskPrinter(this)
            } else if (BuildConfig.FLAVOR == FlavorType.kinyo.name && !printer.isConnect) {
                AlertUtils.showMsgWithConfirmBlueTooth(this@HomeActivity, R.string.activity_main_scan_error)
            } else {
                val msg = """
                ${getString(R.string.printerNoConnection)}
                ${getString(R.string.tryLater)}
                """.trimIndent()
                Kiosk.showMessageDialog(this, msg) {
                    val printer1 = KioskPrinter.getPrinter()
                    if (printer1 == null) {
                        Kiosk.showMessageDialog(this@HomeActivity, getString(R.string.noValidPrinter))
                    } else {
                        printer1.requestPrinterPermission(this@HomeActivity)
                    }
                }
            }
        }
    }

    fun toNextPage() {
        when (BuildConfig.FLAVOR) {
            FlavorType.brogent.name, FlavorType.galileo.name -> {
                CommonUtils.intentActivity(this@HomeActivity, ShopActivity::class.java)
            }
            FlavorType.liangpin.name -> {
                getAllProducts()
                val defaultSaleMethod = """
                            {
                              "id": "0",
                              "name": "購物",
                              "has_address": false,
                              "type": "DEFAULT"
                            }
                    """.trimIndent()
                val gson = Gson()
                val saleMethods = gson.fromJson(defaultSaleMethod, SaleMethod::class.java)
                OrderManager.getInstance().saleMethod = saleMethods
                CommonUtils.intentActivity(this@HomeActivity, ShopActivity::class.java)
            }
            FlavorType.TFG.name -> {
                CommonUtils.intentActivity(this@HomeActivity, EasyCardLoginActivity::class.java)
            }
            FlavorType.lanxin.name -> {
                val defaultSaleMethod = """
                            {
                              "id": "1",
                              "name": "外帶",
                              "has_address": false,
                              "type": "PICK_UP"
                            }
                    """.trimIndent()
                val gson = Gson()
                val saleMethods = gson.fromJson(defaultSaleMethod, SaleMethod::class.java)
                OrderManager.getInstance().saleMethod = saleMethods
                CommonUtils.intentActivity(this@HomeActivity, ShopActivity::class.java)
            }
            else -> {
//                writeCheckoutSuccess()
                CommonUtils.intentActivity(this@HomeActivity, DiningOptionActivity::class.java)
            }
        }
        Bill.Now = Bill()
        Bill.fromServer = Bill()
    }
    private fun getAllProducts() {
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).getProducts(
            GetProductsParams(
                "get_product",
                Config.authKey,
                Config.acckey,
                Config.shop_id,
                Config.saleType,
                "",
                TimeUtil.getNowTime(),
                Config.group_id
            )
        )
            .enqueue(object : Callback<GetProducts> {
                override fun onResponse(call: Call<GetProducts>, response: Response<GetProducts>) {
                    if (response.isSuccessful) {
                        Config.productImgPath = response.body()!!.imgpath
                        response.body()!!.product?.let {
                            for (i in it) {
                                if (i.gencods.hasValue()){
                                    for (j in i.gencods){
                                        Log.d("gencods",i.prod_id+":"+j)
                                        CheckOutOptionActivity.barCodeMap[j] = i
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GetProducts>, t: Throwable) {
                    now!!.idleProof()
                }
            })
    }
    fun showEcCheckoutDialog() {
        val msg = "TEST"
        val alertDialog = AlertDialog.Builder(this@HomeActivity)
            .setTitle("Debug")
            .setMessage(msg)
            .setNegativeButton("NO") { dialog: DialogInterface, which: Int -> dialog.cancel() }
            .setPositiveButton("Yes") { _: DialogInterface?, which: Int -> }
            .create()
        alertDialog.show()
    }

    fun initBannerDisplay() {
        bannerList = BannerManager.getInstance(this).bannerList
        validateHasBanner()
    }

    fun validateHasBanner() {
        if (BuildConfig.FLAVOR.equals(FlavorType.lanxin.name)) {
            ivBanner.visibility = View.GONE
        }else{
            if (bannerList.size > 0) {
                ivBanner.visibility = View.GONE
                viewPager.adapter = BannerPagerAdapter(this, bannerList)
                playBanners()
            } else {
                CommonUtils.loadImage(this@HomeActivity, ivBanner, R.drawable.banner_error2)
                ivBanner.visibility = View.VISIBLE
            }
        }
    }

    fun closeAllActivities() {
        for (activity in activities) {
            try {
                activity.finish()
            } catch (e: Exception) {
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
        activities.clear()
    }

    fun playBanners() {
        bannerCountDownTimer = object : CountDownTimer(1000, 1000) {
            override fun onTick(l: Long) {
                if (bannerTimeCounter % Config.time_adChange == 0) {
                    bannerIndex = (bannerIndex + 1) % bannerList.size
                    changeBannerItem(bannerIndex)
                }
                bannerTimeCounter++
            }

            override fun onFinish() {
                bannerCountDownTimer!!.start()
            }
        }
        bannerCountDownTimer!!.start()
    }

    fun changeBannerItem(index: Int) {
        viewPager.setCurrentItem(index)
    }

    fun idleProof() {
        if (cdtIdle == null) {
            cdtIdle = object : CountDownTimer(1000, 1000) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    Kiosk.idleCount--
                    Log.i(javaClass.simpleName, "IdleProof idleCount:" + Kiosk.idleCount)
                    if (Kiosk.idleCount <= 0) {
                        if (!IdleDialog.showing) {
                            if (activities.size > 0) {
                                val topIndex = activities.size - 1
                                val actTop = activities[topIndex]
                                val idleDialog = IdleDialog(actTop)
                                idleDialog.show()
                            }
                        }
                    }
                    cdtIdle!!.start()
                }
            }
        }
        cdtIdle!!.start()
    }

    fun stopIdleProof() {
        if (cdtIdle != null) {
            Log.i(javaClass.simpleName, "stopIdleProof")
            cdtIdle!!.cancel()
        }
    }

    override fun onUserInteraction() {
        Kiosk.idleCount = Config.idleCount
    }

    fun setLogo() {
        Kiosk.checkAndChangeUi(this@HomeActivity, Config.homeTitleLogoPath, ivLogo)
    }

    companion object {
        @JvmField
        var now: HomeActivity? = null
    }

    // call back by scan bluetooth printer
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            when (resultCode) {
                Print.ACTIVITY_CONNECT_BT -> {
                    BTAddress = data?.extras?.getString("BTAddress").toString()
                    if (CommonUtils.isEmpty(BTAddress)) {
                        AlertUtils.showMsgWithConfirmBlueTooth(this@HomeActivity, R.string.activity_main_scan_error)
                        return
                    } else {
                        CommonUtils.showMessage(this, getString(R.string.activity_main_connected))
                        return
                    }
                }
            }
        } catch (e: Exception) {
            AlertUtils.showMsgWithConfirmBlueTooth(this@HomeActivity, R.string.activity_main_scan_error)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun updateEasyCardDailyCheckoutApi() {
        ProgressUtils.instance!!.showProgressDialog(this)
        val easyCardDailyCheckout = EasycardDailyCheckout(Config.kiosk_id.toUpperCase(Locale.ROOT),
            true)
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).updateEasycardDailyCheckout(Config.token, Config.shop_id, easyCardDailyCheckout)
            .enqueue(object : Callback<Response<Void>> {
                override fun onResponse(call: Call<Response<Void>>, response: Response<Response<Void>>) {
                    ProgressUtils.instance!!.hideProgressDialog()
                    if (response.isSuccessful) {
                    } else {
                    }
                }

                override fun onFailure(call: Call<Response<Void>>, t: Throwable) {
                    ProgressUtils.instance!!.hideProgressDialog()
                }
            })
    }

    override fun onReceived(response: String) {
        validateCashModuleRes(response)
    }

    fun validateCashModuleRes(response: String) {
        if (response.contains("GetMoney")) {
            if (response.contains("Fail")) {
                if (!isFinishing) {
                    dialog = showErrorDialog(
                        R.string.cash_get_money_error_title,
                        R.string.cash_get_money_error_message
                    )
                    dialog.show(this.fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
                }
                return
            }
            val received: TransactionModel = gson.fromJson(response, TransactionModel::class.java)
            received.cashBoxStatus.forEach {
                when (it.denomination) {
                    100 -> {
                        SharePrefsUtils.putHundredInventory(this, it.quantity)
                    }

                    50 -> {
                        SharePrefsUtils.putFiftyDollarInventory(this, it.quantity)
                    }

                    10 -> {
                        SharePrefsUtils.putTenDollarInventory(this, it.quantity)
                    }

                    5 -> {
                        SharePrefsUtils.putFiveDollarInventory(this, it.quantity)
                    }

                    1 -> {
                        SharePrefsUtils.putOneDollarInventory(this, it.quantity)
                    }
                }
            }
            getMoneyThread!!.interrupt()
        }

        if (response.contains("ResetAll")) {
            resetAllThread?.let {
                if (!resetAllThread!!.isInterrupted) {
                    resetAllThread!!.interrupt()
                }
            }
            return
        }
    }

    fun showErrorDialog(title: Int, message: Int): AlertDialogFragment {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment
            .setTitle(title)
            .setMessage(message)
            .setConfirmButton({
                resetAllThread = ClientThread(this, cashModuleFactory.resetAll())
                resetAllThread!!.start()
                alertDialogFragment.dismiss()
            })
            .setUnCancelAble()
        return alertDialogFragment
    }
}
