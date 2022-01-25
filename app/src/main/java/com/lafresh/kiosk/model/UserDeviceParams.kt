package com.lafresh.kiosk.model

/**
 * Created by Kyle on 2021/10/26.
 */

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

data class UserDeviceParams(
    @SerializedName("os_type")
    val osType: String,

    @SerializedName("os_version")
    val osVersion: String,

    @SerializedName("app_version")
    val appVersion: String,

    @SerializedName("machine_id")
    val machineID: String,

    @SerializedName("shop_id")
    val shopID: String
)
