package com.lafresh.kiosk.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.lafresh.kiosk.*
import com.lafresh.kiosk.adapter.BrandsAdapter
import com.lafresh.kiosk.adapter.DiningOptionAdapter
import com.lafresh.kiosk.dialog.CashEmptyDialog
import com.lafresh.kiosk.dialog.KeyboardDialog
import com.lafresh.kiosk.httprequest.ApiRequest
import com.lafresh.kiosk.httprequest.ApiRequest.ApiListener
import com.lafresh.kiosk.httprequest.CheckTableApiRequest
import com.lafresh.kiosk.manager.BasicSettingsManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.SaleMethod
import com.lafresh.kiosk.model.SaleMethods
import com.lafresh.kiosk.model.SaleType
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.type.ScanType
import com.lafresh.kiosk.type.TableNoType
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils
import com.lafresh.kiosk.utils.SharePrefsUtils
import org.json.JSONException
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.M)
class DiningOptionActivity : BaseActivity(), View.OnClickListener,
    BrandsAdapter.onBrandSelectListener {

    private lateinit var adapter: DiningOptionAdapter
    private lateinit var rvDiningOption: RecyclerView
    lateinit var btnLogin: Button
    lateinit var btnWelcome: Button
    lateinit var btnReturn: Button
    lateinit var tvLogin: TextView
    lateinit var tvSelect: TextView
    lateinit var tvWelcomeTitle: TextView
    lateinit var viewBody: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_inorout)
        HomeActivity.now!!.activities.add(this)
        setUI()
        setActions()
    }

    override fun setUI() {
        btnLogin = findViewById(R.id.btn_login)
        btnWelcome = findViewById(R.id.btn_welcome)
        tvSelect = findViewById(R.id.tv_select)
        tvLogin = findViewById(R.id.tv_login)
        btnReturn = findViewById(R.id.btn_return)
        tvLogin.text = ""
        viewBody = findViewById(R.id.rl_body)
        rvDiningOption = findViewById(R.id.rv_dining_option)
        checkShopUseMember()
        checkProject()
        checkMealWay()
        initLogo()
    }

    fun checkMealWay() {
        val manager = OrderManager.getInstance()
        manager.saleMethods = Config.saleMethods
        var saleMethods = manager.saleMethods
        saleMethods = setDefaultSaleMethods(saleMethods)
        adapter = DiningOptionAdapter(this)
        adapter.addItem(saleMethods)
        // 設置adapter給recycler_view
        val layoutManager = GridLayoutManager(this, saleMethods.size)
        rvDiningOption.layoutManager = layoutManager
        rvDiningOption.adapter = adapter
    }

    /**
     * 若沒有銷售方式則使用預設的.
     * @param saleMethods 銷售方式
     * */
    private fun setDefaultSaleMethods(saleMethods: MutableList<SaleMethod>): MutableList<SaleMethod> {
        var saleMethods1 = saleMethods
        if (saleMethods1.size == 0) {
            val defaultSaleMethod = "{\n" +
                    "  \"sale_methods\": [\n" +
                    "    {\n" +
                    "      \"id\": \"1\",\n" +
                    "      \"name\": \"內用\",\n" +
                    "      \"has_address\": false,\n" +
                    "      \"type\": \"DINE_IN\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\": \"2\",\n" +
                    "      \"name\": \"外帶\",\n" +
                    "      \"has_address\": false,\n" +
                    "      \"type\": \"PICK_UP\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}"
            val gson = Gson()
            saleMethods1 = gson.fromJson(defaultSaleMethod, SaleMethods::class.java).sale_methods as MutableList<SaleMethod>
        }
        if (BuildConfig.FLAVOR == FlavorType.jourdeness.name) {
            if (Config.isShopping) {
                val defaultSaleMethod = """
                        {
                          "sale_methods": [
                            {
                              "id": "0",
                              "name": "購物",
                              "has_address": false,
                              "type": "DEFAULT"
                            }
                          ]
                        }
                    """.trimIndent()
                val gson = Gson()
                saleMethods1 =
                    gson.fromJson(defaultSaleMethod, SaleMethods::class.java).sale_methods as MutableList<SaleMethod>
            } else {
                val iterator = saleMethods1.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (item.name != "外帶") {
                        iterator.remove()
                    }
                }
                if (saleMethods1.size == 0) {
                    val defaultSaleMethod = """
                        {
                          "sale_methods": [
                            {
                              "id": "2",
                              "name": "外帶",
                              "has_address": false,
                              "type": "PICK_UP"
                            }
                          ]
                        }
                    """.trimIndent()
                    val gson = Gson()
                    saleMethods1 = gson.fromJson(defaultSaleMethod, SaleMethods::class.java).sale_methods as MutableList<SaleMethod>
                }
            }
        }
        return saleMethods1
    }

    fun checkProject() {
        if (BuildConfig.FLAVOR.equals(FlavorType.liangshehan.name) ||
            BuildConfig.FLAVOR.equals(FlavorType.FormosaChang.name) ||
            BuildConfig.FLAVOR.equals(FlavorType.standard.name)) {
            tvWelcomeTitle = findViewById(R.id.tv_welcome_title)
            tvWelcomeTitle.visibility = View.VISIBLE
        }
        if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name)) {
            if (Config.isShopping) {
                tvSelect.text = ""
            } else {
                tvSelect.text = "【餐點商品僅供外帶】"
            }
            val param = btnLogin.layoutParams as ConstraintLayout.LayoutParams
            param.topMargin = 80
            btnLogin.text = ""
            btnLogin.layoutParams = param
            btnLogin.setBackgroundResource(R.drawable.btn_scan_login)
            btnReturn.setBackgroundResource(R.drawable.btn_main_pressed)
            btnReturn.text = getText(R.string.back)
        }
    }

    fun checkShopUseMember() {
        BasicSettingsManager.instance.getBasicSetting()?.let {
            if (BasicSettingsManager.instance.getBasicSetting()!!.use_member) {
                btnLogin.visibility = View.VISIBLE
            }
        }
    }

    override fun setActions() {
        btnReturn.setOnClickListener({
            finish()
        })
        btnLogin.setOnClickListener {
            if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
            validateFlavorType()
        }
    }

    fun validateFlavorType() {
        // 標準版掃碼or電話登入 佐登僅掃碼登入
        if (BuildConfig.FLAVOR == FlavorType.jourdeness.name) {
            Config.scanType = ScanType.LOGIN.toString()
            Config.intentFrom = "DiningOptionActivity"
            CommonUtils.intentActivityAndFinish(
                this@DiningOptionActivity,
                ScanActivity::class.java
            )
        } else {
            Config.intentFrom = "DiningOptionActivity"
            CommonUtils.intentActivityAndFinish(
                this@DiningOptionActivity,
                EarnPointsWayActivity::class.java
            )
        }
    }

    override fun onClick(view: View) {
        if (ClickUtil.isFastDoubleClick()) return
        validateProjectFlow()
    }

    fun validateProjectFlow() {
        if (BuildConfig.FLAVOR.equals(FlavorType.cashmodule.name).and(Config.isEnabledCashModule)) {
            validateCashIsNearEmpty()
        } else {
            validateDiningOption()
        }
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

    override fun onResume() {
        super.onResume()
        Kiosk.hidePopupBars(this)
        if (Config.isNewOrderApi) {
            // 判斷是否已經登入
            if (OrderManager.getInstance().isLogin.or(OrderManager.getInstance().isGuestLogin).and(BuildConfig.FLAVOR.equals("liangshehan") ||
                        BuildConfig.FLAVOR.equals(FlavorType.FormosaChang.name) ||
                        BuildConfig.FLAVOR.equals(FlavorType.standard.name)
                )) {
                btnLogin.visibility = View.INVISIBLE
                validateIsLogged()
            }
        } else if (Bill.Now.isMember) {
            tvLogin.text = getString(R.string.loggedIn)
            btnLogin.visibility = View.GONE
            btnWelcome.visibility = View.VISIBLE
        }
        HomeActivity.now!!.idleProof()
    }

    fun validateIsLogged() {
        if (OrderManager.getInstance().member != null) {
            OrderManager.getInstance().member.name?.let {
                val welcomeTitle = if (it.isEmpty()) "您好，歡迎點餐" else String.format(
                    "%s ** 您好，歡迎點餐",
                    it.substring(0, 1)
                )
                tvWelcomeTitle.text = welcomeTitle
            }
            tvWelcomeTitle.visibility = View.VISIBLE
        } else if (OrderManager.getInstance().isGuestLogin) {
            tvWelcomeTitle.text = "您好，歡迎點餐"
        } else {
            btnLogin.visibility = View.GONE
        }
    }

    private fun validateCashIsNearEmpty() {
        val cashEmptyDialog = CashEmptyDialog(this, false) {
            Config.isCashInventoryEmpty = true
            validateDiningOption()
        }

        if (SharePrefsUtils.getHundredInventory(this@DiningOptionActivity) < 20) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getFiftyDollarInventory(this@DiningOptionActivity) < 20) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getTenDollarInventory(this@DiningOptionActivity) < 20) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getFiveDollarInventory(this@DiningOptionActivity) < 20) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getOneDollarInventory(this@DiningOptionActivity) < 20) {
            cashEmptyDialog.show()
            return
        }
        validateCashIsFull()
    }

    private fun validateCashIsFull() {
        val cashEmptyDialog = CashEmptyDialog(this, true) {
            Config.isCashInventoryEmpty = true
            validateDiningOption()
        }
        if (SharePrefsUtils.getHundredInventory(this@DiningOptionActivity) > 500) {
            cashEmptyDialog.show()
            return
        }
        if (SharePrefsUtils.getFiftyDollarInventory(this@DiningOptionActivity) > 500) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getTenDollarInventory(this@DiningOptionActivity) > 500) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getFiveDollarInventory(this@DiningOptionActivity) > 500) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getOneDollarInventory(this@DiningOptionActivity) > 500) {
            cashEmptyDialog.show()
            return
        }

        if (SharePrefsUtils.getHundredInventory(this@DiningOptionActivity) > 500) {
            cashEmptyDialog.show()
            return
        }

        validateDiningOption()
        Config.isCashInventoryEmpty = false
    }

    override fun onUserInteraction() {
        Kiosk.idleCount = Config.idleCount
    }

    override fun onPause() {
        super.onPause()
        HomeActivity.now!!.stopIdleProof()
    }

    fun inputTableNo() {
        val keyboardDialog = KeyboardDialog(this@DiningOptionActivity)
        keyboardDialog.type = KeyboardDialog.TABLE_NO
        keyboardDialog.onEnterListener =
            KeyboardDialog.OnEnterListener { count: Int, text: String? ->
                checkTableNo(text)
            }
        keyboardDialog.show()
    }

    fun checkTableNo(tableNo: String?) {
        val listener: ApiListener = object : ApiListener {
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
                runOnUiThread {
                    Kiosk.showMessageDialog(
                        this@DiningOptionActivity,
                        getString(R.string.tableNoError)
                    )
                }
            }
        }
        CheckTableApiRequest(tableNo).setApiListener(listener).go()
    }

    fun toScanPage(scanType: String) {
        Config.scanType = scanType
        CommonUtils.intentActivity(this@DiningOptionActivity, ScanActivity::class.java)
    }

    fun toShopPage() {
        CommonUtils.intentActivityAndFinish(this@DiningOptionActivity, ShopActivity::class.java)
    }

    fun avoidBtn(view: View?) {
        // just avoid scanner action
    }

    override fun onSelect() {
        validateProjectFlow()
    }
}
