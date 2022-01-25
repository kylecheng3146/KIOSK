package com.lafresh.kiosk.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.lafresh.kiosk.Bill
import com.lafresh.kiosk.BuildConfig
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.R
import com.lafresh.kiosk.dialog.KeyboardDialog
import com.lafresh.kiosk.manager.BasicSettingsManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.BasicSettings
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.type.ScanType
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils

/**
 * Created by Kyle on 2021/11/22.
 */
@RequiresApi(Build.VERSION_CODES.M)
class InvoiceActivity : BaseActivity(), View.OnClickListener {
    var ivLogo2: ImageView? = null
    var ivAd2: ImageView? = null
    var btnPaperInvoice: Button? = null
    var etPaperInvoice: EditText? = null
    var btnCloudInvoice: Button? = null
    var etCloudInvoice: EditText? = null
    var rlReceiptModule: ConstraintLayout? = null
    var btnMemberCarrier: Button? = null
    var btnVehicle: Button? = null
    var btnDonate: Button? = null
    var etMemberCarrier: EditText? = null
    var etDonate: EditText? = null
    var etVehicle: EditText? = null
    var ibVehicleDelete: ImageButton? = null
    var ivMemberCarrier: ImageView? = null
    var ibDonateDelete: ImageButton? = null
    var btnUniformInvoiceYes: Button? = null
    var btnUniformInvoiceNo: Button? = null
    var etTaxId: EditText? = null
    var btnBack: Button? = null
    var btnConfirm: Button? = null
    var btnReturn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)
        HomeActivity.now!!.activities.add(this)
        HomeActivity.now!!.stopIdleProof()
        setUI()
        setActions()
        initLogo()
    }

    override fun onResume() {
        super.onResume()
        setNumbers()
        validateReceiptCarrierType()
    }

    override fun setUI() {
        ivLogo2 = findViewById(R.id.ivLogo2)
        ivAd2 = findViewById(R.id.ivAd2)
        btnPaperInvoice = findViewById(R.id.btn_paper_invoice)
        etPaperInvoice = findViewById(R.id.et_paper_invoice)
        btnCloudInvoice = findViewById(R.id.btn_cloud_invoice)
        etCloudInvoice = findViewById(R.id.et_cloud_invoice)
        rlReceiptModule = findViewById(R.id.rl_receipt_module)
        btnMemberCarrier = findViewById(R.id.btn_memberCarrier)
        ivMemberCarrier = findViewById(R.id.iv_member_carrier)
        btnVehicle = findViewById(R.id.btn_vehicle)
        btnDonate = findViewById(R.id.btn_donate)
        etMemberCarrier = findViewById(R.id.et_memberCarrier)
        etDonate = findViewById(R.id.et_donate)
        etVehicle = findViewById(R.id.et_vehicle)
        ibVehicleDelete = findViewById(R.id.ib_vehicle_delete)
        ibDonateDelete = findViewById(R.id.ib_donate_delete)
        btnUniformInvoiceYes = findViewById(R.id.btn_uniform_invoice_yes)
        btnUniformInvoiceNo = findViewById(R.id.btn_uniform_invoice_no)
        etTaxId = findViewById(R.id.et_tax_id)
        btnBack = findViewById(R.id.btn_back)
        btnConfirm = findViewById(R.id.btn_confirm)
        btnReturn = findViewById(R.id.btn_return)
        btnMemberCarrier!!.visibility = if (BuildConfig.FLAVOR == FlavorType.jourdeness.name && BasicSettingsManager.instance.getBasicSetting()!!.use_member_carrier && OrderManager.getInstance().isLogin) View.VISIBLE else View.GONE
        ivMemberCarrier!!.setBackgroundResource(if(!BasicSettingsManager.instance.getBasicSetting()!!.use_member_carrier ) R.drawable.member_carrier_on else R.drawable.member_carrier_off)
        ivMemberCarrier!!.visibility = if (BuildConfig.FLAVOR == FlavorType.jourdeness.name && BasicSettingsManager.instance.getBasicSetting()!!.use_member_carrier && OrderManager.getInstance().isLogin) View.VISIBLE else View.GONE
    }

    override fun setActions() {
        btnPaperInvoice!!.setOnClickListener(this)
        btnCloudInvoice!!.setOnClickListener(this)
        btnMemberCarrier!!.setOnClickListener(this)
        btnVehicle!!.setOnClickListener(this)
        btnDonate!!.setOnClickListener(this)
        btnUniformInvoiceYes!!.setOnClickListener(this)
        btnUniformInvoiceNo!!.setOnClickListener(this)
        btnBack!!.setOnClickListener(this)
        btnConfirm!!.setOnClickListener(this)
        btnReturn!!.setOnClickListener(this)
        ibVehicleDelete!!.setOnClickListener(this)
        ibDonateDelete!!.setOnClickListener(this)
        ivMemberCarrier!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (ClickUtil.isFastDoubleClick()) {
            return
        }
        when (v!!.id) {
            R.id.btn_paper_invoice -> {
                rlReceiptModule!!.visibility = View.GONE
                btnPaperInvoice!!.setBackgroundResource(R.drawable.btn_large_orange_circle_pressed)
                btnCloudInvoice!!.setBackgroundResource(R.drawable.btn_large_orange_circle)
                setCloudInvoceDefault()
            }

            R.id.btn_cloud_invoice -> {
                rlReceiptModule!!.visibility = View.VISIBLE
                btnPaperInvoice!!.setBackgroundResource(R.drawable.btn_large_orange_circle)
                btnCloudInvoice!!.setBackgroundResource(R.drawable.btn_large_orange_circle_pressed)
            }

            R.id.et_vehicle, R.id.btn_vehicle -> {
                // 掃描載具
                Config.scanType = ScanType.VEHICLE.toString()
                Config.intentFrom = "InvoiceActivity"
                CommonUtils.intentActivity(this, ScanActivity::class.java)
            }

            R.id.et_donate, R.id.btn_donate -> {
                // 掃描捐贈碼
                Config.scanType = ScanType.DONATE.toString()
                Config.intentFrom = "InvoiceActivity"
                CommonUtils.intentActivity(this, ScanActivity::class.java)
            }

            R.id.et_taxId, R.id.btn_uniform_invoice_yes -> {
                // 統一編號
                val keyboardDialog = KeyboardDialog(this)
                keyboardDialog.type = KeyboardDialog.S_TaxId
                keyboardDialog.tvCount.text = etTaxId!!.text.toString()
                keyboardDialog.show()
                keyboardDialog.onEnterListener =
                    KeyboardDialog.OnEnterListener { _: Int, text: String? ->
                        etDonate!!.setText("")
                        etVehicle!!.setText("")
                        etTaxId!!.setText(text)
                        Bill.fromServer.npoBan = ""
                        Bill.fromServer.buyerNumber = ""
                        Bill.fromServer.custCode = text
                        OrderManager.getInstance().setBusinessCode(text)
                        checkEditTextHasText()
                        validateReceiptCarrierType()
                        btnUniformInvoiceYes!!.setBackgroundResource(R.drawable.btn_selector)
                        btnUniformInvoiceNo!!.setBackgroundResource(R.drawable.btn_selector_pressed)
                    }
            }

            R.id.btn_back -> finish()

            R.id.iv_member_carrier, R.id.btn_memberCarrier-> {
                if(BasicSettingsManager.instance.getBasicSetting()!!.use_member_carrier) {
                    ivMemberCarrier!!.setBackgroundResource(R.drawable.member_carrier_off)
                    BasicSettingsManager.instance.getBasicSetting()!!.use_member_carrier = false
                    OrderManager.getInstance().setMemberCarrier(false)
                    validateReceiptCarrierType()
                }else{
                    ivMemberCarrier!!.setBackgroundResource(R.drawable.member_carrier_on)
                    BasicSettingsManager.instance.getBasicSetting()!!.use_member_carrier = true
                    OrderManager.getInstance().setMemberCarrier(true)
                    setCloudInvoceDefault()
                }
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
                etTaxId!!.setText("")
                Bill.fromServer.custCode = ""
                OrderManager.getInstance().setBusinessCode("")
//                ibTaxIdDelete!!.visibility = View.GONE
                validateReceiptCarrierType()
            }

            R.id.btn_uniform_invoice_yes -> {
            }

            R.id.btn_uniform_invoice_no -> {
                btnUniformInvoiceYes!!.setBackgroundResource(R.drawable.btn_selector_pressed)
                btnUniformInvoiceNo!!.setBackgroundResource(R.drawable.btn_selector)
                OrderManager.getInstance().setBusinessCode("")
                etTaxId!!.setText("")
                checkEditTextHasText()
                validateReceiptCarrierType()
            }

            R.id.btn_confirm -> {
                CommonUtils.intentActivity(this, CheckOutActivity::class.java)
            }

            R.id.btn_return -> {
                finish()
            }
        }
    }

    private fun setCloudInvoceDefault() {
        validateReceiptCarrierType()
        etTaxId!!.setText("")
        etDonate!!.setText("")
        etVehicle!!.setText("")
    }

    fun setNumbers() {
        val buyerNo = Bill.fromServer.buyerNumber
        val npoBan = Bill.fromServer.npoBan
        val custCode = Bill.fromServer.custCode
        etDonate!!.setText(npoBan)
        etVehicle!!.setText(buyerNo)
        etTaxId!!.setText(custCode)
        checkEditTextHasText()
    }


    /**
     * 驗證載具方式並修改為 checked 圖示
     * */
    private fun validateReceiptCarrierType() {
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

        if (OrderManager.getInstance().receiptData.tax_ID_number != null && OrderManager.getInstance().receiptData.tax_ID_number != "") {
            btnUniformInvoiceYes!!.setBackgroundResource(R.drawable.btn_selector)
            btnUniformInvoiceNo!!.setBackgroundResource(R.drawable.btn_selector_pressed)
        } else {
            btnUniformInvoiceYes!!.setBackgroundResource(R.drawable.btn_selector_pressed)
            btnUniformInvoiceNo!!.setBackgroundResource(R.drawable.btn_selector)
        }
    }

    fun checkEditTextHasText() {
        ibDonateDelete!!.visibility = if (etDonate!!.text.length > 0) View.VISIBLE else View.GONE
//        ibTaxIdDelete!!.visibility = if (etTaxId!!.text.length > 0) View.VISIBLE else View.GONE
        ibVehicleDelete!!.visibility = if (etVehicle!!.text.length > 0) View.VISIBLE else View.GONE
    }
}
