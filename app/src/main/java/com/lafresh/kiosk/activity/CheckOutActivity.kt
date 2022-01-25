package com.lafresh.kiosk.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.*
import com.lafresh.kiosk.adapter.PaymentsAdapter
import com.lafresh.kiosk.adapter.PickupMethodAdapter
import com.lafresh.kiosk.dialog.CashDialog
import com.lafresh.kiosk.dialog.KeyboardDialog
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager
import com.lafresh.kiosk.manager.BasicSettingsManager
import com.lafresh.kiosk.manager.EasyCardManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.manager.OrderManager.OrderListener
import com.lafresh.kiosk.model.*
import com.lafresh.kiosk.type.*
import com.lafresh.kiosk.utils.*
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.M)
class CheckOutActivity : BaseActivity(), View.OnClickListener, PickupMethodAdapter.OnPickupMethodSelectListener {
    private val payments: MutableList<Int> = ArrayList()
    private var btnVehicle: Button? = null
    private var btnDonate: Button? = null
    private var btnCash: Button? = null
    private var btnEasyCard: Button? = null
    private var btnLinePay: Button? = null
    private var btnNccc: Button? = null
    private var btnPiPay: Button? = null
    private var btnTaxId: Button? = null
    private var btnEasyWallet: Button? = null
    private var btnBack: Button? = null
    private var btnMemberCarrier: Button? = null
    private var btnAddOne: Button? = null
    private var btnAddTwo: Button? = null
    private var llAddOne: LinearLayout? = null
    private var llAddTwo: LinearLayout? = null
    private var llPi: LinearLayout? = null
    private var llPaperInvoice: LinearLayout? = null
    private var llTaxId: LinearLayout? = null
    private var llVehicle: LinearLayout? = null
    private var llDonate: LinearLayout? = null
    private var ivPaperInvoice: ImageView? = null
    private var clReceiptModule: ConstraintLayout? = null
    private var etVehicle: EditText? = null
    private var etDonate: EditText? = null
    private var etTaxid: EditText? = null
    private var btnEarnPoints: Button? = null
    private var ibVehicleDelete: ImageButton? = null
    private var ibDonateDelete: ImageButton? = null
    private var ibTaxIdDelete: ImageButton? = null
    private var ib_restart: ImageButton? = null
    private var tvPickupMsg: TextView? = null
    private var textViewTotal: TextView? = null
    private var clPickupMethod: ConstraintLayout? = null
    private lateinit var pickupMethoAdapter: PickupMethodAdapter
    private lateinit var rvPickupMethod: RecyclerView
    private lateinit var paymentsAdapter: PaymentsAdapter
    private lateinit var rvPayments: RecyclerView

    var paymentsFillter = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countPayment()
        setContentView(R.layout.d_checkout)
        HomeActivity.now!!.activities.add(this)
        Kiosk.hidePopupBars(this)
        setUI()
        setActions()
        if (BuildConfig.FLAVOR == FlavorType.jourdeness.name && Config.isShopping) {
            clPickupMethod!!.visibility = View.VISIBLE
            getPickupMethod()
        } else {
            clPickupMethod!!.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        val manager = OrderManager.getInstance()
        val orderId = if (Config.isNewOrderApi) manager.orderId else Bill.fromServer.worder_id
        val easyCardRetryTime = EasyCardManager.easyCardTryTime[orderId]
        validateEasyCardHasRetry(easyCardRetryTime, orderId)
        if (!EasyCardManager.getInstance().isEasyCardCheckout || !Config.useEasyCardByKiosk) {
            disableEasyCardButton()
        }
        setNumbers()
        // 判斷是否已經登入
        validateIsNewApi()

        validateReceiptCarrierType()
    }

    /**
    * 驗證載具方式並修改為 checked 圖示
    * */
    private fun validateReceiptCarrierType() {
        if (OrderManager.getInstance().receiptData.tax_ID_number != null && OrderManager.getInstance().receiptData.tax_ID_number != "") {
            btnTaxId!!.setBackgroundResource(R.drawable.btn_large_orange_circle_pressed)
        } else {
            btnTaxId!!.setBackgroundResource(R.drawable.btn_large_orange_circle)
        }

        if (OrderManager.getInstance().receiptData.carrier != null && OrderManager.getInstance().receiptData.carrier != "") {
            btnVehicle!!.setBackgroundResource(R.drawable.btn_large_orange_circle_pressed)
        } else {
            btnVehicle!!.setBackgroundResource(R.drawable.btn_large_orange_circle)
        }

        if (OrderManager.getInstance().receiptData.npoban != null && OrderManager.getInstance().receiptData.npoban != "") {
            btnDonate!!.setBackgroundResource(R.drawable.btn_large_orange_circle_pressed)
        } else {
            btnDonate!!.setBackgroundResource(R.drawable.btn_large_orange_circle)
        }
    }

    fun validateEasyCardHasRetry(easyCardRetryTime: Int?, orderId: String?) {
        if (easyCardRetryTime == null) {
            EasyCardManager.easyCardTryTime[orderId] = 0
        } else if (easyCardRetryTime > 2) {
            disableEasyCardButton()
        }
    }

    override fun setUI() {
        btnMemberCarrier = findViewById(R.id.btn_memberCarrier)
        btnVehicle = findViewById(R.id.btn_vehicle)
        btnDonate = findViewById(R.id.btn_donate)
        btnCash = findViewById(R.id.Btn_Cash)
        btnEasyCard = findViewById(R.id.Btn_Card)
        btnLinePay = findViewById(R.id.Btn_QRCode)
        btnNccc = findViewById(R.id.btnNccc)
        btnTaxId = findViewById(R.id.btn_taxId)
        btnPiPay = findViewById(R.id.btnPiPay)
        llAddOne = findViewById(R.id.llAddOne)
        llPaperInvoice = findViewById(R.id.ll_paper_invoice)
        llTaxId = findViewById(R.id.ll_taxId)
        llPaperInvoice = findViewById(R.id.ll_paper_invoice)
        llPaperInvoice = findViewById(R.id.ll_paper_invoice)
        ivPaperInvoice = findViewById(R.id.iv_paper_invoice)
        llVehicle = findViewById(R.id.ll_vehicle)
        llDonate = findViewById(R.id.ll_donate)
        btnAddOne = findViewById(R.id.btnAddOne)
        ibVehicleDelete = findViewById(R.id.ib_vehicle_delete)
        ibDonateDelete = findViewById(R.id.ib_donate_delete)
        ibTaxIdDelete = findViewById(R.id.ib_taxId_delete)
        ib_restart = findViewById(R.id.ib_restart)
        btnAddOne = findViewById(R.id.btnAddOne)
        btnAddOne = findViewById(R.id.btnAddOne)
        validateIsTfgFlow()
        btnBack = findViewById(R.id.btn_back)
        btnAddOne = findViewById(R.id.btnAddOne)
        etDonate = findViewById(R.id.et_donate)
        etVehicle = findViewById(R.id.et_vehicle)
        etDonate = findViewById(R.id.et_donate)
        etTaxid = findViewById(R.id.et_taxId)
        btnEarnPoints = findViewById(R.id.btn_earn_points)
        clReceiptModule = findViewById(R.id.rl_receipt_module)
        rvPayments = findViewById(R.id.rv_paymemts)
        clPickupMethod = findViewById(R.id.cl_pickup_method)
        rvPickupMethod = findViewById(R.id.rv_pickup_methods)
        tvPickupMsg = findViewById(R.id.tv_pickup_msg)
        textViewTotal = findViewById(R.id.tv_total)
        if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
            textViewTotal!!.text = "$" + FormatUtil.removeDot(OrderManager.getInstance().totalFee)
        }else{
            textViewTotal!!.visibility = View.GONE
        }
        if (Config.disableReceiptModule || BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name)) {
            clReceiptModule!!.setVisibility(View.GONE)
        }
        if (BuildConfig.FLAVOR == FlavorType.jourdeness.name && OrderManager.getInstance().isLogin) {
            btnMemberCarrier!!.visibility = View.VISIBLE
        } else {
            btnMemberCarrier!!.visibility = View.GONE
        }
        initLogo()
        getPaymentsApi()
        // 判斷是否已經登入
        validateIsNewApi()
    }

    fun validateIsTfgFlow() {
        if (FlavorType.TFG.name != BuildConfig.FLAVOR) {
            if (payments.size > 1) {
                btnAddTwo = findViewById(R.id.btnAddTwo)
                llAddTwo = findViewById(R.id.llAddTwo)
            }
            llPi = findViewById(R.id.llPi)
            if (Config.usePiPay) {
                llPi!!.visibility = View.VISIBLE
            }
        } else {
            btnEasyWallet = findViewById(R.id.btn_easy_wallet)
            btnEasyWallet!!.setOnClickListener {
                Config.scanType = ScanType.EASY_WALLET.toString()
                Config.intentFrom = "CheckOutActivity"
                startActivity(Intent(applicationContext, ScanActivity::class.java))
            }
        }
    }

    fun validateIsNewApi() {
        if (Config.isNewOrderApi) {
            checkEarnPointVisibility()
        } else {
            btnEarnPoints!!.visibility = View.GONE
        }
    }

    fun checkEarnPointVisibility() {
        if (OrderManager.getInstance().isLogin||OrderManager.getInstance().isGuestLogin||!BasicSettingsManager.instance.getBasicSetting()!!.has_points){
            btnEarnPoints!!.visibility = View.GONE
        }else{
            btnEarnPoints!!.visibility = View.VISIBLE
        }
    }

    private fun getPickupMethod() {
        val apiServices = RetrofitManager.instance!!.getApiServices(Config.cacheUrl + "/")
        val call: Call<PickupMethods> = apiServices.getPickupMethod(Config.token, Config.shop_id)
        call.enqueue(object : Callback<PickupMethods?> {
            override fun onResponse(call: Call<PickupMethods?>, response: Response<PickupMethods?>) {
                if (!response.isSuccessful) {
                    showErrorDialog(R.string.not_set_pickup_method, R.string.not_set_payment_message)
                    return
                }
                if (response.body() != null && response.body()?.pickup_methods?.isNotEmpty() == true) {
                    val pickupMethods = response.body()!!.pickup_methods
                    val spanCount = if (pickupMethods!!.size > 4) 4 else pickupMethods.size
                    pickupMethoAdapter = PickupMethodAdapter(
                        this@CheckOutActivity,
                        pickupMethods as List<PickupMethod>,
                        this@CheckOutActivity
                    )
                    val layoutManager = GridLayoutManager(this@CheckOutActivity, spanCount)
                    rvPickupMethod.layoutManager = layoutManager
                    rvPickupMethod.adapter = pickupMethoAdapter
                } else {
                    showErrorDialog(R.string.not_set_pickup_method, R.string.not_set_payment_message)
                }
            }

            override fun onFailure(call: Call<PickupMethods?>, t: Throwable) {
                showErrorDialog(R.string.connectionError, R.string.connectionError_message)
                ProgressUtils.instance!!.hideProgressDialog()
            }
        })
    }

    fun getPaymentsApi() {
        ProgressUtils.instance!!.showProgressDialog(this)
        RetrofitManager.instance!!.getApiServices(Config.cacheUrl).getPayments(Config.token, Config.shop_id, "KIOSK")
            .enqueue(object : Callback<Payments> {
                override fun onResponse(call: Call<Payments>, response: Response<Payments>) {
                    ProgressUtils.instance!!.hideProgressDialog()
                    if (response.isSuccessful) {
                        val defaultSpanCount: Int
                        when (BuildConfig.FLAVOR) {
                            FlavorType.liangshehan.name, FlavorType.bafang.name -> {
                                defaultSpanCount = 4
                            }

                            else -> {
                                defaultSpanCount = 3
                            }
                        }

                        val filterPayments = arrayListOf<PaymentMethod>()
                        if (BuildConfig.FLAVOR.equals(FlavorType.cashmodule.name) && Config.isEnabledCashModule && !Config.isCashInventoryEmpty) {
                            val cashModuleData = addCashModulePayment(response)
                            paymentsAdapter = PaymentsAdapter(this@CheckOutActivity, cashModuleData)
                        } else {
                            response.body()?.payment_methods.let {
                                filterPayments.addAll(response.body()?.payment_methods!!)
                            }
                            if (BuildConfig.FLAVOR == FlavorType.FormosaChang.name) {

//                                //如為外帶則移除臨櫃結帳項目
//                                if (!OrderManager.getInstance().isDineIn) {
//                                    paymentsFillter = arrayListOf("臨櫃結帳")
//                                }
//                                val iterator = filterPayments.iterator()
//                                while (iterator.hasNext()) {
//                                    val item = iterator.next()
//                                    if (paymentsFillter.indexOf(item.name) != -1) {
//                                        iterator.remove()
//                                    }
//                                }

                                // #34585 鬍鬚張 flavor 強制寫入臨櫃結帳 20211110 給一個開關讓鬍鬚張可以自由選擇是否強制寫入
                                if (Config.isEnabledCashPayment) {
                                    var isContainsCASH = false
                                    filterPayments.forEach {
                                        if (it.id == "CASH") {
                                            isContainsCASH = true
                                        }
                                    }
                                    if (!isContainsCASH) {
                                        filterPayments.add(PaymentMethod("CASH", "", "臨櫃結帳"))
                                    }
                                }
                            }
                            paymentsAdapter = PaymentsAdapter(this@CheckOutActivity, filterPayments)
                        }
                        val spanCount = verifyPaymentWayCount(response, filterPayments.size)
                        val layoutManager = GridLayoutManager(this@CheckOutActivity, spanCount)
                        rvPayments.layoutManager = layoutManager
                        rvPayments.adapter = paymentsAdapter
                    } else {
                        showErrorDialog(R.string.connectionError, R.string.connectionError_message)
                    }
                }

                // 判斷付款方式總數
                private fun verifyPaymentWayCount(response: Response<Payments>, defaultSpanCount: Int): Int {
                    if (response.body()!!.payment_methods.size > 0) {
                        when {
                            defaultSpanCount >= 5 -> {
                                if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
                                    return 2
                                }else{
                                    return 4
                                }

                            }

                            else -> {
                                if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
                                    return 2
                                }else{
                                    return defaultSpanCount
                                }
                            }
                        }
                    } else {
                        showErrorDialog(R.string.not_set_payment_title, R.string.not_set_payment_message)
                    }
                    return defaultSpanCount
                }

                override fun onFailure(call: Call<Payments>, t: Throwable) {
                    showErrorDialog(R.string.connectionError, R.string.connectionError_message)
                    ProgressUtils.instance!!.hideProgressDialog()
                }
            })
    }

    private fun addCashModulePayment(response: Response<Payments>): MutableList<PaymentMethod> {
        val cashModule = PaymentMethod(
            id = "CASH_MODULE",
            linePayProductName = null,
            name = "CASH_MODULE"
        )
        val cashModuleData = mutableListOf<PaymentMethod>()
        response.body()!!.payment_methods.forEach {
            cashModuleData.add(it)
        }
        cashModuleData.add(cashModule)
        return cashModuleData
    }

    fun showErrorDialog(title: Int, message: Int) {
        val alertDialogFragment = AlertDialogFragment()
        alertDialogFragment
            .setTitle(title)
            .setMessage(message)
            .setConfirmButton {
                getPaymentsApi()
                alertDialogFragment.dismiss()
            }
            .setUnCancelAble()
            .setCancelButton(R.string.cancel) {
                if (!ClickUtil.isFastDoubleClick()) {
                    alertDialogFragment.dismiss()
                }
            }
            .show(this.fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }

    override fun setActions() {
        btnMemberCarrier!!.setOnClickListener(this)
        btnDonate!!.setOnClickListener(this)
        btnVehicle!!.setOnClickListener(this)
        btnEasyCard!!.setOnClickListener(this)
        btnLinePay!!.setOnClickListener(this)
        btnPiPay!!.setOnClickListener(this)
        btnEarnPoints!!.setOnClickListener(this)
        btnTaxId!!.setOnClickListener(this)
        etTaxid!!.setOnClickListener(this)
        etDonate!!.setOnClickListener(this)
        etVehicle!!.setOnClickListener(this)
        btnBack!!.setOnClickListener(this)
        ibVehicleDelete!!.setOnClickListener(this)
        ibDonateDelete!!.setOnClickListener(this)
        ibTaxIdDelete!!.setOnClickListener(this)
        ib_restart!!.setOnClickListener(this)
        llPaperInvoice!!.setOnClickListener(this)
        llTaxId!!.setOnClickListener(this)
        llVehicle!!.setOnClickListener(this)
        llDonate!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (ClickUtil.isFastDoubleClick()) {
            return
        }
        when (v.id) {
            R.id.et_vehicle, R.id.btn_vehicle, R.id.ll_vehicle -> {
                // 掃描載具
                Config.scanType = ScanType.VEHICLE.toString()
                Config.intentFrom = "CheckOutActivity"
                startActivity(Intent(this@CheckOutActivity, ScanActivity::class.java))
            }
            R.id.et_donate, R.id.btn_donate, R.id.ll_donate -> {
                // 掃描捐贈碼
                Config.scanType = ScanType.DONATE.toString()
                Config.intentFrom = "CheckOutActivity"
                if(Bill.fromServer.custCode.isEmpty()){
                    startActivity(Intent(this@CheckOutActivity, ScanActivity::class.java))
                }else{
                    Toast.makeText(this,R.string.inputCustOrDonate, Toast.LENGTH_SHORT).show()
                }
            }
            R.id.et_taxId, R.id.btn_taxId, R.id.ll_taxId -> {
                // 統一編號
                Config.scanType = ""
                val keyboardDialog = KeyboardDialog(this@CheckOutActivity)
                keyboardDialog.type = KeyboardDialog.S_TaxId
                keyboardDialog.tvCount.text = etTaxid!!.text.toString()
                keyboardDialog.centerText.text = getString(R.string.add_tax_id)
                if(Bill.fromServer.npoBan.isEmpty()){
                    keyboardDialog.show()
                }else{
                    Toast.makeText(this,R.string.inputCustOrDonate, Toast.LENGTH_SHORT).show()
                }

                keyboardDialog.onEnterListener =
                    KeyboardDialog.OnEnterListener { _: Int, text: String? ->
                        etDonate!!.setText("")
                        etVehicle!!.setText("")
                        etTaxid!!.setText(text)
                        Bill.fromServer.npoBan = ""
                        Bill.fromServer.buyerNumber = ""
                        Bill.fromServer.custCode = text
                        OrderManager.getInstance().setBusinessCode(text)
                        checkEditTextHasText()
                        validateReceiptCarrierType()
                    }
            }
            R.id.Btn_Card -> if (Config.isNewOrderApi) {
                addPaymentInfo(PaymentsType.EASY_CARD.name)
            } else {
                toPaymentPage(PaymentsType.EASY_CARD.name)
            }
            R.id.Btn_QRCode -> if (Config.isNewOrderApi) {
                addPaymentInfo(PaymentsType.LINE_PAY.name)
            } else {
                toPaymentPage(PaymentsType.LINE_PAY.name)
            }
            R.id.btnPiPay -> if (Config.isNewOrderApi) {
                addPaymentInfo(PaymentsType.PI_PAY.name)
            } else {
                toPaymentPage(PaymentsType.PI_PAY.name)
            }
            R.id.btn_back -> finish()

            R.id.btn_earn_points -> {
                Config.intentFrom = "CheckOutActivity"
                CommonUtils.intentActivity(this, EarnPointsWayActivity::class.java)
            }

            R.id.ib_vehicle_delete -> {
                etVehicle!!.setText("")
                Bill.fromServer.buyerNumber = ""
                OrderManager.getInstance().setCarrier("")
                ibVehicleDelete!!.visibility = View.GONE
                validateReceiptCarrierType()
            }

            R.id.ib_donate_delete -> {
                etDonate!!.setText("")
                Bill.fromServer.npoBan = ""
                OrderManager.getInstance().setLoveCode("")
                ibDonateDelete!!.visibility = View.GONE
                validateReceiptCarrierType()
            }

            R.id.ib_taxId_delete -> {
                etTaxid!!.setText("")
                Bill.fromServer.custCode = ""
                OrderManager.getInstance().setBusinessCode("")
                ibTaxIdDelete!!.visibility = View.GONE
                validateReceiptCarrierType()
            }
            R.id.ib_restart -> finish()
            R.id.ll_paper_invoice ->{
                ivPaperInvoice!!.setBackgroundResource(R.drawable.btn_confirm_ok)
            }
            else -> {
            }
        }
    }

    fun toPaymentPage(type: String) {
        when (type) {
            PaymentsType.CREDIT_CARD.name -> {
                CommonUtils.intentActivity(this, NCCCActivity::class.java)
            }
            PaymentsType.EASY_CARD.name -> {
                Config.easyCardTransactionType = EasyCardTransactionType.PAY
                CommonUtils.intentActivity(this, EasyCardActivity::class.java)
            }
            PaymentsType.LINE_PAY.name -> {
                Config.scanType = ScanType.LINE_PAY.toString()
                Config.intentFrom = "CheckOutActivity"
                CommonUtils.intentActivity(this, ScanActivity::class.java)
            }
            PaymentsType.PI_PAY.name -> {
                Config.scanType = ScanType.PI_PAY.toString()
                Config.intentFrom = "CheckOutActivity"
                CommonUtils.intentActivity(this, ScanActivity::class.java)
            }
            else -> {
                CashDialog(this@CheckOutActivity).show()
            }
        }
    }

    fun addPaymentInfo(type: String) {
        ProgressUtils.instance!!.showProgressDialog(this)
        val manager = OrderManager.getInstance()
        val tempPayment = Payment()
        tempPayment.type = type
        tempPayment.payment_amount = manager.totalFee.toInt()
        manager.setTempPayment(tempPayment)
        manager.createOrUpdateOrder(manager.total.toDouble(), object : OrderListener {
            override fun onSuccess(response: Response<*>?) {
                val version: String =
                    getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
                CommonUtils.setLog(
                    LogParams(
                        "kiosk",
                        Config.shop_id,
                        "order",
                        manager.orderId,
                        "KIOSK",
                        version,
                        "新增付款資訊",
                        "",
                        response!!.body().toString()
                    )
                )
                ProgressUtils.instance!!.hideProgressDialog()
                manager.setTempPayment(null)
                toPaymentPage(type)
            }

            override fun onRetry() {
                ProgressUtils.instance!!.hideProgressDialog()
                addPaymentInfo(type)
            }
        }, this)
    }

    fun countPayment() {
        validateEnabledCreditCard()
        validateEnabledDemoEnv()
    }

    fun validateEnabledDemoEnv() {
        if (Config.enableCounter) {
            payments.add(P_COUNTER)
        }
    }

    fun validateEnabledCreditCard() {
        if (NcccUtils.isUseNccc()) {
            payments.add(P_NCCC)
        }
    }

    fun setNumbers() {
        val buyerNo = Bill.fromServer.buyerNumber
        val npoBan = Bill.fromServer.npoBan
        val custCode = Bill.fromServer.custCode
        etDonate!!.setText(npoBan)
        etVehicle!!.setText(buyerNo)
        etTaxid!!.setText(custCode)
        checkEditTextHasText()
    }

    fun checkEditTextHasText() {
            ibDonateDelete!!.visibility = if (etDonate!!.text.length > 0) View.VISIBLE else View.GONE
            ibTaxIdDelete!!.visibility = if (etTaxid!!.text.length > 0) View.VISIBLE else View.GONE
            ibVehicleDelete!!.visibility = if (etVehicle!!.text.length > 0) View.VISIBLE else View.GONE
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Kiosk.idleCount = Config.idleCount
        return super.dispatchTouchEvent(ev)
    }

    public override fun onDestroy() {
//        Log.e("Act_CheckOut", "onDestroy");
        HomeActivity.now!!.activities.remove(this)
        super.onDestroy()
    }

    fun disableEasyCardButton() {
        btnEasyCard!!.setOnClickListener(null)
        if (FlavorType.mwd.name == BuildConfig.FLAVOR) {
            btnEasyCard!!.setBackgroundResource(R.drawable.btn_no_text_press)
        } else {
            btnEasyCard!!.setBackgroundResource(R.drawable.checkout_easycard)
        }
    }

    fun disableNcccButton() {
        btnAddOne!!.setOnClickListener(null)
        btnAddOne!!.setBackgroundResource(R.drawable.btn_no_text_press)
    }

    companion object {
        const val P_EASY_CARD = 17
        const val P_LINE_PAY = 93
        const val P_NCCC = 8787
        const val P_COUNTER = 66
        const val P_PI_PAY = 487
    }

    override fun onSelect(pickupMethodMsg: String?) {
        if (pickupMethodMsg != null) {
            tvPickupMsg!!.text = pickupMethodMsg
        } else {
            tvPickupMsg!!.text = ""
        }
    }
}
