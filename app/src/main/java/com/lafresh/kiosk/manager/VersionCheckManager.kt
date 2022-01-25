package com.lafresh.kiosk.manager

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lafresh.kiosk.BuildConfig
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.R
import com.lafresh.kiosk.dialog.DownloadApkDialog
import com.lafresh.kiosk.httprequest.ApiRequest
import com.lafresh.kiosk.httprequest.ApiRequest.ApiListener
import com.lafresh.kiosk.httprequest.CheckKioskVerApiRequest
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.utils.ComputationUtils
import com.lafresh.kiosk.utils.FileUtil
import com.lafresh.kiosk.utils.Json
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VersionCheckManager private constructor() {
    private val APK_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/apk/"
    var isNewestVersion = false
        private set
    private var fileSize = 0
    private var fileName = ""
    private var listener: Listener? = null
    private var checkKioskVerApiRequest: CheckKioskVerApiRequest? = null
    var testApkName: String? = null
        private set
    var testApkSize = 0
        private set
    val isApkDownloaded: Boolean
        get() {
            if (fileName.length == 0) {
                return false
            }
            val file = File(APK_PATH, fileName)
            return file.exists()
        }

    fun checkKioskVersion(activity: Activity) {
        val apiListener: ApiListener = object : ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {
                val response = Json.fromJson(body, CheckKioskVerApiRequest.Response::class.java)
                if (response.code == 0) {
                    testApkName = response.kiosk_test_apk
                    testApkSize = countApkSize(response.kiosk_test_apk_size)
                    val kioskVer = response.kiosk_ver
                    if (!isNeedUpdate(kioskVer)) {
                        isNewestVersion = true
                        if (listener != null) listener!!.isNewestVersion(true)
                    } else {
                        isNewestVersion = false
                        if (listener != null) {
                            listener!!.isNewestVersion(false)
                        }
                        fileName = response.kiosk_apk
                        val apkSize = response.kiosk_apk_size
                        fileSize = countApkSize(apkSize)
                        checkAndRenameOldFile(fileName)
                        activity.runOnUiThread { downloadApk(activity) }
                    }
                } else {
                    onFail()
                }
            }

            override fun onFail() {
                activity.runOnUiThread {
                    val message = """
                        ${activity.getString(R.string.errorOccur)}
                        ${activity.getString(R.string.connectionError)}
                        """.trimIndent()
                    if (listener != null) {
                        listener!!.onFinish(message)
                    }
                }
            }
        }
        checkKioskVerApiRequest = CheckKioskVerApiRequest()
        checkKioskVerApiRequest!!.setApiListener(apiListener).go()
    }

    fun cancelCheckKioskVerApiRequest() {
        if (checkKioskVerApiRequest != null) {
            checkKioskVerApiRequest!!.cancel(true)
        }
    }

    private fun isNeedUpdate(serverVersion: String?): Boolean {
        if (serverVersion == null || serverVersion.length == 0) {
            return false
        }
        val nowVersion = BuildConfig.VERSION_NAME.substring(0, BuildConfig.VERSION_NAME.lastIndexOf("-")).substring(0, 5)
        if (nowVersion == serverVersion) {
            return false
        }
        val serverV = serverVersion.split("\\.".toRegex()).toTypedArray()
        val nowV = nowVersion.split("\\.".toRegex()).toTypedArray()
        val len = nowV.size
        if (len != serverV.size) {
            return false
        }
        for (i in 0 until len) {
            val fromServer = ComputationUtils.parseInt(serverV[i])
            val now = ComputationUtils.parseInt(nowV[i])
            if (fromServer > now) {
                return true
            }
        }
        return false
    }

    fun downloadApk(activity: Activity?) {
        DownloadApkDialog(activity!!, fileName).show()
    }

    fun downloadApk(fileName: String) {
        try {
            deleteFile()
            RetrofitManager.instance!!.getApiServices(Config.cacheUrl).downloadFile("v1/download_kiosk_apk/?filename=" + fileName)
            ?.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    val body = response.body()
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    } else {
                        writeFile(body, fileName)
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                }
            })
//            val listener = DownloadApkRequest.Listener { fileName -> handleStoredApk(fileName) }
//            val size: Int
//            size = if (fileName == this.fileName) {
//                fileSize
//            } else {
//                testApkSize
//            }
//            val downloadApkRequest = DownloadApkRequest(APK_PATH, fileName, size, listener, pgb)
//            downloadApkRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun writeFile(body: ResponseBody?, fileName: String) {

        GlobalScope.launch {
            val file = File(APK_PATH + fileName)
            var inStream: InputStream? = null
            var outStream: OutputStream? = null
            /*注意,在kotlin中沒有受檢異常,
                        如果這裏不寫try catch,編譯器也是不會報錯的,
                        但是我們需要確保流關閉,所以需要在finally進行操作*/
            try {
                // 以下讀寫文件的操作和java類似
                inStream = body!!.byteStream()
                outStream = file.outputStream()
                // 當前已下載長度
                var currentLength = 0L
                // 緩衝區
                val buff = ByteArray(1024)
                var len = inStream.read(buff)
                var percent = 0
                while (len != -1) {
                    outStream.write(buff, 0, len)
                    currentLength += len
                    /*不要頻繁的調用切換線程,否則某些手機可能因爲頻繁切換線程導致卡頓,
                        這裏加一個限制條件,只有下載百分比更新了才切換線程去更新UI*/
                    if ((currentLength * 100 / fileSize).toInt() > percent) {
                        percent = (currentLength * 100 / fileSize).toInt()
                        // 切換到主線程更新UI
                        withContext(Dispatchers.Main) {
                            if (listener != null) {
                                listener!!.onUpdate(percent)
                            }
                        }
                    }
                    len = inStream.read(buff)
                }
                // 下載完成之後,切換到主線程更新UI
                withContext(Dispatchers.Main) {
                    handleStoredApk(fileName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                inStream?.close()
                outStream?.close()
            }
        }
    }

    private fun deleteFile() {
        val oldestFile = FileUtil.getOldestFileInDir(APK_PATH)
        if (oldestFile != null) {
            oldestFile.delete()
        }
    }

    private fun handleStoredApk(fileName: String) {
        // 要執行的檔案名
        this.fileName = fileName
        // 存完檔案後
        if (listener != null) {
            listener!!.onFinish(fileName)
        }
        val fileSize = FileUtil.checkDirSize(APK_PATH)
        if (fileSize > 2) {
            val oldestFile = FileUtil.getOldestFileInDir(APK_PATH)
            val isDeleted = oldestFile.delete()
            Log.d(javaClass.simpleName, "Deleted File Name = " + oldestFile.name)
        }
    }

    fun installApk(fileName: String, context: Context) {
        val path = APK_PATH + fileName
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uriFromFile(context, File(path)), "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Log.e("TAG", "Error in opening the file!")
        }
    }

    private fun uriFromFile(context: Context, file: File): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".FileProvider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    private fun checkAndRenameOldFile(fileName: String) {
        val file = File(APK_PATH, fileName)
        val nowVersion = "(" + BuildConfig.VERSION_NAME + ")"
        if (file.exists()) {
            val isRename = file.renameTo(File(APK_PATH, nowVersion + fileName))
            Log.d(javaClass.simpleName, "moved APK name = $nowVersion")
        }
    }

    private fun countApkSize(apkSize: String?): Int {
        if (apkSize != null && apkSize.endsWith("MB")) {
            val sizeString = apkSize.substring(0, apkSize.indexOf("MB"))
            return (ComputationUtils.parseDouble(sizeString) * 1024 * 1024).toInt()
        }
        return 0
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    interface Listener {
        fun onFinish(result: String?)
        fun isNewestVersion(isNewestVersion: Boolean)
        fun onUpdate(percent: Int)
    }

    companion object {
        @JvmStatic
        var manager: VersionCheckManager? = null
            get() {
                if (field == null) {
                    field = VersionCheckManager()
                }
                return field
            }
            private set
    }

    init {
        val file = File(APK_PATH)
        if (!file.exists()) {
            file.mkdir()
        }
    }
}
