package com.lafresh.kiosk.model

/**
 * Created by Kyle on 2021/2/24.
 */

data class GetProducts(
    val code: Int,
    val message: String,
    val shop_id: String,
    val prod_shop_id: String,
    val datacnt: Int,
    val imgpath: String,
    val appcate_imgpath: String,
    val product: List<Product>?
)

data class Product(
    val iscomb: String,
    val ispack: String,
    val sale_start: Any? = null,
    val sale_end: Any? = null,
    val spe_price: Double? = null,
    val serno: Long,
    val prod_id: String,
    val prod_name1: String,
    val prod_name2: String,
    val prod_shortname: String,
    val dep_id: String,
    val prod_memo: String,
    val spec: String,
    val unit: String,
    val barcode: String,
    val gencods: List<String>,
    val tax: Double,
    val tax_sign: String,
    val price1: Double,
    val enable: String,
    val combined: String,
    val isfloat: String,
    val isusepos: String,
    val stop_sale: String,
    val nonservicecharge: String,
    val nonbonuspt: String,
    val isredeem: String,
    val redeem_point: String,
    val packprc_type: String,
    val size_type: String,
    val szfprod_id: String,
    val sztaste_sno: String,
    val origin: String,
    val element: String,
    val caloric: String,
    val imgfile1: String?,
    val prod_content: String? = null,
    val keyword: String? = null,
    val promotion_content: String? = null,
    val description_url: String? = null,
    val spec_html: String? = null,
    val spec_url: String? = null,
    val EC_url: String? = null,
    val appCateid: List<Long>,
    val appCate: List<AppCate>,
    val price2: Double,
    val combPrice: Double? = null,
    val stop_sell: Boolean,
    var is_hidden: String
)

data class AppCate(
    val serno: Long,
    val subject: String,
    val imgfile1: Any? = null,
    val imgfile2: Any? = null
)
