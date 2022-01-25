package com.lafresh.kiosk.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lafresh.kiosk.BuildConfig
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.Kiosk
import com.lafresh.kiosk.R
import com.lafresh.kiosk.activity.EasyCardHelperActivity
import com.lafresh.kiosk.creditcardlib.model.Code
import com.lafresh.kiosk.creditcardlib.model.Response
import com.lafresh.kiosk.httprequest.ApiRequest
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.manager.EasyCardManager
import com.lafresh.kiosk.manager.EasyCardManager.EasyCardListener
import com.lafresh.kiosk.manager.FileManager
import com.lafresh.kiosk.manager.VersionCheckManager
import com.lafresh.kiosk.model.ConfigFileData
import com.lafresh.kiosk.model.LoginParams
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.utils.*
import com.lafresh.kiosk.utils.NcccUtils.NcccTaskListener
import okhttp3.HttpUrl
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

@RequiresApi(Build.VERSION_CODES.M)
class ConfigDialog(activity: AppCompatActivity) : Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar) {

    private var tvAddress: TextView? = null
    private var tvLinePayCode: TextView? = null
    private val activity: AppCompatActivity
    private var etUsername: EditText? = null
    private var etPassword: EditText? = null
    private var actvApiRoot: AutoCompleteTextView? = null
    private var actvCacheUrl: AutoCompleteTextView? = null
    private var actvAuthKey: AutoCompleteTextView? = null
    private var etShopId: EditText? = null
    private var etKioskId: EditText? = null
    private var etAcckey: EditText? = null
    private var etComport: EditText? = null
    private val etBaudRate: EditText? = null
    private var etAddress: EditText? = null
    private var etLinePayCode: EditText? = null
    private var llNcccSetting: LinearLayout? = null
    private var btnOk: Button? = null
    private var btnClear: Button? = null
    private var btnNcccManualCheck: Button? = null
    private var btnEzcardManualCheck: Button? = null
    private lateinit var btnCheckout: Button
    private var btnBack: Button? = null
    private var cbNccc: CheckBox? = null
    private var cbDisableReceipt: CheckBox? = null
    private var cbUseEasyCard: CheckBox? = null
    private var cbDisablePrinter: CheckBox? = null
    private var cbOnlyCounter: CheckBox? = null
    private var cbNewOrderApi: CheckBox? = null
    private var cbCashEnabled: CheckBox? = null
    private var cbCashModule: CheckBox? = null
    private var cbEnableDebug: CheckBox? = null
    private var cbUseMultiBrand: CheckBox? = null
    private var listener: SettingConfigListener? = null
    private var rbRestaurant: RadioButton? = null
    private var rbShopping: RadioButton? = null
    private var rgJourdeness: RadioGroup? = null
    val crashlytics = FirebaseCrashlytics.getInstance()

    fun setUI() {
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        etLinePayCode = findViewById(R.id.et_linepay_code)
        actvApiRoot = findViewById(R.id.actv_api_root)
        actvAuthKey = findViewById(R.id.actv_auth_key)
        etShopId = findViewById(R.id.et_shopId)
        etKioskId = findViewById(R.id.et_kioskId)
        etAcckey = findViewById(R.id.et_acckey)
        etAddress = findViewById(R.id.et_address)
        cbDisableReceipt = findViewById(R.id.cb_disable_receipt)
        cbUseEasyCard = findViewById(R.id.cbUseEasyCard)
        cbCashModule = findViewById(R.id.cbCashModule)
        cbDisablePrinter = findViewById(R.id.cbDisablePrinter)
        cbOnlyCounter = findViewById(R.id.cbOnlyCounter)
        cbNewOrderApi = findViewById(R.id.cbNewOrderApi)
        cbCashEnabled = findViewById(R.id.cb_cash_enabled)
        cbNccc = findViewById(R.id.cbNccc)
        cbEnableDebug = findViewById(R.id.cbEnableDebug)
        cbUseMultiBrand = findViewById(R.id.cbMultiBrand)
        actvCacheUrl = findViewById(R.id.actv_cache_url)
        llNcccSetting = findViewById(R.id.llNcccSetting)
        etComport = findViewById(R.id.etComport)
        tvAddress = findViewById(R.id.tv_address)
        tvLinePayCode = findViewById(R.id.tv_linepay_code)
        //        etBaudRate = findViewById(R.id.etBaudRate);
        btnOk = findViewById(R.id.btn_ok)
        btnClear = findViewById(R.id.btn_clear)
        btnCheckout = findViewById(R.id.btn_checkout)
        btnNcccManualCheck = findViewById(R.id.btn_nccc_manual_check)
        btnEzcardManualCheck = findViewById(R.id.btn_ezcard_manual_check)
        rbShopping = findViewById(R.id.rbShopping)
        rbRestaurant = findViewById(R.id.rbRestaurant)
        rgJourdeness = findViewById(R.id.rgJourdeness)
        actvApiRoot!!.setText(Config.ApiRoot)
        etAddress!!.setText(Config.addressDefault)
        actvAuthKey!!.setText(Config.authKey)
        etShopId!!.setText(Config.shop_id)
        etLinePayCode!!.setText(Config.linePayPaymentCode)
        etKioskId!!.setText(Config.kiosk_id)
        etAcckey!!.setText(Config.acckeyDefault)
        actvCacheUrl!!.setText(Config.cacheUrl)
        rbRestaurant!!.isChecked = Config.isRestaurant
        rbShopping!!.isChecked = Config.isShopping
        cbUseEasyCard!!.isChecked = Config.useEasyCardByKiosk
        cbEnableDebug!!.isChecked = Config.isUseEmulator
        cbCashModule!!.isChecked = Config.isEnabledCashModule
        cbNccc!!.isChecked = Config.useNcccByKiosk
        cbDisableReceipt!!.isChecked = Config.disableReceiptModule
        cbDisablePrinter!!.isChecked = Config.disablePrinter
        cbOnlyCounter!!.isChecked = Config.isOnlyCounterPay
        cbNewOrderApi!!.isChecked = Config.isNewOrderApi
        cbCashEnabled!!.isChecked = Config.isEnabledCashPayment
        cbUseMultiBrand!!.isChecked = Config.useMultiBrand
        // 判斷如果啟用信用卡才顯示手動日結帳按鈕
        btnNcccManualCheck!!.visibility = if (Config.useNcccByKiosk) View.VISIBLE else View.GONE
        etComport!!.setText(Config.creditCardComportPath)
        //        etBaudRate.setText(Config.creditCardBaudRate + "");
        if (Config.useNcccByKiosk) {
            llNcccSetting!!.visibility = View.VISIBLE
        }
        btnBack = findViewById(R.id.btnBack)
        val tvVersion = findViewById<TextView>(R.id.tv_version)
        val version = activity.getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
        tvVersion.text = version
        if (Config.isUseEmulator) {
            findViewById<View>(R.id.ll_config).visibility = View.VISIBLE
            findViewById<View>(R.id.ll_login).visibility = View.GONE
            tvLinePayCode!!.visibility = View.VISIBLE
            etLinePayCode!!.visibility = View.VISIBLE
        } else {
            findViewById<View>(R.id.ll_config).visibility = View.GONE
            findViewById<View>(R.id.ll_login).visibility = View.VISIBLE
        }

        if (BuildConfig.FLAVOR.equals(FlavorType.cashmodule.name)) {
            btnCheckout.visibility = View.VISIBLE
        }
        if (!BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name)) {
            rgJourdeness!!.visibility = View.GONE
        }

        when (BuildConfig.FLAVOR) {
            FlavorType.galileo.name -> {
                // do nothing
            }

            else -> {
                tvAddress!!.visibility = View.GONE
                etAddress!!.visibility = View.GONE
            }
        }

        initAutoCompleteTextView(actvApiRoot!!, activity, R.array.urls)
        initAutoCompleteTextView(actvAuthKey!!, activity, R.array.keys)
        initAutoCompleteTextView(actvCacheUrl!!, activity, R.array.urls_for_cache)
    }

    private fun initAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView, appCompatActivity: AppCompatActivity, arrays: Int) {
        val array: Array<String> = appCompatActivity.getResources().getStringArray(arrays)
        val adapter = ArrayAdapter(appCompatActivity, android.R.layout.simple_expandable_list_item_1, array)
        autoCompleteTextView.threshold = 1
        autoCompleteTextView.setDropDownBackgroundDrawable(ColorDrawable(context.getResources().getColor(
            R.color.white
        )))
        autoCompleteTextView.setAdapter(adapter)
    }

    fun setActions() {
        btnOk!!.setOnClickListener { view: View? ->
            val apiRoot = actvApiRoot!!.text.toString().trim { it <= ' ' }
            val cacheUrl = actvCacheUrl!!.text.toString().trim { it <= ' ' }
            if (HttpUrl.parse(apiRoot) == null || HttpUrl.parse(cacheUrl) == null) {
                CommonUtils.showMessage(activity, "網址錯誤，請重設")
                return@setOnClickListener
            }
            // 重新寫入Config參數
            Config.ApiRoot = apiRoot
            Config.cacheUrl = cacheUrl
            Config.addressDefault = etAddress!!.text.toString().trim { it <= ' ' }
            Config.linePayUrl = Config.ApiRoot + "/webservice/line/pay.php"
            Config.authKey = actvAuthKey!!.text.toString().trim { it <= ' ' }
            Config.shop_id = etShopId!!.text.toString().trim { it <= ' ' }
            Config.kiosk_id = etKioskId!!.text.toString().trim { it <= ' ' }
            Config.acckeyDefault = etAcckey!!.text.toString().trim { it <= ' ' }
            Config.acckey = Config.acckeyDefault
            Config.useEasyCardByKiosk = cbUseEasyCard!!.isChecked
            Config.isEnabledCashModule = cbCashModule!!.isChecked
            Config.isUseEmulator = cbEnableDebug!!.isChecked
            Config.useNcccByKiosk = cbNccc!!.isChecked
            Config.useMultiBrand = cbUseMultiBrand!!.isChecked
            Config.isRestaurant = rbRestaurant!!.isChecked
            Config.isShopping = rbShopping!!.isChecked
            Config.linePayPaymentCode = etLinePayCode!!.text.toString()
            Config.creditCardComportPath = etComport!!.text.toString().trim { it <= ' ' }
            //                Config.creditCardBaudRate = ComputationUtils.parseInt(etBaudRate.getText().toString().trim());
            Config.disableReceiptModule = cbDisableReceipt!!.isChecked
            if (Config.disablePrinter != cbDisablePrinter!!.isChecked) {
                if (KioskPrinter.getPrinter() != null) {
                    KioskPrinter.getPrinter().releaseAll(activity)
                }
                Config.disablePrinter = cbDisablePrinter!!.isChecked
            }
            Config.isOnlyCounterPay = cbOnlyCounter!!.isChecked
            Config.isNewOrderApi = cbNewOrderApi!!.isChecked
            Config.isEnabledCashPayment = cbCashEnabled!!.isChecked
            ApiRequest.renewUrl()
            // 寫入檔案到Kiosk資料
            FileManager.writeFileData(FileManager.CONFIG_FILE_NAME, Config.getAllConfig())
            dismiss()
            if (listener != null) {
                listener!!.onOk()
            }
            logInfoToCrashlytics(activity)
        }
        btnNcccManualCheck!!.setOnClickListener { view: View? ->
            if (!ClickUtil.isFastDoubleClick()) {
                ProgressUtils.instance!!.showProgressDialog(activity)
                CommonUtils.showMessage(activity, "結帳中請稍候...")
                val checkListener = NcccTaskListener { response: Response ->
                    val isSuccess = Code.SUCCESS == response.code
                    activity.runOnUiThread {
                        NcccUtils.updateCheckoutDate(activity)
                        ProgressUtils.instance!!.hideProgressDialog()
                        CommonUtils.showMessage(activity, if (isSuccess) "結帳成功" else "結帳失敗")
                        dismiss()
                    }
                }
                val progressListener = NcccUtils.ProgressListener { second: Int -> Log.d("Manual_NCCC_Checkout", "Timeout:$second") }
                val checkoutTask = NcccUtils.checkoutTask(checkListener, progressListener)
                NcccUtils.getThreadPoolExecutor().execute(checkoutTask)
            }
        }
        btnEzcardManualCheck!!.setOnClickListener {
            if (!ClickUtil.isFastDoubleClick()) {
                ProgressUtils.instance!!.showProgressDialog(activity)
                val listener: EasyCardListener = object : EasyCardListener {
                    override fun onSuccess() {
                        ProgressUtils.instance!!.hideProgressDialog()
                    }

                    override fun onFailed() {
                        ProgressUtils.instance!!.hideProgressDialog()
                    }

                    override fun onProgress(msg: String) {
                        ProgressUtils.instance!!.showProgressDialog(activity)
                    }
                }
                val manager = EasyCardManager.getInstance()
                manager.setEasyCardListener(listener)
                manager.checkoutAllUnCheckout(activity)
            }
        }
        findViewById<View>(R.id.btn_login).setOnClickListener {
            if (etUsername!!.text.toString() == "lafresh") {
                if (etPassword!!.text.toString() == "24436074") {
                    findViewById<View>(R.id.ll_config).visibility = View.VISIBLE
                    findViewById<View>(R.id.ll_login).visibility = View.GONE
                    return@setOnClickListener
                }
            } else if (etUsername!!.text.toString().isNotEmpty() && etPassword!!.text.toString().isNotEmpty()) {
                    login()
                    return@setOnClickListener
            } else {
                CommonUtils.showMessage(activity, "帳號密碼有誤")
            }
        }
        btnBack!!.setOnClickListener {
            if (listener != null) listener!!.onBack()
            dismiss()
            CommonUtils.killAllMessage()
        }
        cbNccc!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                llNcccSetting!!.visibility = View.VISIBLE
            } else {
                llNcccSetting!!.visibility = View.GONE
            }
        }
        cbOnlyCounter!!.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                // 只用櫃台結帳，需關閉金流初始
                cbUseEasyCard!!.isChecked = false
                cbNccc!!.isChecked = false
            }
        }
        findViewById<View>(R.id.btnEcCheckout).setOnClickListener {
            dismiss()
            CommonUtils.intentActivityAndFinish(activity, EasyCardHelperActivity::class.java)
        }

        btnClear!!.setOnClickListener {
            if (actvApiRoot!!.hasFocus()) {
                actvApiRoot!!.setText("")
            }
            if (actvCacheUrl!!.hasFocus()) {
                actvCacheUrl!!.setText("")
            }
            if (actvAuthKey!!.hasFocus()) {
                actvAuthKey!!.setText("")
            }
        }

        btnCheckout.setOnClickListener {
            val cashModuleDialog = CashInventoryDialog(activity)
            cashModuleDialog.show()
        }

        if (!BuildConfig.FLAVOR.equals(FlavorType.cashmodule.name)) {
            cbCashModule!!.visibility = View.GONE
        }
    }

    private fun setTestApkFunction() {
        val vm = VersionCheckManager.manager
        val size = vm!!.testApkSize
        val fileName = vm.testApkName
        // 判斷有收到測試APK才執行
        if (size > 0 && fileName != null) {
            val btnTestApk = findViewById<Button>(R.id.btnTestApk)
            btnTestApk.visibility = View.VISIBLE
            btnTestApk.setOnClickListener { DownloadApkDialog(activity, fileName).show() }
        }
    }

    interface SettingConfigListener {
        fun onOk()
        fun onBack()
    }

    fun setListener(listener: SettingConfigListener?) {
        this.listener = listener
    }

    companion object {
        private const val TAG = "ConfigDialog"
        private const val CONFIG_SP_KEY = "config"
        private const val API_ROOT_KEY = "ApiRoot"
        private const val AUTH_KEY_KEY = "AuthKey"
        private const val SHOP_ID_KEY = "ShopId"
        private const val KIOSK_ID_KEY = "KioskId"
        private const val ACC_KEY_KEY = "AccKey"
        private const val CREDIT_CARD_COM = "CreditCardComport"
        private const val CREDIT_CARD_BAUD_RATE = "CreditCardBaudRate"
        private const val USE_NCCC_BY_KIOSK = "UseNcccByKiosk"
        val crashlytics = FirebaseCrashlytics.getInstance()

        /**
         * 從SD card 讀取已儲存的參數做初始化.
         */
        fun init(activity: AppCompatActivity) {
            val configJson = FileManager.readFileData(FileManager.EXTERNAL_STORAGE_PATH + FileManager.CONFIG_FILE_NAME)
            val preference = activity.getSharedPreferences(CONFIG_SP_KEY, Context.MODE_PRIVATE)
            val hasSpConfig = FileManager.isSharedPreferenceHasData(preference)
            // 判斷config有無資料
            if (configJson.length > 0) {
                val configFileData = Json.fromJson(configJson, ConfigFileData::class.java)
//                val apiDomainName = configFileData.apiRoot.substring(7, configFileData.apiRoot.lastIndexOf("/") - 11)
//                val cacheDomainName = configFileData.cacheUrl.substring(7)
//                if (!Patterns.IP_ADDRESS.matcher(apiDomainName).matches()) {
//                    Config.ApiRoot = configFileData.apiRoot
//                }
//                if (!Patterns.IP_ADDRESS.matcher(cacheDomainName).matches()) {
//                    Config.cacheUrl = configFileData.cacheUrl
//                }
                Config.addressDefault = configFileData.addressDefault
                Config.linePayUrl = configFileData.linePayUrl
                Config.authKey = configFileData.authKey
                Config.shop_id = configFileData.shop_id
                Config.kiosk_id = configFileData.kiosk_id
                Config.acckeyDefault = configFileData.acckeyDefault
                Config.acckey = configFileData.acckeyDefault
                Config.creditCardComportPath = configFileData.creditCardComportPath
                Config.useNcccByKiosk = configFileData.isUseNcccByKiosk
                Config.disableReceiptModule = configFileData.disableReceiptModule
                Config.isOnlyCounterPay = configFileData.isOnlyCounterPay
                Config.useEasyCardByKiosk = configFileData.isUseEasyCardByKiosk
                Config.disablePrinter = configFileData.isDisablePrinter
                Config.useMultiBrand = configFileData.isUseMultiBrand
                Config.isRestaurant = configFileData.isRestaurant
                Config.isShopping = configFileData.isShopping
                Config.isEnabledCashPayment = configFileData.isEnabledCashPayment
//                if (configFileData.isNewOrderApi) {
//                    Config.cacheUrl = configFileData.cacheUrl
//                }

                ApiRequest.renewUrl()
                logInfoToCrashlytics(activity)
            } else if (hasSpConfig) { // 沒有資料就先讀取share preferences 儲存的參數.
                Config.ApiRoot = preference.getString(API_ROOT_KEY, "")
                Config.linePayUrl = Config.ApiRoot + "/webservice/line/pay.php"
                Config.authKey = preference.getString(AUTH_KEY_KEY, "")
                Config.shop_id = preference.getString(SHOP_ID_KEY, "")
                Config.kiosk_id = preference.getString(KIOSK_ID_KEY, "")
                Config.acckeyDefault = preference.getString(ACC_KEY_KEY, "")
                Config.creditCardComportPath = preference.getString(CREDIT_CARD_COM, "")
                Config.useNcccByKiosk = preference.getBoolean(USE_NCCC_BY_KIOSK, true)
                ApiRequest.renewUrl()
                // 將config檔寫到外部，並刪除share preferences
                FileManager.writeFileData(FileManager.CONFIG_FILE_NAME, Config.getAllConfig())
                FileManager.clearSpData(preference)
            }
        }

        private fun logInfoToCrashlytics(context: Context) {
            try {
                throw Exception("Kiosk Information")
            } catch (e: Exception) {
                val configInfo = Config.getAllConfig()
                val macAddress = NetworkUtils.getMacAddress(context)
                val wifiIpAddress = NetworkUtils.getWIFILocalIpAddress(context)
                crashlytics.setUserId(Config.authKey + Config.shop_id + Config.kiosk_id)
                crashlytics.log("E/TAG: Config==>$configInfo")
                crashlytics.log("E/TAG: MacAddress==>$macAddress")
                crashlytics.log("E/TAG: WifiAddress==>$wifiIpAddress")
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun login() {
        ProgressUtils.instance!!.showProgressDialog(activity)
        val loginParams = LoginParams(etUsername!!.text.toString(), etPassword!!.text.toString(), Config.shop_id)
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).login(Config.token, loginParams)
            .enqueue(object : Callback<retrofit2.Response<Void>> {
                override fun onResponse(call: Call<retrofit2.Response<Void>>, response: retrofit2.Response<retrofit2.Response<Void>>) {
                    ProgressUtils.instance!!.hideProgressDialog()
                    if (response.isSuccessful) {
                        val cashModuleDialog = CashInventoryDialog(activity)
                        cashModuleDialog.show()
                        dismiss()
                    } else {
                        try {
                            val jObjError = JSONObject(response.errorBody()!!.charStream().readText())
                            val errMsg = jObjError.getString("message")
                            Kiosk.showMessageDialog(context, errMsg)
                        } catch (e: Exception) {
                            val errMsg = context.getString(R.string.login_failed)
                            Kiosk.showMessageDialog(context, errMsg)
                        }
                    }
                }

                override fun onFailure(call: Call<retrofit2.Response<Void>>, t: Throwable) {
                    val errMsg = context.getString(R.string.connectionError_message)
                    Kiosk.showMessageDialog(context, errMsg)
                    ProgressUtils.instance!!.hideProgressDialog()
                }
            })
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        init(activity)
        Kiosk.hidePopupBars(this@ConfigDialog)
        setContentView(R.layout.d_config)
        this.activity = activity
        setUI()
        setActions()
        setTestApkFunction()
        //        Config.reloadFileDataToConfig(this.ct);
    }
}
