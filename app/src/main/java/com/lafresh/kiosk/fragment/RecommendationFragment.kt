package com.lafresh.kiosk.fragment

import android.app.DialogFragment
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lafresh.kiosk.*
import com.lafresh.kiosk.activity.CheckOutOptionActivity
import com.lafresh.kiosk.adapter.RecommendationAdapter
import com.lafresh.kiosk.manager.BasicSettingsManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.ProductBean
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils
import org.json.JSONException

/**
 * Created by Kyle on 2020/11/11.
 */

@RequiresApi(Build.VERSION_CODES.M)
class RecommendationFragment : DialogFragment(), View.OnClickListener {

    lateinit var products: List<ProductBean>
    lateinit var btnReturn: Button
    lateinit var btnConfirm: Button
    lateinit var recycler_view: RecyclerView
    private var adapter: RecommendationAdapter? = null
    inline fun <reified T> genericType() = object : TypeToken<T>() {}.type
    val testData = """
    [
        {
          "id": "UC00000011",
          "title": "椒鹽雞塊(單點)",
          "categories": [
            {
              "id": "281",
              "title": "單品類(炸)"
            }
          ],   
          "memo": "",
          "spec": "300G",
          "unit": "PCS",
          "barcode": "",
          "tax": .0500,
          "tax_sign": "",
          "price": 80.000000,
          "enable": true,
          "is_combo": false,
          "is_package": false,
          "is_combo_item": false,
          "is_stop_sale": false,
          "can_get_points": false,
          "can_redeemed": false,
          "spend_points": 0,
          "combo_price": 0,
          "origin": "",
          "element": "",
          "caloric": "",
          "img": "https://twgreatdaily.com/images/elastic/MZ4/MZ4mX3ABjYh_GJGVES8-.jpg",
          "desciption": "<p>描述的內容</p>",
          "special_price": null
        }
    ]
    """

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_AppCompat_DayNight_NoActionBar)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_recommendation, container, false)
        btnConfirm = view.findViewById(R.id.btn_confirm)
        btnConfirm.setOnClickListener(this)
        btnReturn = view.findViewById(R.id.btn_return)
        btnReturn.setOnClickListener(this)
        recycler_view = view.findViewById(R.id.recyclerView)
        adapter = RecommendationAdapter(activity, products)
        // 設置adapter給recycler_view
        recycler_view.adapter = adapter
        recycler_view.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                // this will be called multiple times for single click - for MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP, and MotionEvent.ACTION_MOVE
                // So restricting the call only for ACTION_DOWN,
                if (e.action == MotionEvent.ACTION_DOWN) {
                    activity.onUserInteraction()
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        val window = dialog!!.window
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
//        window?.setLayout((getResources().getDisplayMetrics().widthPixels * 0.8).toInt(), (getResources().getDisplayMetrics().heightPixels * 0.8).toInt())
//        window?.setGravity(Gravity.CENTER)
    }

    override fun onClick(view: View) {
        if (ClickUtil.isFastDoubleClick()) return
        when (view.id) {
            R.id.btn_return -> {
                dismiss()
                CommonUtils.intentActivity(activity as AppCompatActivity, CheckOutOptionActivity::class.java)
            }

            R.id.btn_confirm -> {
                addProductData()
            }
        }
    }

    private fun addProductData() {
        val product = Product()
        val newProduct = adapter?.getProductsData()
        newProduct.let {
            it?.forEach {
                if (it.count > 0) {
                    var productVO = com.lafresh.kiosk.shoppingcart.model.Product()
                    productVO.prod_id = it.id
                    productVO.dep_memo = it.memo
                    productVO.unit = it.unit
                    productVO.prod_name1 = switchProductNameType(it)
                    product.prod_name1 = switchProductNameType(it)
                    productVO.enable = it.enable.toString()
                    productVO.tax = it.tax.toString()
                    productVO.iscomb = it.is_combo.toString()
                    productVO.ispack = it.is_package.toString()
                    if (it.special_price == null) {
                        productVO.spe_price = "0.0"
                        product.spe_price = 0.0
                    } else {
                        productVO.spe_price = it.special_price.toString()
                        product.spe_price = it.special_price
                    }
                    productVO.price1 = it.price.toString()
                    productVO.redeem_point = it.spend_points.toString()
                    productVO.usePoint = false
                    product.count = it.count
                    product.price1 = it.price.toDouble()
                    product.productVO = productVO
                    val order = Order(product)
                    order.updateUI(activity)
                    order.updateCheckoutOptionsUI(activity)
                    Bill.Now.order_add(order)
                    OrderManager.getInstance().addItem(order)
                }
            }
            CommonUtils.intentActivity(activity as AppCompatActivity, CheckOutOptionActivity::class.java)
            dismiss()
        }
    }

    fun switchProductNameType(productBean: ProductBean): String {
        when (BasicSettingsManager.instance.getBasicSetting()?.kiosk_product_name_type) {
            "FIRST_NAME" -> try {
                return productBean.title
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            "SECOND_NAME" -> try {
                if ("" == productBean.second_title || productBean.second_title == null) {
                    return productBean.title
                } else {
                    return productBean.second_title
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            "ABBREVIATED_NAME" -> try {
                if ("" == productBean.abbreviated_title || productBean.abbreviated_title == null) {
                    return productBean.title
                } else {
                    return productBean.abbreviated_title
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            else -> {
            }
        }
        return productBean.title
    }

    private fun enableButtonClickable() {
        btnConfirm.isClickable = true
        btnReturn.isClickable = true
    }
}
