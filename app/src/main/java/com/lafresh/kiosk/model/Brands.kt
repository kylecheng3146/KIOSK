package com.lafresh.kiosk.model

import com.google.gson.annotations.SerializedName

data class Brands(
    val brands:List<Brand>
)
data class Brand(
    val img:Int,
    val shopId:String,
    val authKey: String,
)
