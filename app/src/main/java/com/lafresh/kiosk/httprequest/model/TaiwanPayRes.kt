package com.lafresh.kiosk.httprequest.model

data class TaiwanPayRes(
    val respDesc: String,
    val orderNumber: String,
    val txnDir: String,
    val endpointCode: String,
    val txnCurrency: String,
    val sign: String,
    val terminalId: String,
    val storeId: String,
    val txnDateTime: String,
    val txnAccNO: String,
    val txnSeqno: String,
    val txnAmt: String,
    val respCode: String
)
