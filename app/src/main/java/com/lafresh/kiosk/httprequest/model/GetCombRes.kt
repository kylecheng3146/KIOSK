package com.lafresh.kiosk.httprequest.model

import com.google.gson.annotations.SerializedName

class GetCombRes {
    /**
     * code : 0
     * message : 組合商品資料
     * datacnt : 2
     * comb : [{"comb_sno":"1","prod_id":"PS00000001","subject":"鍋貼","prod_name1":"招牌鍋貼","imgfile1":"4695-1-1546586572_s.jpg","quantity":"10.0000","price":"5.0000","isdefault":"1","detail":[{"comb_sno":"1","prod_id":"PS00000002","prod_name1":"韭菜鍋貼","imgfile1":"","quantity":"10.0000","price":"5.5000"},{"comb_sno":"1","prod_id":"PS00000003","prod_name1":"韓式辣味鍋貼","imgfile1":null,"quantity":"10.0000","price":"5.5000"},{"comb_sno":"1","prod_id":"PS00000004","prod_name1":"咖哩鍋貼","imgfile1":null,"quantity":"10.0000","price":"5.5000"},{"comb_sno":"1","prod_id":"PS00000005","prod_name1":"田園蔬菜鍋貼","imgfile1":null,"quantity":"10.0000","price":"5.5000"},{"comb_sno":"1","prod_id":"PS00000013","prod_name1":"玉米鍋貼","imgfile1":null,"quantity":"10.0000","price":"5.5000"},{"comb_sno":"1","prod_id":"PS00000015","prod_name1":"韭黃魚肉鍋貼","imgfile1":null,"quantity":"10.0000","price":"6.5000"},{"comb_sno":"1","prod_id":"PS00000017","prod_name1":"蒲瓜鍋貼","imgfile1":null,"quantity":"10.0000","price":"5.0000"}]},{"comb_sno":"2","prod_id":"PS06000015","subject":"飲品八方","prod_name1":"寒天薏仁白豆漿","imgfile1":null,"quantity":"1.0000","price":"35.0000","isdefault":"1","detail":[{"comb_sno":"2","prod_id":"PS06000016","prod_name1":"寒天薏仁黑豆漿","imgfile1":null,"quantity":"1.0000","price":"35.0000"},{"comb_sno":"2","prod_id":"PS06000017","prod_name1":"寒天薏仁無糖豆漿","imgfile1":null,"quantity":"1.0000","price":"35.0000"},{"comb_sno":"2","prod_id":"PS06000022","prod_name1":"寒天真傳紅茶","imgfile1":null,"quantity":"1.0000","price":"35.0000"}]}]
     */
    var code = -999
    var message: String? = null
    var datacnt = 0
    var comb: List<CombBean>? = null

    class CombBean {
        /**
         * comb_sno : 1
         * prod_id : PS00000001
         * subject : 鍋貼
         * prod_name1 : 招牌鍋貼
         * imgfile1 : 4695-1-1546586572_s.jpg
         * quantity : 10.0000
         * price : 5.0000
         * isdefault : 1
         * detail : [{"comb_sno":"1","prod_id":"PS00000002","prod_name1":"韭菜鍋貼","imgfile1":"","quantity":"10.0000","price":"5.5000"},{"comb_sno":"1","prod_id":"PS00000003","prod_name1":"韓式辣味鍋貼","imgfile1":null,"quantity":"10.0000","price":"5.5000"},{"comb_sno":"1","prod_id":"PS00000004","prod_name1":"咖哩鍋貼","imgfile1":null,"quantity":"10.0000","price":"5.5000"},{"comb_sno":"1","prod_id":"PS00000005","prod_name1":"田園蔬菜鍋貼","imgfile1":null,"quantity":"10.0000","price":"5.5000"},{"comb_sno":"1","prod_id":"PS00000013","prod_name1":"玉米鍋貼","imgfile1":null,"quantity":"10.0000","price":"5.5000"},{"comb_sno":"1","prod_id":"PS00000015","prod_name1":"韭黃魚肉鍋貼","imgfile1":null,"quantity":"10.0000","price":"6.5000"},{"comb_sno":"1","prod_id":"PS00000017","prod_name1":"蒲瓜鍋貼","imgfile1":null,"quantity":"10.0000","price":"5.0000"}]
         */
        var comb_sno: String? = null
        var prod_id: String? = null
        var subject: String? = null
        var prod_name1: String? = null
        var imgfile1: String? = null
        var quantity = 0
        var price: String? = null
        var isdefault: String? = null

        @SerializedName("is_stop_sale")
        var isStopSale = false
        @SerializedName("is_hidden")
        var isHidden = false
        var detail: List<DetailBean>? = null

        class DetailBean {
            /**
             * comb_sno : 1
             * prod_id : PS00000002
             * prod_name1 : 韭菜鍋貼
             * imgfile1 :
             * quantity : 10.0000
             * price : 5.5000
             */
            var comb_sno: String? = null
            var prod_id: String? = null
            var prod_name1: String? = null
            var imgfile1: String? = null
            var quantity = 0
            var price: String? = null

            @SerializedName("is_stop_sale")
            var isStopSale = false
            @SerializedName("is_hidden")
            var isHidden = false
        }
    }
}
