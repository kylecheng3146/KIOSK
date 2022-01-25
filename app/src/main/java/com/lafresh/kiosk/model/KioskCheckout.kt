package com.lafresh.kiosk.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by Kyle on 2020/12/16.
 */
@IgnoreExtraProperties
data class KioskCheckout(
        var has_checked: Boolean? = false
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "has_checked" to has_checked,
        )
    }
}


