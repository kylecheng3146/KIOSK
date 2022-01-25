package com.lafresh.kiosk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.lafresh.kiosk.*
import com.lafresh.kiosk.activity.CheckOutOptionActivity
import com.lafresh.kiosk.activity.HomeActivity
import com.lafresh.kiosk.activity.ScanActivity
import com.lafresh.kiosk.activity.ShopActivity
import com.lafresh.kiosk.dialog.CashEmptyDialog
import com.lafresh.kiosk.dialog.KeyboardDialog
import com.lafresh.kiosk.httprequest.ApiRequest
import com.lafresh.kiosk.httprequest.CheckTableApiRequest
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.manager.BasicSettingsManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.GetProducts
import com.lafresh.kiosk.model.GetProductsParams
import com.lafresh.kiosk.model.SaleMethod
import com.lafresh.kiosk.model.SaleType
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.type.ScanType
import com.lafresh.kiosk.type.TableNoType
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils
import com.lafresh.kiosk.utils.CommonUtils.hasValue
import com.lafresh.kiosk.utils.SharePrefsUtils
import com.lafresh.kiosk.utils.TimeUtil
import org.json.JSONException
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Kyle on 2021/11/1.
 */
@RequiresApi(Build.VERSION_CODES.M)
class DiningOptionAdapter(private val mContext: Context) : RecyclerView.Adapter<DiningOptionAdapter.ViewHolder>() {
    private var saleMethods: MutableList<SaleMethod> = mutableListOf()
    val order = OrderManager.getInstance()

    // 建立ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 宣告元件
        val btnDining: Button
        val btnDining2: Button
        val ivDining: ImageView
        val clNormal: ConstraintLayout
        val clGif: ConstraintLayout
        val btnGif: GifImageButton

        init {
            btnDining = itemView.findViewById(R.id.btn_dining)
            btnDining2 = itemView.findViewById(R.id.btn_dining_2)
            ivDining = itemView.findViewById(R.id.iv_dining)
            clNormal = itemView.findViewById(R.id.cl_normal_btn)
            clGif = itemView.findViewById(R.id.cl_gif_btn)
            btnGif = itemView.findViewById(R.id.gif_btn_dining)
        }
    }

    fun addItem(saleMethods: MutableList<SaleMethod>) {
        this.saleMethods.clear()
        this.saleMethods.addAll(saleMethods)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 連結項目布局檔list_item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dining_option, parent, false)

        val height: Int = parent.getMeasuredHeight() / 4
        view.setMinimumHeight(height)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        // 設置tvTitle要顯示的內容
        holder.btnDining2.text = saleMethods[position].name
        holder.btnDining2.setOnClickListener {
            if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
            setSaleMethod(position)
            validateProjectFlow()
            if (BuildConfig.FLAVOR == FlavorType.liangpin.name) {
                getAllProducts()
            }
        }
        holder.btnDining.setOnClickListener {
            if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
            setSaleMethod(position)
            validateProjectFlow()
            if (BuildConfig.FLAVOR == FlavorType.liangpin.name) {
                getAllProducts()
            }
        }
        when {
            saleMethods[position].name.contains(mContext.getString(R.string.dineIn)) -> {
                CommonUtils.loadImage(mContext, holder.ivDining, R.drawable.meal_dine_in)
            }

            saleMethods[position].name.contains(mContext.getString(R.string.takeAway)) -> {
                CommonUtils.loadImage(mContext, holder.ivDining, R.drawable.meal_take_out)
            }
        }
        if (BuildConfig.FLAVOR == FlavorType.jourdeness.name) {
            holder.clNormal.visibility = View.GONE
            holder.clGif.visibility = View.VISIBLE
            if (Config.isShopping) {
                holder.btnGif.setBackgroundResource(R.drawable.gif_btn_shop_now)
            } else {
                holder.btnGif.setBackgroundResource(R.drawable.gif_btn_order_now)
            }
            holder.btnGif.setOnClickListener {
                if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
                setSaleMethod(position)
                validateProjectFlow()
                if (BuildConfig.FLAVOR == FlavorType.liangpin.name) {
                    getAllProducts()
                }
            }
        }
    }

    private fun getAllProducts() {
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).getProducts(
            GetProductsParams(
                "get_product",
                Config.authKey,
                Config.acckey,
                Config.shop_id,
                Config.saleType,
                "",
                TimeUtil.getNowTime(),
                Config.group_id
            )
        )
            .enqueue(object : Callback<GetProducts> {
                override fun onResponse(call: Call<GetProducts>, response: Response<GetProducts>) {
                    if (response.isSuccessful) {
                        Config.productImgPath = response.body()!!.imgpath
                        response.body()!!.product?.let {
                            for (i in it) {
                                if (i.gencods.hasValue()){
                                    for (j in i.gencods){
                                        Log.d("gencods",i.prod_id+":"+j)
                                        CheckOutOptionActivity.barCodeMap[j] = i
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GetProducts>, t: Throwable) {
                    HomeActivity.now!!.idleProof()
                }
            })
    }

    private fun setSaleMethod(position: Int) {
        Config.saleType = saleMethods[position].id
        if (saleMethods[position].type == null) {
            when {
                saleMethods[position].name == "內用" -> {
                    saleMethods[position].type = "DINE_IN"
                }

                saleMethods[position].name == "外帶" -> {
                    saleMethods[position].type = "PICK_UP"
                }
            }
        }
        OrderManager.getInstance().saleMethod = saleMethods[position]
    }

    fun inputTableNo() {
        val keyboardDialog = KeyboardDialog(mContext)
        keyboardDialog.type = KeyboardDialog.TABLE_NO
        keyboardDialog.onEnterListener =
            KeyboardDialog.OnEnterListener { count: Int, text: String? ->
                checkTableNo(text)
            }
        keyboardDialog.show()
    }

    fun checkTableNo(tableNo: String?) {
        val listener: ApiRequest.ApiListener = object : ApiRequest.ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {
                try {
                    val jsonObject = JSONObject(body)
                    if (jsonObject.getInt("code") == 0) {
                        OrderManager.getInstance().setTableNumber(tableNo)
                        Bill.Now.setTableNo(tableNo)
                        toShopPage()
                    } else {
                        onFail()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }

            override fun onFail() {
                (mContext as AppCompatActivity).runOnUiThread {
                    Kiosk.showMessageDialog(
                        mContext,
                        mContext.getString(R.string.tableNoError)
                    )
                }
            }
        }
        CheckTableApiRequest(tableNo).setApiListener(listener).go()
    }

    fun validateProjectFlow() {
        if (BuildConfig.FLAVOR.equals(FlavorType.cashmodule.name).and(Config.isEnabledCashModule)) {
            validateCashIsNearEmpty()
        } else {
            validateDiningOption()
        }
    }

    private fun validateCashIsNearEmpty() {
        val cashEmptyDialog = CashEmptyDialog(mContext as AppCompatActivity, false) {
            Config.isCashInventoryEmpty = true
            validateDiningOption()
        }

        if (SharePrefsUtils.getHundredInventory(mContext) < 20) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getFiftyDollarInventory(mContext) < 20) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getTenDollarInventory(mContext) < 20) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getFiveDollarInventory(mContext) < 20) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getOneDollarInventory(mContext) < 20) {
            cashEmptyDialog.show()
            return
        }
        validateCashIsFull()
    }

    private fun validateCashIsFull() {
        val cashEmptyDialog = CashEmptyDialog(mContext as AppCompatActivity, true) {
            Config.isCashInventoryEmpty = true
            validateDiningOption()
        }
        if (SharePrefsUtils.getHundredInventory(mContext) > 500) {
            cashEmptyDialog.show()
            return
        }
        if (SharePrefsUtils.getFiftyDollarInventory(mContext) > 500) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getTenDollarInventory(mContext) > 500) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getFiveDollarInventory(mContext) > 500) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getOneDollarInventory(mContext) > 500) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getHundredInventory(mContext) > 500) {
            cashEmptyDialog.show()
            return
        }

        validateDiningOption()
        Config.isCashInventoryEmpty = false
    }

    private fun validateDiningOption() {
        if (OrderManager.getInstance().saleMethod.type == SaleType.DINE_IN.name) {
            val basicSettings = BasicSettingsManager.instance.getBasicSetting()
            when (basicSettings?.table_no_type) {
                TableNoType.QR_CODE.name -> toScanPage(ScanType.TABLE_NO.toString())
                TableNoType.KEYBOARD.name -> if (BuildConfig.FLAVOR != FlavorType.kinyo.name) {
                    inputTableNo()
                } else {
                    toShopPage()
                }
                TableNoType.DO_NOT_USE.name -> toShopPage()
                else -> toShopPage()
            }
        } else {
            toShopPage()
        }
    }

    fun toScanPage(scanType: String) {
        Config.scanType = scanType
        CommonUtils.intentActivity(mContext as AppCompatActivity, ScanActivity::class.java)
    }

    fun toShopPage() {
        CommonUtils.intentActivityAndFinish(mContext as AppCompatActivity, ShopActivity::class.java)
    }

    override fun getItemCount(): Int {
        return saleMethods.size
    }
}
