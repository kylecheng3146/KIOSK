package com.lafresh.kiosk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.R
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.manager.BasicSettingsManager.Companion.instance
import com.lafresh.kiosk.model.Item
import com.lafresh.kiosk.model.LogParams
import com.lafresh.kiosk.model.Product
import com.lafresh.kiosk.module.GlideApp
import com.lafresh.kiosk.module.GlideApp.with
import java.security.MessageDigest
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * utility method everyone can use it
 * Created by kevin on 22/09/2017.
 */
@RequiresApi(Build.VERSION_CODES.M)
object CommonUtils {
    private const val TAG = "CommonUtils"
    private val msgToasts = ArrayList<Toast>()
    var manager: FragmentManager? = null
    @SuppressLint("all")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun hideSoftKeyboard(activity: AppCompatActivity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showDialog(title: String, message: String, fragmentManager: android.app.FragmentManager) {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment
            .setTitle(title)
            .setMessage(message)
            .setConfirmButton({
                alertDialogFragment.dismiss()
            })
            .setUnCancelAble()
            .show(fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }
    fun showDialog(title: String, message: String, fragmentManager: android.app.FragmentManager, confirmMsg: Int) {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment
            .setTitle(title)
            .setMessage(message)
            .setConfirmButton(confirmMsg, { alertDialogFragment.dismiss() }
            )
            .setUnCancelAble()
            .show(fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }

    /**
     * 顯示提示訊息
     * @param activity [當前頁面]
     * @param msg [訊息]
     */
    @JvmStatic
    fun showMessage(context: Context, msg: String) {
        try {
            val t = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
            t.setGravity(Gravity.CENTER, 0, 0)
            val linearLayout = t.view as LinearLayout
            val messageTextView = linearLayout.getChildAt(0) as TextView
            messageTextView.textSize = context.resources.getDimension(R.dimen.mini_size)
            GlobalScope.launch(Dispatchers.Main) {
                t.show()
            }
            msgToasts.add(t)
        } catch (e: Exception) {
            LogUtils.error(e.message)
        }
    }

    /**
     * 移除所有Toast訊息
     */
    fun killAllMessage() {
        for (t in msgToasts) t?.cancel()
        msgToasts.clear()
    }

    /**
     * 檢查電子郵件格式
     *
     * @param email [電子郵件]
     */
    fun isEmailValid(email: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }

    /**
     * 檢查密碼長度
     *
     * @param password [密碼]
     */
    fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    fun showFragment(context: AppCompatActivity, isAddToBack: Boolean, hideFragment: Fragment?, fragment: Fragment) {
        killAllMessage()
        manager = context.supportFragmentManager
        manager!!.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentTransaction: FragmentTransaction = manager!!.beginTransaction()
//        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        // /是否要加入 fragment 堆疊.
        if (isAddToBack)
            fragmentTransaction.addToBackStack(fragment.javaClass.name)

        // 如果 fragment 已經被創建過就隱藏。
        if (hideFragment != null)
            fragmentTransaction.remove(hideFragment)

        // 如果 fragment 已經被 add 過就顯示
//        if (fragment.isAdded()){
//            (fragment as? BackPressed)?.onBackPressed(hideFragment!!)
//            fragmentTransaction.replace(R.id.container, fragment)
//        } else{
        // 不然就新增
        fragmentTransaction.replace(R.id.container, fragment)
//        }
        fragmentTransaction.commit()
    }

    /**
     * 跳轉頁面
     *
     * @param activity [頁面context]
     * @param act [activity]
     */
    fun intentActivityAndFinish(activity: AppCompatActivity, act: Class<*>?) {
        activity.startActivity(Intent(activity, act))
        activity.finish()
    }

    /**
     * 跳轉頁面
     *
     * @param activity [頁面context]
     * @param act [activity]
     */
    fun intentActivity(activity: AppCompatActivity, act: Class<*>?) {
        activity.startActivity(Intent(activity, act))
    }

    /**
     * 帶有參數的跳轉頁面
     *
     * @param context [頁面context]
     * @param act [activity]
     * @param bundle [bundle value]
     */
    fun intentActivity(context: Context, act: Class<*>?, bundle: Bundle?) {
        val intent = Intent()
        intent.setClass(context, act!!)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        context.startActivity(intent)
    }

    /**
     * 取得現在時間
     *
     * @param setTimeType [時間格式] yyyy-MM-dd HH:mm:ss
     * @return strDate
     */
    fun getCurrentTime(setTimeType: String?): String {
        val sdfDate = SimpleDateFormat(setTimeType) // dd/MM/yyyy
        sdfDate.timeZone = TimeZone.getTimeZone("Asia/Taipei")
        val now = Date()
        return sdfDate.format(now)
        //        return "2020-05-22";
    }

    fun encryptSHA256(base: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(base.toByteArray(charset("UTF-8")))
            val hexString = StringBuffer()
            for (i in hash.indices) {
                val hex = Integer.toHexString(0xff and hash[i].toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            hexString.toString()
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }

    val currentTimeStamp: String
        get() {
            val tsLong = System.currentTimeMillis() / 1000
            return java.lang.Long.toString(tsLong)
        }

    @Throws(ParseException::class)
    fun isDayExpired(day: String?): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        // 當前時間
        val pre = Calendar.getInstance()
        val predate = Date(System.currentTimeMillis())
        pre.time = predate

        // 設定的時間
        val cal = Calendar.getInstance()
        val date = sdf.parse(day)
        cal.time = date
        return if (pre[Calendar.YEAR] == cal[Calendar.YEAR]) {
            val diffDay = pre[Calendar.DAY_OF_YEAR] - cal[Calendar.DAY_OF_YEAR]
            val diffHour = pre[Calendar.HOUR_OF_DAY] - cal[Calendar.HOUR_OF_DAY]
            val diffMin = pre[Calendar.MINUTE] - cal[Calendar.MINUTE]
            if (diffDay == 0) {
                if (diffHour == 0) {
                    diffMin >= 0
                } else diffHour > 0
            } else diffDay > 0
        } else pre[Calendar.YEAR] > cal[Calendar.YEAR]
    }

    @JvmStatic
    val time: String
        get() {
            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH] + 1
            val day = c[Calendar.DAY_OF_MONTH]
            val hour = c[Calendar.HOUR_OF_DAY]
            val minute = c[Calendar.MINUTE]
            val second = c[Calendar.SECOND]
            val M = if (month <= 9) "0$month" else month.toString() + ""
            val D = if (day <= 9) "0$day" else day.toString() + ""
            val hh = if (hour <= 9) "0$hour" else hour.toString() + ""
            val mm = if (minute <= 9) "0$minute" else minute.toString() + ""
            val ss = if (second <= 9) "0$second" else second.toString() + ""
            val YMD = "$year-$M-$D"
            val hhmmss = "$hh:$mm:$ss"
            return "$YMD $hhmmss"
        }

    @JvmStatic
    fun getPayName(ct: Context, payId: String?): String {
        return when (payId) {
            "K" -> ct.getString(R.string.easyCard)
            "Q004" -> ct.getString(R.string.coupon)
            "Q006" -> ct.getString(R.string.linePay)
            "Q011" -> ct.getString(R.string.creditCard)
            "Q012" -> ct.getString(R.string.piPay)
            else -> ""
        }
    }

    fun removeDot(d: Double): String {
        val s = d.toString() + ""
        return s.substring(0, s.length - 2)
    }

    @JvmStatic
    fun removeDot(d: String): String {
        return d.split("\\.".toRegex()).toTypedArray()[0]
    }

    @JvmStatic
    fun spaceIt(s: String, l: Int): String {
        var s = s
        while (s.length < l) {
            s = " $s"
        }
        return s
    }

    /**
     * 檢查傳入參數是否為空
     *
     * @param value 傳入參數 String
     */
    fun isEmpty(value: String?): Boolean {
        return TextUtils.isEmpty(value)
    }

    fun convertObjectToJson(productData: Product): JSONObject? {
        val gson = Gson()
        val jsonString = gson.toJson(productData)
        try {
            return JSONObject(jsonString)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun loadImage(ct: Context?, imageView: ImageView?, path: String?) {
        with(ct!!)
            .load(path)
            .centerCrop()
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView!!)
    }

    @JvmStatic
    fun loadImage(ct: Context?, imageView: ImageView?, path: Int?) {
        with(ct!!)
            .load(path)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView!!)
    }

    @JvmStatic
    fun loadImage(ct: Context?, errorPath: Int, imageView: ImageView?, path: String?) {
            with(ct!!)
                .load(path)
                .error(errorPath)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView!!)
    }

    private fun clearGlideCacheImage(ct: Context?) {
        GlobalScope.launch(Dispatchers.Default) {
            GlideApp.get(ct!!).clearDiskCache()
        }
        GlideApp.get(ct!!).clearMemory()
    }

    fun setLog(logParams: LogParams) {
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).setLog(Config.token, logParams)
            .enqueue(object : Callback<Response<Void>> {
                override fun onResponse(call: Call<Response<Void>>, response: retrofit2.Response<retrofit2.Response<Void>>) {
                    if (response.isSuccessful) {
                    } else {
                    }
                }

                override fun onFailure(call: Call<Response<Void>>, t: Throwable) {
                }
            })
    }

    fun switchProductNameType(item: Item): String {
        when (instance.getBasicSetting()?.kiosk_product_name_type) {
            "FIRST_NAME" -> try {
                return item.title
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            "SECOND_NAME" -> try {
                if ("" == item.secondTitle || item.secondTitle == null) {
                    return item.title
                } else {
                    return item.secondTitle
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            "ABBREVIATED_NAME" -> try {
                if ("" == item.abbreviatedTitle || item.abbreviatedTitle == null) {
                    return item.title
                } else {
                    return item.abbreviatedTitle
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            else -> {
                return item.title
            }
        }
        return item.title
    }

    fun switchProductNameType(jo: JSONObject): String {
        when (instance.getBasicSetting()?.kiosk_product_name_type) {
            "FIRST_NAME" -> try {
                 return jo.getString("prod_name1")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            "SECOND_NAME" -> try {
                if ("" == jo.getString("prod_name2") || jo.getString("prod_name2") == null) {
                     return jo.getString("prod_name1")
                } else {
                     return jo.getString("prod_name2")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            "ABBREVIATED_NAME" -> try {
                if (!jo.has("prod_shortname")) {
                     return jo.getString("prod_name1")
                } else if ("" == jo.getString("prod_shortname") || jo.getString("prod_shortname") == null) {
                     return jo.getString("prod_name1")
                } else {
                     return jo.getString("prod_shortname")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            else -> {
                return jo.getString("prod_name1")
            }
        }
        return ""
    }

    fun String?.hasValue(): Boolean {
        return !this.isNullOrEmpty()
    }

    fun <T> Collection<T>?.hasValue(): Boolean {
        return !this.isNullOrEmpty()
    }
}
