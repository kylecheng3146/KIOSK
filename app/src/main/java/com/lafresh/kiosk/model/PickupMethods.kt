package com.lafresh.kiosk.model

import com.google.gson.annotations.SerializedName

data class PickupMethods(

    @SerializedName("pickup_methods")
    val pickup_methods: List<PickupMethod?>? = null
)

data class PickupMethod(

    @SerializedName("min_amount")
    val minAmount: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("has_address")
    val hasAddress: Boolean? = null
)
