package com.lafresh.kiosk.model

/**
 * Created by Kyle on 2021/3/2.
 */
data class GetProductCategoryParams(
    val op: String,
    val authkey: String,
    val shop_id: String,
)
