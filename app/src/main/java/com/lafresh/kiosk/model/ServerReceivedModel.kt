package com.lafresh.kiosk.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kyle on 2021/5/4.
 */
data class ServerReceivedModel(
    @SerializedName("TimeStamp")
    val timeStamp: String,

    @SerializedName("ModelName")
    val moduleName: String,

    @SerializedName("Command")
    val command: String,

    @SerializedName("Amount")
    val amount: Int,

    @SerializedName("Result")
    val result: String,

    @SerializedName("DoorIndex")
    val doorIndex: String
)
