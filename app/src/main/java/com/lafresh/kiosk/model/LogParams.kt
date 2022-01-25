package com.lafresh.kiosk.model

import kotlinx.serialization.SerialName

/**
 * Created by Kyle on 2021/5/24.
 */
data class LogParams(
    @SerialName("file_name")
    val file_name: String,

    @SerialName("shop_id")
    val shop_id: String,

    val type: String,

    @SerialName("resource_id")
    val resource_id: String,

    @SerialName("service_id")
    val service_id: String,

    @SerialName("service_version")
    val service_version: String,

    val msg: String,

    @SerialName("msg_data")
    val msg_data: String,

    @SerialName("msg_detail")
    val msg_detail: String
)
