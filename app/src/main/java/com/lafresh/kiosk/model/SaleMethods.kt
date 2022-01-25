package com.lafresh.kiosk.model
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
/**
 * Created by Kyle on 2021/10/19.
 */

data class SaleMethods(
    @SerialName("sale_methods")
    val sale_methods: List<SaleMethod>
)

data class SaleMethod(
    val id: String,
    val name: String,
    @SerialName("has_address")
    val has_address: Boolean,
    var type: String
)
