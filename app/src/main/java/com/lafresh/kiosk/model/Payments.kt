package com.lafresh.kiosk.model

/**
 * Created by Kevin on 2020/11/9.
 */

data class Payments(
    val payment_methods: List<PaymentMethod>
)

data class PaymentMethod(
    val id: String,
    val linePayProductName: String? = null,
    val name: String
)
