package com.lafresh.kiosk.httprequest.model

data class CreateOrderRes(
    /**
     * id : STORE2005010001
     * current_state : ACCEPTED
     * create_at : 2020-05-01T22:25:49Z
     * is_get_receipt : true
     */
    var id: String,
    var current_state: String,
    var create_at: String,
    var is_get_receipt: Boolean = false
)
