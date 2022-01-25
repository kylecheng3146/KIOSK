package com.lafresh.kiosk.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.lafresh.kiosk.*
import com.lafresh.kiosk.dialog.ConfigDialog
import com.lafresh.kiosk.dialog.ConfigDialog.SettingConfigListener
import com.lafresh.kiosk.easycard.EasyCard
import com.lafresh.kiosk.fragment.MessageDialogFragment.MessageDialogFragmentListener
import com.lafresh.kiosk.manager.DailyMissionManager
import com.lafresh.kiosk.manager.DailyMissionManager.DailyMissionListener
import com.lafresh.kiosk.manager.FileManager
import com.lafresh.kiosk.manager.VersionCheckManager
import com.lafresh.kiosk.model.LogParams
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.timertask.LogTimerTask
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.utils.*
import java.util.*
import pl.droidsonroids.gif.GifImageView
import print.Print

@RequiresApi(Build.VERSION_CODES.M)
class SplashActivity : BaseActivity(), SettingConfigListener {

    private var loadingImage: GifImageView? = null
    private var loadingMessage: TextView? = null
    private var tvMissionStep: TextView? = null
    private var listener: DailyMissionListener? = null
    private var vLeft: View? = null
    private var vCenter: View? = null
    private var vRight: View? = null
    private var configKey = ""
    private var settingTimer: CountDownTimer? = null
    private var errorMessage = ""
    private var BTAddress: String? = null
    var REQUESTCODE_FROM_ACTIVITY = 1000

    override fun onResume() {
        super.onResume()
        configKey = "?" // 保證不點清空不能進設定
    }

    override fun setUI() {
        loadingImage = findViewById(R.id.gif_loading)
        loadingMessage = findViewById(R.id.loading_message)
        tvMissionStep = findViewById(R.id.tvMissionStep)
        vLeft = findViewById(R.id.vLeft)
        vCenter = findViewById(R.id.vCenter)
        vRight = findViewById(R.id.vRight)
    }

    override fun setActions() {
        vCenter!!.setOnClickListener { configKey = "" }
        vLeft!!.setOnClickListener { configKey += "0" }
        vRight!!.setOnClickListener {
            configKey += "1"
            if (configKey == "010101") {
                if (settingTimer != null) {
                    settingTimer!!.cancel()
                }
                val configDialog = ConfigDialog(this@SplashActivity)
                configDialog.show()
                configDialog.setListener(this@SplashActivity)
                configKey = ""
            }
        }
        listener = object : DailyMissionListener {
            override fun allDone() {
                setInitLog()
                val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun unDone(msg: String?) {
                runOnUiThread {
                    errorMessage = msg!!
                    showErrorDialog()
                }
            }

            override fun progressMessage(msg: String?) {
                runOnUiThread { loadingMessage!!.text = msg }
            }

            override fun postMissionStep(msg: String?) {
                runOnUiThread { tvMissionStep!!.append(msg) }
            }
        }

        validateConnectBT()

        loadingImage!!.setOnClickListener {
            if (!isNewestVersion) {
                checkAndUpdateKiosk()
            }
        }
    }

    /**
     * 初始化資料寫入elk平台查詢
     * */
    private fun setInitLog() {
        val version: String =
            getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
        CommonUtils.setLog(
            LogParams(
                "kiosk",
                Config.shop_id,
                "order",
                Config.shop_id + "_" + Config.kiosk_id,
                "KIOSK",
                version,
                "初始化完成",
                "",
                Config.getAllConfig()
            )
        )
    }

    fun validateConnectBT() {
        if (BuildConfig.FLAVOR == FlavorType.kinyo.name) {
            if (KioskPrinter.getPrinter() == null) {
    //                ConnectUtils.connectionBluetooth(this);
                ConnectUtils.connectionWifi(this)
                KioskPrinter.initKioskPrinter(this)
            } else {
                DailyMissionManager.instance!!.setDailyMissionListener(listener)
                DailyMissionManager.instance!!.dailyFlow(this)
            }
        } else {
            DailyMissionManager.instance!!.setDailyMissionListener(listener)
            DailyMissionManager.instance!!.dailyFlow(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(javaClass.simpleName, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        if (BuildConfig.DEBUG) {
//            detectPerformance();
//        }
        Kiosk.hidePopupBars(this)
        FileManager.init(this)
        ConfigDialog.init(this)
        setUI()
        setActions()
        // 背景上傳LOG
        startLogUploadTimer()
        if (!Config.isUseEmulator && EasyCard.hasUnUploadEcData()) {
            EasyCard.startUploadTimer()
        }

        Config.isTriggerFirebaseDatabase = false
    }

    fun startLogUploadTimer() {
        val logTimer = Timer(true)
        val repeatTime = 60 * 1000.toLong()
        logTimer.schedule(LogTimerTask(), repeatTime, repeatTime)
    }

    override fun onOk() {
        DailyMissionManager.instance!!.setDailyMissionListener(listener)
        DailyMissionManager.instance!!.restart(this)
    }

    override fun onBack() {
        DailyMissionManager.instance!!.setDailyMissionListener(listener)
        DailyMissionManager.instance!!.onContinue(this)
    }

    fun showErrorDialog() {
        Kiosk.showMessageDialogFragment(this@SplashActivity, errorMessage, object : MessageDialogFragmentListener {
            override fun onFinish() {
                loadingMessage!!.text = ""
                DailyMissionManager.instance!!.onContinue(this@SplashActivity)
            }

            override fun onDismiss() {
                validateIsNewVersion()
            }
        })
    }

    fun waitSetting() {
        settingTimer = object : CountDownTimer(15 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                loadingMessage!!.text = String.format(getString(R.string.waitingIntoSettingTime), millisUntilFinished / 1000)
            }

            override fun onFinish() {
                showErrorDialog()
            }
        }.start()
    }

    private val isNewestVersion: Boolean
        get() = VersionCheckManager.manager!!.isNewestVersion

    fun checkAndUpdateKiosk() {
        VersionCheckManager.manager!!.checkKioskVersion(this@SplashActivity)
    }

    fun changeLoadingMessage(msg: String?) {
        runOnUiThread { loadingMessage!!.text = msg }
    }

    fun detectPerformance() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll() // or detectAll() for all detectable problems
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build())
    }

    // call back by scan bluetooth printer
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            when (resultCode) {
                Print.ACTIVITY_CONNECT_BT -> {
                    BTAddress = data?.extras?.getString("BTAddress")
                    if (validateToothIsConnected()) return
                }

                Print.ACTIVITY_CONNECT_WIFI -> {
                    if (data?.extras?.getBoolean("isConnected")!!) {
                        Toast.makeText(this, getString(R.string.activity_main_connected), Toast.LENGTH_LONG).show()
                        DailyMissionManager.instance!!.setDailyMissionListener(listener)
                        DailyMissionManager.instance!!.dailyFlow(this)
                    } else {
                        showWifiConnectionErrorDialog()
                    }
                    return
                }
            }
        } catch (e: Exception) {
            AlertUtils.showMsgWithConfirmBlueTooth(this@SplashActivity, R.string.activity_main_scan_error)
            Log.e("HPRTSDKSample", StringBuilder("Activity_Main --> onActivityResult ").append(e.message).toString())
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun validateToothIsConnected(): Boolean {
        if (CommonUtils.isEmpty(BTAddress)) {
            AlertUtils.showMsgWithConfirmBlueTooth(
                this@SplashActivity,
                R.string.activity_main_scan_error
            )
            return true
        } else {
            Toast.makeText(this, getString(R.string.activity_main_connected), Toast.LENGTH_LONG)
                .show()
            DailyMissionManager.instance!!.setDailyMissionListener(listener)
            DailyMissionManager.instance!!.dailyFlow(this)
            return true
        }
    }

    fun showWifiConnectionErrorDialog() {
        Kiosk.showMessageDialogFragment(this@SplashActivity, this.getString(R.string.wifi_connect_failure), object : MessageDialogFragmentListener {
            override fun onFinish() {
                loadingMessage!!.text = ""
                ConnectUtils.connectionWifi(this@SplashActivity)
            }

            override fun onDismiss() {
                validateIsNewVersion()
            }
        })
    }

    fun validateIsNewVersion() {
        if (isNewestVersion) {
            waitSetting()
        } else if (!NetworkUtils.isOnline(this@SplashActivity)) {
            changeLoadingMessage(getString(R.string.checkNetwork))
        } else {
            changeLoadingMessage(getString(R.string.clickLoadingToRetry))
        }
    }
}
