package com.lafresh.kiosk.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.lafresh.kiosk.BuildConfig
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.manager.FileManager
import com.lafresh.kiosk.model.GrokLog

@RequiresApi(Build.VERSION_CODES.M)
object LogUtil {
    // 寫到本地端檔案，再由背景服務上傳
    @JvmStatic
    fun writeLogToLocalFile(logData: String?) {
        if (logData == null) {
            return
        }
        val builder = basicLogDataStringBuilder
        val fileLogData = builder.append(logData).append("\n").toString()
        val fileName = logFileName
        FileManager.writeLogFile(fileName, fileLogData)
    }

    private val basicLogDataStringBuilder: StringBuilder
        get() {
            val builder = StringBuilder("\n")
            builder.append("LogTime:").append(TimeUtil.getNowTimeToMs()).append("\n")
                .append("AuthKey:").append(Config.authKey).append("\n")
                .append("ShopId:").append(Config.shop_id).append("\n")
                .append("KioskId:").append(Config.kiosk_id).append("\n")
                .append("KioskVersion:").append(BuildConfig.FLAVOR).append(BuildConfig.VERSION_NAME)
                .append("\n")
                .append("LogData:")
            return builder
        }

    // 寫到本地端檔案，再由背景服務上傳
    fun writeGrokLogToLocalFile(logData: String?) {
        val fileName = logFileName
        FileManager.writeLogFile(fileName, logData)
    }

    val grokLog: GrokLog
        get() {
            val grokLog = GrokLog()
            grokLog.setLogDatetime(TimeUtil.getNowTimeToMs())
            grokLog.setCompanyId(Config.authKey)
            grokLog.setShopId(Config.shop_id)
            grokLog.setDeviceId(Config.kiosk_id)
            grokLog.setServiceVersion(BuildConfig.FLAVOR + BuildConfig.VERSION_NAME)
            return grokLog
        }

    // 一天86,400,000毫秒, long data有13位。切後面10位10天內也不會重複
    val logFileName: String
        get() {
            val nowTimeMs = System.currentTimeMillis()
            val msString = nowTimeMs.toString()
            // 一天86,400,000毫秒, long data有13位。切後面10位10天內也不會重複
            return msString.substring(3)
        }

    fun writeEcDataToLocalFile(dataJson: String?) {
        if (dataJson == null) {
            return
        }
        FileManager.writeEcData(logFileName, dataJson)
    }

    @JvmStatic
    fun parseExceptionToString(e: Exception?): String {
        return Log.getStackTraceString(e)
    }
}
