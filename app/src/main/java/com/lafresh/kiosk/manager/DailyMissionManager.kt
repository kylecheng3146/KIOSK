package com.lafresh.kiosk.manager

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.lafresh.kiosk.BuildConfig
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.R
import com.lafresh.kiosk.activity.SplashActivity
import com.lafresh.kiosk.creditcardlib.model.Code
import com.lafresh.kiosk.creditcardlib.nccc.NCCCCode
import com.lafresh.kiosk.creditcardlib.nccc.NCCCTransDataBean
import com.lafresh.kiosk.easycard.BlackList
import com.lafresh.kiosk.factory.CashModuleFactory
import com.lafresh.kiosk.httprequest.ApiRequest
import com.lafresh.kiosk.httprequest.ApiRequest.ApiListener
import com.lafresh.kiosk.httprequest.GetKioskImgApiRequest
import com.lafresh.kiosk.httprequest.GetShop00ConfigApiRequest
import com.lafresh.kiosk.httprequest.model.*
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.manager.BannerManager.BannerListener
import com.lafresh.kiosk.manager.EasyCardManager.EasyCardListener
import com.lafresh.kiosk.model.BasicSettings
import com.lafresh.kiosk.model.SaleMethods
import com.lafresh.kiosk.model.UserDeviceParams
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.test.ClientThread
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.utils.*
import com.lafresh.kiosk.utils.NcccUtils.NcccTaskListener
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 處理每日初始化的任務
 */
@RequiresApi(Build.VERSION_CODES.M)
class DailyMissionManager private constructor() : CashModuleClientListener {
    private lateinit var resetAll: ClientThread
    private var portSettingThread: ClientThread? = null
    private val STEP_GET_API_TOKEN = 10
    private val STEP_CHECK_VERSION = 11
    private val STEP_GET_SHOP_CONFIG = 33
    private val STEP_GET_SALE_METHOD = 333
    private val STEP_GET_BANNER = 44
    private val STEP_GET_KIOSK_IMAGE = 55
    private val STEP_UPDATE_BLACK_LIST = 66
    private val STEP_CHECK_EASY_CARD = 77
    private val STEP_CHECKOUT_EASY_CARD_ALL_UN_CHECKOUT = 7878
    private val STEP_WAIT_NCCC_READY = 8787
    private val STEP_NCCC_CHECKOUT = 87
    private val STEP_INIT_PRINTER = 99
    private val STEP_FINISH = 666
    private val STEP_CASH_MODULE = 1111
    private val STEP_USER_DEVICE = 1112
    private var CURRENT_STEP = -1
    private var dailyMissionListener: DailyMissionListener? = null
    val cashModuleFactory = CashModuleFactory()
    val gson = Gson()
    private val threadPoolExecutor = ThreadPoolExecutor(1, 1, 300L,
            TimeUnit.SECONDS, ArrayBlockingQueue(5))
    private lateinit var context: Context

    interface DailyMissionListener {
        fun allDone()
        fun unDone(msg: String?)
        fun progressMessage(msg: String?)
        fun postMissionStep(msg: String?)
    }

    fun setDailyMissionListener(l: DailyMissionListener?) {
        dailyMissionListener = l
    }

    //        return Config.useEasyCardByShop && Config.useEasyCardByKiosk;
    private val isUseEasyCard: Boolean
        get() = Config.useEasyCardByKiosk
    //        return Config.useEasyCardByShop && Config.useEasyCardByKiosk;

    fun dailyFlow(context: Context?) {
        if (context != null) {
            this.context = context
        }
        nextStep(STEP_INIT_PRINTER, context)
    }

    private fun nextStep(step: Int, context: Context?) {
        CURRENT_STEP = step
        val startString = context!!.getString(R.string.start)
        val stepMessage = """
            ${getTaskMessage(step, context)}
            
            """.trimIndent()
        dailyMissionListener!!.postMissionStep(startString + stepMessage)
        when (step) {
            STEP_GET_API_TOKEN -> {
                getApiToken(context)
            }

            STEP_CHECK_VERSION -> {
                checkVersion(context)
            }

            STEP_UPDATE_BLACK_LIST -> {
                if (!Config.isUseEmulator && isUseEasyCard) {
                    updateBlackList(context)
                }
                nextStep(STEP_GET_BANNER, context)
            }

            STEP_GET_BANNER -> {
                getBanner(context)
            }

            STEP_GET_KIOSK_IMAGE -> {
                getKioskImg(context)
            }

            STEP_GET_SHOP_CONFIG -> {
                if (Config.isNewOrderApi) {
                    getBasicSettingsApi(context)
                } else {
                    getShopConfigFromServer(context, Config.shop_id)
                }
            }

            STEP_GET_SALE_METHOD -> {
                getSaleMethod(context)
            }

            STEP_CHECK_EASY_CARD -> {
                if (!Config.isUseEmulator && isUseEasyCard) {
                    initEasyCardAndCheckout(context)
                } else {
                    nextStep(STEP_WAIT_NCCC_READY, context)
                }
            }

            STEP_CHECKOUT_EASY_CARD_ALL_UN_CHECKOUT -> {
                checkoutAllUnCheckout(context)
            }

            STEP_WAIT_NCCC_READY -> {

//                if (!Config.isUseEmulator && NcccUtils.isUseNccc() && NcccUtils.isNotNcccCheckoutToday(context)) {
                if (!Config.isUseEmulator && NcccUtils.isUseNccc()) {
                    dailyMissionListener!!.unDone(context.getString(R.string.checkCreditCardEdcReady))
                    CURRENT_STEP = STEP_NCCC_CHECKOUT
                } else {
                    if (BuildConfig.FLAVOR.equals(FlavorType.cashmodule.name)) {
                        nextStep(STEP_CASH_MODULE, context)
                    } else {
                        nextStep(STEP_USER_DEVICE, context)
                    }
                }
            }

            STEP_NCCC_CHECKOUT -> {
                ncccCheckout(context)
            }

            STEP_INIT_PRINTER -> {
                if (!Config.isUseEmulator && BuildConfig.FLAVOR != FlavorType.kinyo.name && KioskPrinter.getPrinter() == null) {
                    val printer = KioskPrinter.initKioskPrinter(context.applicationContext)
                    if (printer == null) {
                        dailyMissionListener!!.unDone(""" ${context.getString(R.string.noConnectedPrinter)} """.trimIndent())
                        return
                    } else {
                        printer.requestPrinterPermission(context.applicationContext)
                    }
                }
                nextStep(STEP_GET_API_TOKEN, context)
            }

            STEP_CASH_MODULE -> {
                if (!SharePrefsUtils.getPortSettingDone(context)) {
                    portSettingThread = ClientThread(this, cashModuleFactory.portSetting())
                    portSettingThread!!.start()
                } else {
                    nextStep(STEP_USER_DEVICE, context)
                }
            }

            STEP_USER_DEVICE -> {
                apiSetUserDevice()
            }

            STEP_FINISH -> {
                dailyMissionListener!!.allDone()
            }

            else -> {
                dailyMissionListener!!.allDone()
            }
        }
    }

    private fun apiSetUserDevice() {
        val version: String = context.getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).setUserDevice(Config.token, UserDeviceParams("KIOSK", version, BuildConfig.VERSION_NAME, Config.kiosk_id, Config.shop_id))
            .enqueue(object : Callback<Response<Void>> {
                override fun onResponse(
                    call: Call<Response<Void>>,
                    response: Response<Response<Void>>
                ) {
                    if (response.isSuccessful) {
                        nextStep(STEP_FINISH, context)
                    } else {
                        nextStep(STEP_FINISH, context)
                    }
                }

                override fun onFailure(call: Call<Response<Void>>, t: Throwable) {
                    nextStep(STEP_FINISH, context)
                }
            })
    }

    private fun getBasicSettingsApi(context: Context?) {
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).getBasicSetting(Config.token, Config.shop_id)
            .enqueue(object : Callback<BasicSettings> {
                override fun onResponse(call: Call<BasicSettings>, response: Response<BasicSettings>) {
                    if (response.isSuccessful) {
                        val basicSettingsManager = BasicSettingsManager.instance
                        basicSettingsManager.setBasicSetting(response.body())
                        nextStep(STEP_GET_SALE_METHOD, context)
                    } else {
                        dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.get_basic_settings_failed)} """.trimIndent())
                    }
                }

                override fun onFailure(call: Call<BasicSettings>, t: Throwable) {
                    dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.get_basic_settings_failed)} """.trimIndent())
                }
            })
    }

    private fun getApiToken(context: Context?) {
        val manager = RetrofitManager.instance
        val apiServices = manager!!.getApiServices(Config.cacheUrl)
        val parameter = AuthParameter()
        parameter.authKey = Config.authKey
        parameter.accKey = Config.acckey
        apiServices.getToken(parameter).enqueue(object : Callback<Auth> {
            override fun onResponse(call: Call<Auth>, response: Response<Auth>) {
                if (response.isSuccessful) {
                    Config.defaultToken = "Bearer " + response.body()!!.token
                    Config.token = Config.defaultToken
                    nextStep(STEP_CHECK_VERSION, context)
                } else {
                    dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.getApiTokenFailed)} """.trimIndent())
                }
            }

            override fun onFailure(call: Call<Auth>, t: Throwable) {
                dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.getApiTokenFailed)} """.trimIndent())
            }
        })
    }

    // 先以單店設定為主，若單店無設定則取公司設定(無shopId)
    private fun getShopConfigFromServer(context: Context?, shopId: String?) {
        val getShop00ConfigApiRequest = GetShop00ConfigApiRequest(shopId)
        val apiListener: ApiListener = object : ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {
                val shop00Config = Json.fromJson(body, Shop00Config::class.java)
                if (shop00Config.code == 0) {
                    if (shopId != null) { // 有SHOP_ID 為單店設定
                        setSingleShopConfig(shop00Config)
                    }
                    setOrderPriceLimit(context, shop00Config, shopId)
                } else {
                    onFail()
                }
            }

            override fun onFail() {
                dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.setShopConfigFailed)} """.trimIndent())
            }
        }
        getShop00ConfigApiRequest.setApiListener(apiListener).go()
    }

    private fun setSingleShopConfig(singleShopConfig: Shop00Config) {
        // 金流 只取單店設定
        Config.usePiPay = "1" == singleShopConfig.enable_pi_pay_kiosk
        Config.useEasyCardByShop = "1" == singleShopConfig.enable_easy_card_pay
        Config.useNcccByShop = "1" == singleShopConfig.enable_nccc_pay
        Config.enableCounter = "1" == singleShopConfig.enable_counter_pay
        if (singleShopConfig.enable_table_no != null) { // 無設定會傳null
            // 先只取店家設定
            Config.tableNoConfig = ComputationUtils.parseInt(singleShopConfig.enable_table_no)
        }
    }

    private fun getSaleMethod(context: Context?) {
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).getSaleMethod(Config.token, Config.shop_id, "KIOSK")
            .enqueue(object : Callback<SaleMethods> {
                override fun onResponse(call: Call<SaleMethods>, response: Response<SaleMethods>) {
                    if (response.isSuccessful) {
                        val saleMethodList = response.body()?.sale_methods
                        saleMethodList?.let {
                            Config.saleMethods = it
                            nextStep(STEP_CHECK_EASY_CARD, context)
                        } ?: run {
                            dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.setSaleMethodFailed)} """.trimIndent())
                        }
                    } else {
                        dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.setSaleMethodFailed)} """.trimIndent())
                    }
                }

                override fun onFailure(call: Call<SaleMethods>, t: Throwable) {
                    dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.setSaleMethodFailed)} """.trimIndent())
                }
            })
    }

    // 先取單店，若無在取總店。皆無則為預設值
    private fun setOrderPriceLimit(context: Context?, shop00Config: Shop00Config, shopId: String?) {
        val amtLimit = ComputationUtils.parseInt(shop00Config.kiosk_amt_limit)
        if (amtLimit > 0) {
            Config.orderPriceLimit = amtLimit
            nextStep(STEP_GET_SALE_METHOD, context)
        } else if (shopId != null) {
            getShopConfigFromServer(context, null)
        } else {
            nextStep(STEP_GET_SALE_METHOD, context)
        }
    }

    private fun ncccCheckout(context: Context?) {
        val runnable = NcccUtils.checkoutTask(ncccCheckListener(context), progressListener(context))
        threadPoolExecutor.execute(runnable)
    }

    private fun progressListener(context: Context?): NcccUtils.ProgressListener {
        return NcccUtils.ProgressListener { second: Int -> dailyMissionListener!!.progressMessage(String.format(context!!.getString(R.string.waitingNcccCheckOutTime), second)) }
    }

    private fun ncccCheckListener(context: Context?): NcccTaskListener {
        return NcccTaskListener { response: com.lafresh.kiosk.creditcardlib.model.Response ->
            if (Code.SUCCESS == response.code) {
                NcccUtils.updateCheckoutDate(context)
                nextStep(STEP_FINISH, context)
            } else {
                showNcccCheckoutFailed(context, response)
            }
        }
    }

    private fun showNcccCheckoutFailed(context: Context?, response: com.lafresh.kiosk.creditcardlib.model.Response) {
        val dataBean = NCCCTransDataBean.generateRes(response.data)
        val resCode = dataBean.ecrResponseCode
        val failMsg = NCCCCode.getResMessage(resCode)
        val showMsg = context!!.getString(R.string.ncccCheckoutFailed) + resCode + failMsg
        dailyMissionListener!!.unDone(showMsg)
    }

    private fun updateBlackList(context: Context?) {
        // 目前悠遊卡黑名單只要有持行就好不用判斷是否成功
        BlackList.checkEcBlackList(context)
    }

    private fun checkVersion(context: Context?) {
        val versionCheckManager = VersionCheckManager.manager
        val listener: VersionCheckManager.Listener = object : VersionCheckManager.Listener {
            override fun onFinish(result: String?) {
                // ONLY FAIL COMES HERE
                var result = result
                result = """ ${context!!.getString(R.string.checkKioskVersionFailed)} $result """.trimIndent()
                // 檢查失敗依然可以繼續往下個步驟走.
                if (isUseEasyCard) {
                    nextStep(STEP_UPDATE_BLACK_LIST, context)
                } else {
                    nextStep(STEP_GET_BANNER, context)
                }
            }

            override fun isNewestVersion(isNewestVersion: Boolean) {
                if (isNewestVersion) {
                    if (isUseEasyCard) {
                        nextStep(STEP_UPDATE_BLACK_LIST, context)
                    } else {
                        nextStep(STEP_GET_BANNER, context)
                    }
                } else {
                    if (context != null) {
                        (context as SplashActivity).changeLoadingMessage(context.getString(R.string.clickLoadingToRetry))
                    }
                }
            }

            override fun onUpdate(percent: Int) {
            }
        }
        versionCheckManager!!.setListener(listener)
        versionCheckManager.checkKioskVersion(context as Activity)
    }

    private fun getBanner(context: Context?) {
        val listener: BannerListener = object : BannerListener {
            override fun onSuccess() {
                nextStep(STEP_GET_KIOSK_IMAGE, context)
            }

            override fun onFail() {
                dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.setBannerFailed)} """.trimIndent())
            }
        }
        val bannerManager = BannerManager.getInstance(context)
        bannerManager.setListener(listener)
        bannerManager.getBanners()
    }

    private fun initEasyCardAndCheckout(context: Context?) {
        val listener: EasyCardListener = object : EasyCardListener {
            override fun onSuccess() {
                nextStep(STEP_CHECKOUT_EASY_CARD_ALL_UN_CHECKOUT, context)
            }

            override fun onFailed() {
                dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.setEasyCardFailed)} """.trimIndent())
            }

            override fun onProgress(msg: String) {}
        }
        EasyCardManager.getInstance().setEasyCardListener(listener)
        EasyCardManager.getInstance().initEcParameter(context)
    }

    private fun checkoutAllUnCheckout(context: Context?) {
        val listener: EasyCardListener = object : EasyCardListener {
            override fun onSuccess() {
                nextStep(STEP_WAIT_NCCC_READY, context)
            }

            override fun onFailed() {
                dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.loopCheckoutFailed)} """.trimIndent())
            }

            override fun onProgress(msg: String) {
                dailyMissionListener!!.postMissionStep(msg)
            }
        }
        EasyCardManager.getInstance().setEasyCardListener(listener)
        EasyCardManager.getInstance().checkoutAllUnCheckout(context)
    }

    private fun getKioskImg(context: Context?) {
        val getKioskImgApiRequest = GetKioskImgApiRequest()
        getKioskImgApiRequest.apiListener = object : ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {
                val res = Json.fromJson(body, KioskImg::class.java)
                if (res.code == 0) {
                    if (res.img_kiosk_logo_1 != null) {
                        Config.homeTitleLogoPath = ApiRequest.LOGO_IMAGE_URL + res.img_kiosk_logo_1
                    }
                    Config.bannerImg = ApiRequest.LOGO_IMAGE_URL + res.img_kiosk
                    Config.titleLogoPath = ApiRequest.LOGO_IMAGE_URL + res.img_kiosk_logo_2
                    nextStep(STEP_GET_SHOP_CONFIG, context)
                } else {
                    onFail()
                }
            }

            override fun onFail() {
                dailyMissionListener!!.unDone(""" ${context!!.getString(R.string.setKioskImgFailed)} """.trimIndent())
            }
        }
        getKioskImgApiRequest.go()
    }

    private fun getTaskMessage(step: Int, context: Context?): String {
        return when (step) {
            STEP_GET_API_TOKEN -> context!!.getString(R.string.getApiToken)
            STEP_CHECK_VERSION -> context!!.getString(R.string.checkKioskVersion)
            STEP_UPDATE_BLACK_LIST -> context!!.getString(R.string.setBlackList)
            STEP_GET_BANNER -> context!!.getString(R.string.setBanner)
            STEP_GET_KIOSK_IMAGE -> context!!.getString(R.string.setKioskImage)
            STEP_GET_SHOP_CONFIG -> context!!.getString(R.string.setShopConfig)
            STEP_GET_SALE_METHOD -> context!!.getString(R.string.setSaleMethod)
            STEP_CHECK_EASY_CARD -> context!!.getString(R.string.setEasyCard)
            STEP_CHECKOUT_EASY_CARD_ALL_UN_CHECKOUT -> context!!.getString(R.string.checkoutUnCheckoutEasyCard)
            STEP_WAIT_NCCC_READY -> context!!.getString(R.string.waitNcccReady)
            STEP_NCCC_CHECKOUT -> context!!.getString(R.string.ncccCheckout)
            STEP_INIT_PRINTER -> context!!.getString(R.string.initPrinter)
            STEP_CASH_MODULE -> context!!.getString(R.string.init_cash_module)
            STEP_FINISH -> context!!.getString(R.string.finish)
            else -> context!!.getString(R.string.finish)
        }
    }

    fun restart(context: Context?) {
        dailyFlow(context)
    }

    fun onContinue(context: Context?) {
        nextStep(CURRENT_STEP, context)
    }

    companion object {
        private var manager: DailyMissionManager? = null
        val instance: DailyMissionManager?
            get() {
                if (manager == null) {
                    manager = DailyMissionManager()
                }
                return manager
            }
    }

    override fun onReceived(response: String) {

        if (response.contains("ResetAll")) {
            portSettingThread = ClientThread(this, cashModuleFactory.portSetting())
            portSettingThread!!.start()
            return
        }

        if (response.contains("Reason")) {
            val showMsg = context.getString(R.string.cash_module_register_failed)
            dailyMissionListener!!.unDone(showMsg)
            portSettingThread!!.interrupt()
            resetAll = ClientThread(this, cashModuleFactory.resetAll())
            resetAll!!.start()
        } else {
            SharePrefsUtils.putPortSettingDone(context, true)
            portSettingThread!!.interrupt()
            nextStep(STEP_FINISH, context)
        }
    }
}
