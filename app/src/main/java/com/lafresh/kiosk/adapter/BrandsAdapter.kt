package com.lafresh.kiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.Bill
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.R
import com.lafresh.kiosk.httprequest.model.Auth
import com.lafresh.kiosk.httprequest.model.AuthParameter
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.model.Brand
import com.lafresh.kiosk.utils.ClickUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BrandsAdapter(private val mContext: Context, private val mData: List<Brand>, onBrandSelectListener: onBrandSelectListener) : RecyclerView.Adapter<BrandsAdapter.ViewHolder>() {
    // 建立ViewHolder
    public interface onBrandSelectListener {
        fun onSelect()
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 宣告元件
        val btnType: Button = itemView.findViewById(R.id.btn_type)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 連結項目布局檔list_item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payments, parent, false)
        return ViewHolder(view)
    }
    val monBrandSelectListener = onBrandSelectListener
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        // 設置tvTitle要顯示的內容\
        holder.btnType.setBackgroundResource(mData[position].img)
        holder.btnType.setOnClickListener {
            if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
            Config.authKey = mData[position].authKey
            Config.shop_id = mData[position].shopId
            getApiToken()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getApiToken() {
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
                    Bill.Now.In = false
                    monBrandSelectListener.onSelect()
                }
            }

            override fun onFailure(call: Call<Auth>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
    override fun getItemCount(): Int {
        return mData.size
    }
}
