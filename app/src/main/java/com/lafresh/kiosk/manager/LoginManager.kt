package com.lafresh.kiosk.manager

import com.lafresh.kiosk.Config
import com.lafresh.kiosk.httprequest.model.Auth
import com.lafresh.kiosk.httprequest.model.AuthParameter
import com.lafresh.kiosk.httprequest.model.Member
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager.Companion.instance
import com.lafresh.kiosk.model.GuestLoginParams
import com.lafresh.kiosk.model.GuestLoginRes
import com.lafresh.kiosk.model.MemberKey
import com.lafresh.kiosk.model.MemberCardLoginParams
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginManager {
    private var listener: Listener? = null
    private val memberApi = instance!!.getApiServices(Config.cacheUrl)
    //用卡號取得 accKey
    fun loginByCardNumber(cardNo: String) {
        val memberCardLoginParams = MemberCardLoginParams()
        memberCardLoginParams.card_no = cardNo
        memberApi.memberCardLogin(Config.token, memberCardLoginParams).enqueue(object : Callback<Member> {
            override fun onResponse(call: Call<Member>, response: Response<Member>) {
                if (response.isSuccessful) {
                    val accKey = response.body()!!.accKey
                    getMemberToken(accKey)
                } else {
                    listener!!.onFail()
                }
            }

            override fun onFailure(call: Call<Member>, t: Throwable) {
                listener!!.onFail()
            }
        })
    }

    //用accKey 換取 token
    fun getMemberToken(accKey: String?) {
        val authApi = instance!!.getApiServices(Config.cacheUrl)
        val parameter = AuthParameter()
        parameter.authKey = Config.authKey
        parameter.accKey = accKey
        authApi.getToken(parameter).enqueue(object : Callback<Auth> {
            override fun onResponse(call: Call<Auth>, response: Response<Auth>) {
                if (response.isSuccessful) {
                    val token = "Bearer " + response.body()!!.token
                    getMemberData(token)
                } else {
                    listener!!.onFail()
                }
            }

            override fun onFailure(call: Call<Auth>, t: Throwable) {
                listener!!.onFail()
            }
        })
    }

    //取得會員資料
    fun getMemberData(token: String) {
        memberApi.memberData(token)!!.enqueue(object : Callback<Member?> {
            override fun onResponse(call: Call<Member?>, response: Response<Member?>) {
                if (response.isSuccessful) {
                    val member = response.body()
                    member!!.token = token
                    listener!!.onSuccess(member)
                } else {
                    listener!!.onFail()
                }
            }

            override fun onFailure(call: Call<Member?>, t: Throwable) {
                listener!!.onFail()
            }
        })
    }

    /**
     * 查詢會員狀態
     * token [Token]
     * phoneNumber [電話號碼]
     * */
    fun memberKey(phoneNumber: String,type:String) : Call<MemberKey>{
        return memberApi.getMemberKey(Config.token,phoneNumber,type)
    }

    /**
     * 訪客登入 (Guest Login)
     * token [Token]
     * type [帳號類型]
     * id [帳號]
     * */
    fun guestLogin(guestLoginParams: GuestLoginParams,) : Call<GuestLoginRes>{
        return memberApi.guestLogin(Config.token,guestLoginParams)
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    interface Listener {
        fun onSuccess(member: Member?)
        fun onFail()
    }
}
