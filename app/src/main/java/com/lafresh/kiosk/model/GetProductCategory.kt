package com.lafresh.kiosk.model

/**
 * Created by Kyle on 2021/3/2.
 */
data class GetProductCategory(
    val code: Long,
    val message: String,
    val datacnt: Long,
    val prod_model: String,
    val imgpath: String,
    val data: List<Datum>
)

data class Datum(
    val serno: String,
    val subject: String,
    val imgfile1: Any? = null,
    val imgfile2: Any? = null,
    val istag: Long,
    val ishome: Long
)
