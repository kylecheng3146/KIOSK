package com.lafresh.kiosk.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.*
import com.lafresh.kiosk.adapter.TimeIntervalAdapter
import com.lafresh.kiosk.httprequest.ApiRequest
import com.lafresh.kiosk.httprequest.ApiRequest.ApiListener
import com.lafresh.kiosk.httprequest.GetMealTimeApiRequest
import com.lafresh.kiosk.httprequest.GetShopApiRequest
import com.lafresh.kiosk.httprequest.model.Shop
import com.lafresh.kiosk.model.MealTime
import com.lafresh.kiosk.type.EasyCardTransactionType
import com.lafresh.kiosk.utils.CommonUtils
import com.lafresh.kiosk.utils.Json
import com.lafresh.kiosk.utils.ProgressUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Kyle on 2020/4/6.
 */
@RequiresApi(Build.VERSION_CODES.M)
class TimeIntervalActivity : BaseActivity(), View.OnClickListener {
    private var recycler_view: RecyclerView? = null
    private var btnBack: Button? = null
    private var adapter: TimeIntervalAdapter? = null
    private var ivEasyCard: ImageView? = null
    private var tvWelcomeMsg: TextView? = null
    private val testData = """{
    "code": 0,
    "message": "取餐時段資料",
    "meal_time_type": "1",
    "date": "2020-08-15",
    "datacnt": 96,
    "allday_rest": 0,
    "next_time": "09:57",
    "ext_meal_time": "",
    "data": [
        {
            "time": "00:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "00:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "00:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "00:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "01:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "01:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "01:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "01:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "02:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "02:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "02:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "02:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "03:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "03:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "03:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "03:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "04:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "04:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "04:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "04:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "05:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "05:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "05:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "05:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "06:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "06:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "06:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "06:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "07:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "07:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "07:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "07:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "08:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "08:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "08:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "08:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "09:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "09:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "09:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "09:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "10:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "10:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "10:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "10:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "11:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "11:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "11:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "11:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "12:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "12:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "12:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "12:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "13:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "13:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "13:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "13:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "14:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "14:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "14:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "14:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "15:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "15:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "15:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "15:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "16:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "16:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "16:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "16:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "17:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "17:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "17:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "17:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "18:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "18:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "18:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "18:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "19:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "19:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "19:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "19:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "20:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "20:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "20:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "20:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "21:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "21:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "21:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "21:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "22:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "22:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "22:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "22:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "23:00",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "23:15",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "23:30",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        },
        {
            "time": "23:45",
            "gap_amt_limit": "0",
            "order_amt_limit": "0",
            "ismeal": "1",
            "sales_total": 0
        }
    ]
}"""

    override fun setUI() {
        recycler_view = findViewById(R.id.recyclerView)
        btnBack = findViewById(R.id.btn_back)
        tvWelcomeMsg = findViewById(R.id.tv_welcome_msg)
        ivEasyCard = findViewById(R.id.iv_easycard)
    }

    override fun setActions() {
        btnBack!!.setOnClickListener(this)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_interval)
        HomeActivity.now!!.activities.add(this)
        HomeActivity.now!!.stopIdleProof()
        setUI()
        setActions()
        getMealTimeApi(SimpleDateFormat("yyyy-MM-dd").format(Date()))
        getShopApi()
        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        Kiosk.checkAndChangeUi(this, Config.titleLogoPath, ivLogo)
        val ivAd = findViewById<ImageView>(R.id.ivAd)
        Kiosk.checkAndChangeUi(this, Config.bannerImg, ivAd)

        // 如果未登入狀態，則隱藏登入資訊的部分.
        if (EasyCardTransactionType.LOGIN == Config.easyCardTransactionType && "" != Config.memberName) {
            ivEasyCard!!.setVisibility(View.VISIBLE)
            tvWelcomeMsg!!.setVisibility(View.VISIBLE)
            when (Config.memberName) {
                "柯文哲" -> {
                    tvWelcomeMsg!!.setText(String.format(getString(R.string.welcomeMayorMsg), Config.memberName))
                }

                "柯市長" -> {
                    tvWelcomeMsg!!.setText(String.format(getString(R.string.welcomeMayorMsg), Config.memberName))
                }

                "曾燦金" -> {
                    tvWelcomeMsg!!.setText(String.format(getString(R.string.welcomeMayorMsg), Config.memberName))
                }

                else -> {
                    tvWelcomeMsg!!.setText(String.format(getString(R.string.welcomeMsg), Config.memberName[0].toString() + "OO "))
                }
            }
            ivAd.visibility = View.GONE
        }
    }

    /**
     * 加入時間區間.
     */
    private fun getMealTimeApi(date: String) {
        HomeActivity.now!!.idleProof()
        ProgressUtils.instance!!.showProgressDialog(this)
        val getProductApiRequest = GetMealTimeApiRequest(date)
        val listener: ApiListener = object : ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {

                // 當前時刻
                val pre = Calendar.getInstance()
                val predate = Date(System.currentTimeMillis())
                pre.time = predate
                // 設置adapter給recycler_view
                val res = Json.fromJson(body, MealTime::class.java)
                if (res.code == 0) {
                    if (res.datacnt == 0) {
                        onFail()
                        return
                    }
                    runOnUiThread {
                        ProgressUtils.instance!!.hideProgressDialog()
                        // 將資料交給adapter
                        val data: MutableList<MealTime.DataBean> = ArrayList()
                        for (d in res.data!!) {
                            try {
                                if (!CommonUtils.isDayExpired(date + " " + d.time)) {
                                    data.add(d)
                                }
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                        }
                        adapter = TimeIntervalAdapter(this@TimeIntervalActivity, data)
                        // 設置adapter給recycler_view
                        recycler_view!!.adapter = adapter
                    }
                } else {
                    onFail()
                }
            }

            override fun onFail() {
                runOnUiThread {
                    ProgressUtils.instance!!.hideProgressDialog()
                    CommonUtils.showMessage(this@TimeIntervalActivity, getString(R.string.connectionError))
                }
            }
        }
        getProductApiRequest.setApiListener(listener).go()
    }

    /**
     * 加入時間區間.
     */
    private fun getShopApi() {
        ProgressUtils.instance!!.showProgressDialog(this)
        val getShopApiRequest = GetShopApiRequest(Config.shop_id)
        val listener: ApiListener = object : ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {
                val res = Json.fromJson(body, Shop::class.java)
                if (res.code == 0) {
                    if (res.datacnt == 0) {
                        onFail()
                        return
                    }
                    runOnUiThread {
                        ProgressUtils.instance!!.hideProgressDialog()
                        Config.shopName = res.data[0].shop_name
                    }
                } else {
                    onFail()
                }
            }

            override fun onFail() {
                runOnUiThread {
                    ProgressUtils.instance!!.hideProgressDialog()
                    CommonUtils.showMessage(this@TimeIntervalActivity, getString(R.string.connectionError))
                }
            }
        }
        getShopApiRequest.setApiListener(listener).go()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_back -> finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HomeActivity.now!!.activities.remove(this)
    }
}
