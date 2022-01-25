package com.lafresh.kiosk.model

/**
 * Created by Kyle on 2021/4/27.
 */

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.json.*
data class TransactionModel (
    @SerializedName("Command")
    val command: String,

    @SerializedName("TaskID")
    val taskID: String,

    @SerializedName("Amount")
    val amount: Long,

    @SerializedName("CollectDetails")
    val collectDetails: List<CollectDetail>,

    @SerializedName("Status")
    val status: String,

    @SerializedName("CashBoxStatus")
    val cashBoxStatus: List<CashBoxStatus>,

    @SerializedName("Detail")
    val detail: List<Detail>,

    @SerializedName("UnchangeAmount")
    val unchangeAmount: Long,

    @SerializedName("Result")
    val result: String,

    @SerializedName("Name")
    val name: String,
)

data class CashBoxStatus (
    @SerializedName("Denomination")
    val denomination: Int,

    @SerializedName("Quantity")
    val quantity: Int
)

data class CollectDetail (
    @SerializedName("Time")
    val time: String,

    @SerializedName("Amount")
    val amount: Int
)

data class Detail (
    @SerializedName("Currency")
    val currency: Int,

    @SerializedName("Quantity")
    val quantity: Int
)
