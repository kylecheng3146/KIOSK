package com.lafresh.kiosk.model

/**
 * Created by Kyle on 2020/11/12.
 */

data class Products(
    val products: List<ProductBean>
)

data class ProductBean(
    val id: String,
    val is_combo: Boolean,
    val is_package: Boolean,
    val sale_start: Any? = null,
    val sale_end: Any? = null,
    val special_price: Double? = null,
    val title: String,
    val second_title: String,
    val abbreviated_title: String,
    val memo: String,
    val spec: String,
    val unit: String,
    val barcode: String,
    val tax: Double,
    val tax_sign: String,
    val price: Long,
    val enable: Boolean,
    val is_combo_item: Boolean,
    val can_get_points: Boolean,
    val can_redeemed: Boolean,
    val spend_points: Long,
    val origin: String,
    val element: String,
    val caloric: String,
    val description: String,
    val promotion_content: String,
    val description_url: String,
    val spec_html: String,
    val spec_url: String,
    val EC_url: String,
    val combo_price: Any? = null,
    val is_stop_sale: Boolean,
    val categories: List<CategoryBean>,
    val img: String,
    var count: Int
)

data class CategoryBean(
    val id: String,
    val title: String
)
