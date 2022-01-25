package com.lafresh.kiosk.httprequest.restfulapi

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.lafresh.kiosk.BuildConfig
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.utils.LogUtil
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RequiresApi(Build.VERSION_CODES.M)
class RetrofitManager {
    private var connectTimeout = 15
    private var readTimeout = 15
    fun getApiServices(baseUrl: String): ApiServices {
        return buildRetrofit(baseUrl).create(ApiServices::class.java)
    }

    private fun buildRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private val client: OkHttpClient
        get() = OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .connectTimeout(connectTimeout.toLong(), TimeUnit.SECONDS)
                .readTimeout(readTimeout.toLong(), TimeUnit.SECONDS)
                .build()

    private val logInterceptor: HttpLoggingInterceptor
        get() = HttpLoggingInterceptor(logger()).setLevel(HttpLoggingInterceptor.Level.BODY)

    private fun logger(): HttpLoggingInterceptor.Logger {
        return object : HttpLoggingInterceptor.Logger {
            var builder = StringBuilder()
            override fun log(message: String) {
                if (BuildConfig.DEBUG) {
                    Log.wtf("@@@", message)
                }
                builder.append(message).append("@")
                if (message.contains("END HTTP") || message.contains("HTTP FAILED")) {
                    saveApiLog(builder.toString())
                }
            }
        }
    }

    private fun saveApiLog(logData: String) {
        val grokLog = LogUtil.grokLog
        grokLog.setMsgDetail(logData)
        if (Config.isNewOrderApi) {
            val orderId = OrderManager.getInstance().orderId
            if (orderId != null) {
                grokLog.setResourceId(OrderManager.getInstance().orderId)
            }
        }
        LogUtil.writeGrokLogToLocalFile(grokLog.toString())
    }

    fun setConnectTimeout(connectTimeout: Int) {
        this.connectTimeout = connectTimeout
    }

    fun setReadTimeout(readTimeout: Int) {
        this.readTimeout = readTimeout
    }

    companion object {
        private var manager: RetrofitManager? = null
        @JvmStatic
        val instance: RetrofitManager?
            get() {
                if (manager == null) {
                    manager = RetrofitManager()
                }
                return manager
            }
    }
}
