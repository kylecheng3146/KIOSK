package com.lafresh.kiosk.model

/**
 * Created by Kyle on 2021/4/28.
 */

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

data class AddMoneyModel(
    @SerializedName("Command")
    val command: String,

    @SerializedName("TaskID")
    val taskID: String,

    @SerializedName("Result")
    val result: List<Result>
)

data class Result(
    @SerializedName("Name")
    val name: String,

    @SerializedName("ID")
    val id: String,

    @SerializedName("Status")
    val status: String,

    @SerializedName("ErrorMessage")
    val errorMessage: String
)
