package com.lafresh.kiosk.httprequest.model

import com.google.gson.annotations.SerializedName

class Banners {
    @SerializedName("banners")
    var bannerList: List<Banner>? = null

    class Banner {
        /**
         * id : 101
         * position : 0
         * subject : 梁社漢排骨
         * image_url : https://www.lafresh.com.tw/kiosk/apptest/public/banners/101-2-1577692663.png
         * redirect_url :
         */
        var id: String? = null
        var position: String? = null
        var subject: String? = null
        var image_url: String? = null
        var redirect_url: String? = null
    }
}
