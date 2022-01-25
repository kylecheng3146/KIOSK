package com.lafresh.kiosk.httprequest.model

import com.google.gson.annotations.SerializedName

data class TaiwanPayReq(

    @SerializedName("shop_id")
    var shopId: String? = null,

    @SerializedName("amount")
    var amount: Int? = null,

    @SerializedName("orderNumber")
    var orderNumber: String? = null,

    @SerializedName("qrcode")
    var qrcode: String? = null,

    @SerializedName("terminalId")
    var terminalId: String? = null,

    @SerializedName("retry_time")
    var retryTime: Int? = null
)
