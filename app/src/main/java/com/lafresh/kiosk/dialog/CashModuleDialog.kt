package com.lafresh.kiosk.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import com.lafresh.kiosk.*
import com.lafresh.kiosk.factory.CashModuleFactory
import com.lafresh.kiosk.model.Detail
import com.lafresh.kiosk.model.TransactionModel
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.test.ClientThread
import com.lafresh.kiosk.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

/**
 * Created by Kyle on 2021/4/27.
 */
@RequiresApi(Build.VERSION_CODES.M)
class CashModuleDialog(context: Context) : Dialog(context, android.R.style.Theme_Translucent_NoTitleBar),
    View.OnClickListener, CashModuleClientListener {

    private var threadSize: Int = 0
    private val details = mutableListOf<Detail>()
    private lateinit var clTypePasswordPage: ConstraintLayout
    private lateinit var clCheckoutPage: ConstraintLayout
    private lateinit var clCheckoutResultPage: ConstraintLayout
    private lateinit var cbHundred: CheckBox
    private lateinit var cbFifty: CheckBox
    private lateinit var cbTen: CheckBox
    private lateinit var cbFive: CheckBox
    private lateinit var cbOne: CheckBox
    private lateinit var etPassword: EditText
    private lateinit var btnConfirm: Button
    private lateinit var btnCancel: Button
    private lateinit var tvTitle: TextView
    private lateinit var tvTotalHundred: TextView
    private lateinit var tvTotalFifty: TextView
    private lateinit var tvTotalTen: TextView
    private lateinit var tvTotalFive: TextView
    private lateinit var tvTotalOne: TextView
    val checkoutModuleList = mutableListOf<String>()
    var jsonArray = JSONArray()
    val cashModuleFactory = CashModuleFactory()
    var cashModuleClientListener: CashModuleClientListener? = null
    val gson = Gson()

    interface ClickOkListener {
        fun onOK()
    }

    @JvmField
    var clickOkListener: ClickOkListener? = null

    fun setUI() {
        clTypePasswordPage = findViewById(R.id.cl_type_password_page)
        clCheckoutPage = findViewById(R.id.cl_checkout_page)
        clCheckoutResultPage = findViewById(R.id.cl_checkout_result_page)
        btnConfirm = findViewById(R.id.btn_confirm)
        tvTitle = findViewById(R.id.tv_title)
        tvTotalHundred = findViewById(R.id.tv_total_hundred)
        tvTotalFifty = findViewById(R.id.tv_total_fifty)
        tvTotalTen = findViewById(R.id.tv_total_ten)
        tvTotalFive = findViewById(R.id.tv_total_five)
        tvTotalOne = findViewById(R.id.tv_total_one)
        btnCancel = findViewById(R.id.btn_cancel)
        etPassword = findViewById(R.id.et_password)
        cbHundred = findViewById(R.id.cb_hundred)
        cbFifty = findViewById(R.id.cb_fifty)
        cbTen = findViewById(R.id.cb_ten)
        cbFive = findViewById(R.id.cb_five)
        cbOne = findViewById(R.id.cb_one)
    }

    fun setActions() {
        btnConfirm.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        cbHundred.setOnClickListener(this)
        cbFifty.setOnClickListener(this)
        cbTen.setOnClickListener(this)
        cbFive.setOnClickListener(this)
        cbOne.setOnClickListener(this)
        clCheckoutPage.visibility = View.VISIBLE
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Kiosk.idleCount = Config.idleCount
        return super.dispatchTouchEvent(ev)
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_cashmodule_inventory)
        Kiosk.hidePopupBars(this)
        cashModuleClientListener = this
        setUI()
        setActions()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_cancel -> {
                dismiss()
            }

            R.id.btn_confirm -> {
                if (ClickUtil.isFastDoubleClick()) {
                    return
                }
                if (clCheckoutResultPage.visibility == View.VISIBLE) {
                    dismiss()
                }

                if (clCheckoutPage.visibility == View.VISIBLE) {
                    if (vaildateMoneyCount()) {
                        checkoutModuleList.forEachIndexed { index, s ->
                            ClientThread(cashModuleClientListener, cashModuleFactory.outAllMoney(index.toString(), s)).start()
                        }
                        clCheckoutPage.visibility = View.GONE
                        btnCancel.visibility = View.GONE
                        btnConfirm.visibility = View.GONE
                        tvTitle.text = context.getString(R.string.cash_module_checkout_processing)
                    }
                }

                if (clTypePasswordPage.visibility == View.VISIBLE) {
//                    if(etPassword.text.toString() == "123") {
                        clCheckoutPage.visibility = View.VISIBLE
                        clTypePasswordPage.visibility = View.GONE
                        tvTitle.text = context.getString(R.string.cash_module_check_option)
//                    }
                }
            }

            R.id.cb_hundred -> {
                if ((v as CompoundButton).isChecked) {
                    cbHundred.setChecked(true)
                    cbHundred.setBackgroundResource(R.drawable.cb_checked)
                    checkoutModuleList.add(CashModuleFactory.DevicePort.GIVE_PAPER_MONEY.port)
                } else {
                    cbHundred.setChecked(false)
                    cbHundred.setBackgroundResource(R.drawable.cb_unchecked)
                    checkoutModuleList.remove(CashModuleFactory.DevicePort.GIVE_PAPER_MONEY.port)
                }
            }

            R.id.cb_fifty -> {
                if ((v as CompoundButton).isChecked) {
                    cbFifty.setChecked(true)
                    cbFifty.setBackgroundResource(R.drawable.cb_checked)
                    checkoutModuleList.add(CashModuleFactory.DevicePort.FIFTY_DOLLAR.port)
                } else {
                    cbFifty.setChecked(false)
                    cbFifty.setBackgroundResource(R.drawable.cb_unchecked)
                    checkoutModuleList.remove(CashModuleFactory.DevicePort.FIFTY_DOLLAR.port)
                }
            }

            R.id.cb_ten -> {
                if ((v as CompoundButton).isChecked) {
                    cbTen.setChecked(true)
                    cbTen.setBackgroundResource(R.drawable.cb_checked)
                    checkoutModuleList.add(CashModuleFactory.DevicePort.TEN_DOLLAR.port)
                } else {
                    cbTen.setChecked(false)
                    cbTen.setBackgroundResource(R.drawable.cb_unchecked)
                    checkoutModuleList.remove(CashModuleFactory.DevicePort.TEN_DOLLAR.port)
                }
            }

            R.id.cb_five -> {
                if ((v as CompoundButton).isChecked) {
                    cbFive.setChecked(true)
                    cbFive.setBackgroundResource(R.drawable.cb_checked)
                    checkoutModuleList.add(CashModuleFactory.DevicePort.FIVE_DOLLAR.port)
                } else {
                    cbFive.setChecked(false)
                    cbFive.setBackgroundResource(R.drawable.cb_unchecked)
                    checkoutModuleList.remove(CashModuleFactory.DevicePort.FIVE_DOLLAR.port)
                }
            }

            R.id.cb_one -> {
                if ((v as CompoundButton).isChecked) {
                    cbOne.setChecked(true)
                    cbOne.setBackgroundResource(R.drawable.cb_checked)
                    checkoutModuleList.add(CashModuleFactory.DevicePort.ONE_DOLLAR.port)
                } else {
                    cbOne.setChecked(false)
                    cbOne.setBackgroundResource(R.drawable.cb_unchecked)
                    checkoutModuleList.remove(CashModuleFactory.DevicePort.ONE_DOLLAR.port)
                }
            }
        }
    }

    fun vaildateMoneyCount(): Boolean {
        if (checkoutModuleList.size == 0) {
            CommonUtils.showMessage(context, context.getString(R.string.get_money_warning))
            return false
        }
        return true
    }

    override fun onReceived(response: String) {
        KioskPrinter.addLog("shop_id:" + Config.shop_id)
        KioskPrinter.addLog("CASH_MODULE:" + response)
        threadSize++
        val transactionModel: TransactionModel = gson.fromJson(response, TransactionModel::class.java)
        details.add(transactionModel.detail.get(0))

        if (threadSize == checkoutModuleList.size) {
            GlobalScope.launch {
                // TODO("Background processing...")
                withContext(Dispatchers.Main) {
                    tvTitle.text = context.getString(R.string.checkout_result_title)
                    clCheckoutResultPage.visibility = View.VISIBLE
                    btnConfirm.visibility = View.VISIBLE
                    details.forEach {
                        when (it.currency) {
                            100 -> {
                                tvTotalHundred.text = String.format(context.getString(R.string.all_money_quantity_info), it.currency.toString(), it.quantity.toString(), "張")
                                SharePrefsUtils.putHundredInventory(context, 0)
                            }
                            50 -> {
                                tvTotalFifty.text = String.format(context.getString(R.string.all_money_quantity_info), it.currency.toString(), it.quantity.toString(), "個")
                                SharePrefsUtils.putFiftyDollarInventory(context, 0)
                            }

                            10 -> {
                                tvTotalTen.text = String.format(context.getString(R.string.all_money_quantity_info), it.currency.toString(), it.quantity.toString(), "個")
                                SharePrefsUtils.putTenDollarInventory(context, 0)
                            }

                            5 -> {
                                tvTotalFive.text = String.format(context.getString(R.string.all_money_quantity_info), it.currency.toString(), it.quantity.toString(), "個")
                                SharePrefsUtils.putFiveDollarInventory(context, 0)
                            }

                            1 -> {
                                tvTotalOne.text = String.format(context.getString(R.string.all_money_quantity_info), it.currency.toString(), it.quantity.toString(), "個")
                                SharePrefsUtils.putOneDollarInventory(context, 0)
                            }
                        }
                    }
                }
            }
        }
    }
}
