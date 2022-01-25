package com.lafresh.kiosk.model

data class GetAccountingInformation(
    val revenue: Revenue,   //營業額
    val discount: Money,    //總折扣讓
    val total_sale: Money,  //銷售總額
    val receipt: Receipt,   //銷售總額
    val other: Other        //其他
)

data class Revenue(
    val revenue_items: List<RevenueItem>?,  //營業總表
    val total_revenue: Money,   //營業總額
)

data class RevenueItem(
    val type: String,   //付款方式
    val payment_amount: Money,  //支付金額
)

data class Money(
    val amount: Float,    //最小單位金額
    val currency_code: String,  //貨幣代碼 ISO 4217 code
    val formatted_amount: String,    //顯示用金額
)

data class Receipt(
    val count: Int, //發票張數
    val receipt_amount: Money,  //發票金額
    val void_receipt_count: Int,    //作廢發票張數
    val void_receipt_amount: Money, //作廢發票金額
)

data class Other(
    val customer_count: Int,    //來客數
    val sale_count: Int,        //銷售數量
    val average_price: Float    //平均客單價
)