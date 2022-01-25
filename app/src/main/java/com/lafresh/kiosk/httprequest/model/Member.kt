package com.lafresh.kiosk.httprequest.model

import com.google.gson.annotations.SerializedName

class Member {

    /**
     * id : 021100543
     * name : Jane
     * zip : null
     * address : 台北市大安區復興南路二段171巷20號1樓
     * telephone : 0227006275#20
     * sex : Female
     * birthday : 2020-01-01T00:00:00
     * email : janeli@lafresh.com.tw
     * mobile : 0981959679
     * points : 0.0000
     * group_id : APP
     * group_name : 品牌APP會員
     * card_no :
     * phone_carrier :
     */
    var id: String? = null
    var name: String? = null
    var zip: String? = null // 郵遞區號
    var address: String? = null
    var telephone: String? = null
    var sex: String? = null
    var birthday: String? = null
    var email: String? = null
    var mobile: String? = null
    var points: String? = null
    var appliedPoints: Int = 0
    var group_id: String? = null
    var group_name: String? = null

    @SerializedName("phone_carrier")
    var phoneCarrier: String? = null

    @SerializedName("card_no")
    var cardNo: String? = null

    @SerializedName("acckey")
    var accKey: String? = null

    var token: String? = null
}
