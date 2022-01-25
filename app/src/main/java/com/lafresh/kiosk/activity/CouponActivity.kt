package com.lafresh.kiosk.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.R
import com.lafresh.kiosk.adapter.CouponAdapter
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.*
import com.lafresh.kiosk.utils.ProgressUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Kyle on 2020/10/14.
 */
@RequiresApi(Build.VERSION_CODES.M)
class CouponActivity : BaseActivity(), View.OnClickListener {
    private lateinit var adapter: CouponAdapter
    private lateinit var recycler_view: RecyclerView
    private var btnReturn: Button? = null
    private var btnConfirm: Button? = null
    private val order = OrderManager.getInstance()
    inline fun <reified T> genericType() = object : TypeToken<T>() {}.type
    private val testData = """[
        {
          "id": "TE0020",
          "name": "麥味登",
          "ticketType": "一般票券",
          "ticketKind": "禮券",
          "productName": "",
          "price": 100.0000,
          "points": 0,
          "img": null,
          "canTransfer": true,
          "desciption": "",
          "beginDate": "2019/06/21",
          "endDate": "2099/12/31"
        }
      ] """

    override fun setUI() {
        recycler_view = findViewById(R.id.recyclerView)
        btnReturn = findViewById(R.id.btn_return)
        btnReturn!!.setOnClickListener(this)
        btnConfirm = findViewById(R.id.btn_confirm)
        btnConfirm!!.setOnClickListener(this)
    }

    override fun setActions() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon)
        HomeActivity.now!!.activities.add(this)
        HomeActivity.now!!.stopIdleProof()
        setUI()
        getTicketApi()
        initLogo()
        setActions()
    }

    fun getTicketApi() {
        HomeActivity.now!!.idleProof()
        ProgressUtils.instance!!.showProgressDialog(this)
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).getTickets(Config.token, "APP")
            .enqueue(object : Callback<Tickets> {
                override fun onResponse(call: Call<Tickets>, response: Response<Tickets>) {
                    ProgressUtils.instance!!.hideProgressDialog()
                    if (response.isSuccessful) {
                            adapter = CouponAdapter(this@CouponActivity)
                            adapter.addItem(response.body()!!.tickets)
                            adapter.addTempItem()
                        // 設置adapter給recycler_view
                        recycler_view.adapter = adapter
                    } else {
                    }
                }

                override fun onFailure(call: Call<Tickets>, t: Throwable) {
                    ProgressUtils.instance!!.hideProgressDialog()
                }
            })
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btn_return -> {
                if (order.tickets.size > 0) {
                    cancelFlow()
                } else {
                    removeAllTempTickets()
                }
            }

            R.id.btn_confirm -> {
                confirmFlow()
            }
        }
    }

    private fun cancelFlow() {
        if (order.tickets.size > 0) {
            order.removeAllTickets()
            cancelFlow()
        } else {
            addTempTicketsToTickets()
        }
    }

    private fun addTempTicketsToTickets() {
        if (order.tickets.size == 0) {
            adapter.addTempTicketsToTickets()
            addTempTicketsToTickets()
        } else {
            removeAllTempTickets()
        }
    }

    private fun confirmFlow() {
        if (order.tickets.size > 0) {
            order.removeAllTickets()
            confirmFlow()
        } else {
            removeTicketPayment()
        }
    }

    private fun removeTicketPayment() {
        if (order.payments.size > 0) {
            order.removeTicketPayment()
            removeTicketPayment()
        } else {
            addAllTickets(order.tempTickets)
        }
    }

    private fun addAllTickets(tempTickets: List<Order.TicketsBean>) {
        if (order.tempTickets.size > 0 && order.tickets.size == 0) {
            order.addAllTickets(tempTickets)
            addAllTickets(tempTickets)
        } else {
            removeAllTempTickets()
        }
    }

    private fun removeAllTempTickets() {
        if (order.tempTickets.size > 0) {
            order.removeAllTempTickets()
            removeAllTempTickets()
        } else {
            finish()
        }
    }
}
