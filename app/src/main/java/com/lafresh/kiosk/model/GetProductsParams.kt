package com.lafresh.kiosk.model

/**
 * Created by Kyle on 2021/2/24.
 */
data class GetProductsParams(
    val op: String,
    val authkey: String,
    val acckey: String,
    val shop_id: String,
    val sale_method: String,
    val dep_id: String?,
    val meal_time: String,
    val vip_group_id: String
)
