package com.lafresh.kiosk.model

/**
 * Created by Kyle on 2020/11/26.
 */

data class BasicSettings(
    val register_url: String,
    val available_days: Long,
    val has_points: Boolean,
    val use_points: Boolean,
    val maximum_order_amount: Long,
    val table_no_type: String,
    val preparation_time: Int,
    val time_interval: Int,
    val kiosk_product_name_type: String?,
    var use_member_carrier: Boolean,
    val member_carrier_type: String?,
    val print_order_qrcode: Boolean,
    val use_member: Boolean
)
