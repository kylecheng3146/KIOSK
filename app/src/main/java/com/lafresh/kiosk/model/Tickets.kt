package com.lafresh.kiosk.model

/**
 * Created by Kevin on 2020/10/14.
 */
data class Tickets (
        val tickets: List<TicketBean>
    )

data class TicketBean (
        val begin_date: String,
        val can_transfer: Boolean,
        val description: String,
        val end_date: String,
        val id: String,
        val img: String,
        val is_hidden_ticket_no: Boolean,
        val name: String,
        val points: Int,
        val price: Int,
        val product_name: String,
        val ticket_kind: String,
        val ticket_type: String,
)

