package com.lafresh.kiosk.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.Kiosk
import com.lafresh.kiosk.R
import com.lafresh.kiosk.factory.CashModuleFactory
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.model.ServerReceivedModel
import com.lafresh.kiosk.model.TransactionModel
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.test.ClientThread
import com.lafresh.kiosk.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Kyle on 2021/4/28.
 */
@RequiresApi(Build.VERSION_CODES.M)
class CashInventoryDialog(activity: AppCompatActivity) : Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar), View.OnClickListener, CashModuleClientListener, CashModuleServerListener {

    private var addMoneyThread: Thread? = null
    private var resetAllThread: ClientThread? = null
    private var thread: Thread
//    private lateinit var cashModuleServer: CashModuleServer
    private lateinit var clOption: ConstraintLayout
    private lateinit var clContinue: ConstraintLayout
    private lateinit var clTotalPrice: ConstraintLayout
    private lateinit var clAddMoneyPage: ConstraintLayout
    private lateinit var clMessage: ConstraintLayout
    private lateinit var clAddMoneyCheckPage: ConstraintLayout
    private lateinit var btnAddMoney: Button
    private lateinit var btnGetMoney: Button
    private lateinit var btnAddMoneyBack: Button
    private lateinit var btnAddMoneyCheckExit: Button
    private lateinit var tv_add_money_title: TextView
    private lateinit var btnAddMoneyContinue: Button
    private lateinit var btnGetMoneyContinue: Button
    private lateinit var btnOptionExit: Button
    private lateinit var btnExit: Button
    private lateinit var btnAddMoneyCheckConfirm: Button
    private lateinit var btnAddMoneyCheckBack: Button
    private lateinit var btnCancelContinue: Button
    private lateinit var btnTotalConfirm: Button
    private lateinit var btnAddMoneyConfirm: Button
    private lateinit var btnAddMoneyExit: Button
    private lateinit var btnInventory: Button
    private lateinit var tvHundred: TextView
    private lateinit var tvFifty: TextView
    private lateinit var tvTen: TextView
    private lateinit var tvFive: TextView
    private lateinit var tvOne: TextView
    private lateinit var tvAddMoneyCheckTitle: TextView
    private lateinit var tvOptionTitle: TextView
    private lateinit var tvAddMoneyTitle: TextView
    private lateinit var etHundred: EditText
    private lateinit var etFifty: EditText
    private lateinit var etTen: EditText
    private lateinit var etFive: EditText
    private lateinit var etOne: EditText
    private lateinit var ivAddMoneySuccess: ImageView
    private val array = JSONArray()
    var cashModuleClientListener: CashModuleClientListener? = null
    var cashModuleServerListener: CashModuleServerListener? = null
    val cashModuleFactory = CashModuleFactory()
    var dialog = AlertDialogFragment()
    val gson = Gson()
    private lateinit var activity: AppCompatActivity
//    val gson = Gson()
//
//    interface ClickOkListener {
//        fun onOK()
//    }

//    @JvmField
//    var clickOkListener: ClickOkListener? = null

    fun setUI() {
        clOption = findViewById(R.id.cl_option)
        clOption.visibility = View.VISIBLE
        clContinue = findViewById(R.id.cl_continue)
        clTotalPrice = findViewById(R.id.cl_total_price)
        clMessage = findViewById(R.id.cl_message)
        clAddMoneyCheckPage = findViewById(R.id.cl_add_money_check_page)
        clAddMoneyPage = findViewById(R.id.cl_add_money_page)
//        clCheckoutPage = findViewById(R.id.cl_checkout_page)
//        clCheckoutResultPage = findViewById(R.id.cl_checkout_result_page)
        btnAddMoney = findViewById(R.id.btn_add_money)
        ivAddMoneySuccess = findViewById(R.id.iv_add_money_success)
        btnGetMoney = findViewById(R.id.btn_get_money)
        btnOptionExit = findViewById(R.id.btn_option_exit)
        btnAddMoneyCheckExit = findViewById(R.id.btn_add_money_check_exit)
        btnAddMoneyContinue = findViewById(R.id.btn_add_money_continue)
        btnAddMoneyExit = findViewById(R.id.btn_add_money_exit)
        btnGetMoneyContinue = findViewById(R.id.btn_get_money_continue)
        btnAddMoneyCheckBack = findViewById(R.id.btn_add_money_check_back)
        btnAddMoneyBack = findViewById(R.id.btn_add_money_back)
        btnExit = findViewById(R.id.btn_exit)
        btnCancelContinue = findViewById(R.id.btn_cancel_continue)
        btnInventory = findViewById(R.id.btn_inventory)
        btnTotalConfirm = findViewById(R.id.btn_total_confirm)
        btnAddMoneyCheckConfirm = findViewById(R.id.btn_add_money_check_confirm)
        btnAddMoneyConfirm = findViewById(R.id.btn_add_money_confirm)
        tvAddMoneyCheckTitle = findViewById(R.id.tv_add_money_check_title)
        tvAddMoneyTitle = findViewById(R.id.tv_add_money_title)
        tvOptionTitle = findViewById(R.id.tv_option_title)
        tvOptionTitle.text = context.getString(R.string.door_close)
        btnAddMoney.visibility = View.VISIBLE
        btnGetMoney.visibility = View.VISIBLE
        btnOptionExit.visibility = View.VISIBLE
        tvAddMoneyTitle.text = context.getString(R.string.add_money_title)
        tvHundred = findViewById(R.id.tv_hundred)
        tvFifty = findViewById(R.id.tv_fifty)
        tvTen = findViewById(R.id.tv_ten)
        tvFive = findViewById(R.id.tv_five)
        tvOne = findViewById(R.id.tv_one)
        etHundred = findViewById(R.id.et_hundred)
        etFifty = findViewById(R.id.et_fifty)
        etTen = findViewById(R.id.et_ten)
        etFive = findViewById(R.id.et_five)
        etOne = findViewById(R.id.et_one)
    }

    fun setActions() {
        btnAddMoney.setOnClickListener(this)
        btnGetMoney.setOnClickListener(this)
        btnAddMoneyContinue.setOnClickListener(this)
        btnOptionExit.setOnClickListener(this)
        btnAddMoneyCheckExit.setOnClickListener(this)
        btnExit.setOnClickListener(this)
        btnCancelContinue.setOnClickListener(this)
        btnAddMoneyBack.setOnClickListener(this)
        btnAddMoneyCheckBack.setOnClickListener(this)
        btnInventory.setOnClickListener(this)
        btnTotalConfirm.setOnClickListener(this)
        btnAddMoneyExit.setOnClickListener(this)
        btnAddMoneyCheckConfirm.setOnClickListener(this)
        btnAddMoneyConfirm.setOnClickListener(this)
//        btnConfirm.setOnClickListener(this)
//        cbHundred.setOnClickListener(this)
//        cbFifty.setOnClickListener(this)
//        cbTen.setOnClickListener(this)
//        cbFive.setOnClickListener(this)
//        cbOne.setOnClickListener(this)
//        clTypePasswordPage.visibility = View.VISIBLE
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Kiosk.idleCount = Config.idleCount
        return super.dispatchTouchEvent(ev)
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_cashmodule_checkout)
        Kiosk.hidePopupBars(this)
        this.activity = activity
        cashModuleClientListener = this
        cashModuleServerListener = this
        setUI()
        setActions()
        thread = ClientThread(cashModuleClientListener, cashModuleFactory.registerCallBack())
        thread.start()
//        cashModuleServer = CashModuleServer()
//        cashModuleServer.initCashModuleServer(cashModuleServerListener)
    }

    override fun onClick(v: View) {
        if (ClickUtil.isFastDoubleClick()) {
            return
        }
        when (v.id) {
            R.id.btn_cancel -> {
            }

            R.id.btn_option_exit -> {
                dismiss()
//                cashModuleServer.interrupt()
                thread.interrupt()
                addMoneyThread?.interrupt()
            }

            R.id.btn_add_money_check_exit -> {
                dismiss()
//                cashModuleServer.interrupt()
                thread.interrupt()
                addMoneyThread?.interrupt()
            }

            R.id.btn_inventory -> {
                ClientThread(cashModuleClientListener, cashModuleFactory.money).start()
                tvAddMoneyCheckTitle.text = context.getString(R.string.check_current_total_money_title)
                clAddMoneyPage.visibility = View.VISIBLE
                btnAddMoneyBack.visibility = View.GONE
                btnAddMoneyExit.visibility = View.GONE
                clOption.visibility = View.GONE
            }

            R.id.btn_add_money_check_back -> {
                clOption.visibility = View.VISIBLE
                clAddMoneyCheckPage.visibility = View.GONE
            }

            R.id.btn_add_money -> {
                if (clOption.visibility == View.VISIBLE) {
                    clAddMoneyCheckPage.visibility = View.VISIBLE
                    clOption.visibility = View.GONE
                }
            }

            R.id.btn_get_money -> {
                val cashModuleDialog = CashModuleDialog(context)
                cashModuleDialog.show()
            }

            R.id.btn_add_money_exit -> {
                dismiss()
//                cashModuleServer.interrupt()
                thread.interrupt()
                addMoneyThread?.interrupt()
                CommonUtils.hideSoftKeyboard(activity)
            }

            R.id.btn_add_money_back -> {
                clAddMoneyPage.visibility = View.GONE
                clAddMoneyCheckPage.visibility = View.VISIBLE
                tvAddMoneyTitle.text = context.getString(R.string.add_money_title)
                validateEditHasText()
            }

            R.id.btn_add_money_check_confirm -> {
                if (validateMoneyCount())
                    return
                if (validateMoneyNotZero())
                    return
                if (validateAmountLimit())
                    return
                clAddMoneyPage.visibility = View.VISIBLE
                clAddMoneyCheckPage.visibility = View.GONE
                tvAddMoneyCheckTitle.text = context.getString(R.string.add_money_check_title)
                val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
            }

            R.id.btn_add_money_confirm -> {

                if (tvAddMoneyCheckTitle.text.equals(context.getString(R.string.add_money_success)).or(tvAddMoneyCheckTitle.text.equals(context.getString(R.string.check_current_total_money_title)))) {
                    dismiss()
                    thread.interrupt()
                    return
                }

                if (tvAddMoneyCheckTitle.text.equals(context.getString(R.string.add_money_confirm_title))) {
                    var currentCount: Int
                    if (etHundred.text.isNotEmpty() && etHundred.text.toString().toInt() != 0) {
                        array.put(addMoney(CashModuleFactory.DevicePort.GIVE_PAPER_MONEY.port, etHundred.text.toString().toInt()))
                        currentCount = SharePrefsUtils.getHundredInventory(context)
                        SharePrefsUtils.putHundredInventory(context, currentCount + etHundred.text.toString().toInt())
                    }
                    if (etFifty.text.isNotEmpty() && etFifty.text.toString().toInt() != 0) {
                        array.put(addMoney(CashModuleFactory.DevicePort.FIFTY_DOLLAR.port, etFifty.text.toString().toInt()))
                        currentCount = SharePrefsUtils.getFiftyDollarInventory(context)
                        SharePrefsUtils.putFiftyDollarInventory(context, currentCount + etFifty.text.toString().toInt())
                    }
                    if (etTen.text.isNotEmpty() && etTen.text.toString().toInt() != 0) {
                        array.put(addMoney(CashModuleFactory.DevicePort.TEN_DOLLAR.port, etTen.text.toString().toInt()))
                        currentCount = SharePrefsUtils.getTenDollarInventory(context)
                        SharePrefsUtils.putTenDollarInventory(context, currentCount + etTen.text.toString().toInt())
                    }
                    if (etFive.text.isNotEmpty() && etFive.text.toString().toInt() != 0) {
                        array.put(addMoney(CashModuleFactory.DevicePort.FIVE_DOLLAR.port, etFive.text.toString().toInt()))
                        currentCount = SharePrefsUtils.getFiveDollarInventory(context)
                        SharePrefsUtils.putFiveDollarInventory(context, currentCount + etFive.text.toString().toInt())
                    }
                    if (etOne.text.isNotEmpty() && etOne.text.toString().toInt() != 0) {
                        array.put(addMoney(CashModuleFactory.DevicePort.ONE_DOLLAR.port, etOne.text.toString().toInt()))
                        currentCount = SharePrefsUtils.getOneDollarInventory(context)
                        SharePrefsUtils.putOneDollarInventory(context, currentCount + etOne.text.toString().toInt())
                    }

                    tvHundred.text = SharePrefsUtils.getHundredInventory(context).toString()
                    tvFifty.text = SharePrefsUtils.getFiftyDollarInventory(context).toString()
                    tvTen.text = SharePrefsUtils.getTenDollarInventory(context).toString()
                    tvFive.text = SharePrefsUtils.getFiveDollarInventory(context).toString()
                    tvOne.text = SharePrefsUtils.getOneDollarInventory(context).toString()
                    addMoneyThread = ClientThread(cashModuleClientListener, cashModuleFactory.addMoney(array))
                    addMoneyThread!!.start()
                    return
                }

                tvAddMoneyCheckTitle.text = context.getString(R.string.add_money_confirm_title)
            }
        }
    }

    private fun validateEditHasText() {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
        if (etHundred.text.isNotEmpty()) {
            etHundred.requestFocus()
            return
        }
        if (etFifty.text.isNotEmpty()) {
            etFifty.requestFocus()
            return
        }
        if (etTen.text.isNotEmpty()) {
            etTen.requestFocus()
            return
        }
        if (etFive.text.isNotEmpty()) {
            etFive.requestFocus()
            return
        }
        if (etOne.text.isNotEmpty()) {
            etOne.requestFocus()
            return
        }
    }

    private fun validateAmountLimit(): Boolean {
        if (etHundred.text.isNotEmpty() && etHundred.text.toString().toInt() + SharePrefsUtils.getHundredInventory(context) > 500) {
            CommonUtils.showMessage(context, context.getString(R.string.amount_limit_error))
            return true
        }
        if (etFifty.text.isNotEmpty() && etFifty.text.toString().toInt() + SharePrefsUtils.getFiftyDollarInventory(context) > 500) {
            CommonUtils.showMessage(context, context.getString(R.string.amount_limit_error))
            return true
        }
        if (etTen.text.isNotEmpty() && etTen.text.toString().toInt() + SharePrefsUtils.getTenDollarInventory(context) > 500) {
            CommonUtils.showMessage(context, context.getString(R.string.amount_limit_error))
            return true
        }
        if (etFive.text.isNotEmpty() && etFive.text.toString().toInt() + SharePrefsUtils.getFiveDollarInventory(context) > 500) {
            CommonUtils.showMessage(context, context.getString(R.string.amount_limit_error))
            return true
        }
        if (etOne.text.isNotEmpty() && etOne.text.toString().toInt() + SharePrefsUtils.getOneDollarInventory(context) > 500) {
            CommonUtils.showMessage(context, context.getString(R.string.amount_limit_error))
            return true
        }
        return false
    }

    fun validateMoneyNotZero(): Boolean {
        if (etHundred.text.toString() == "0" ||
            etFifty.text.toString() == "0" ||
            etTen.text.toString() == "0" ||
            etFive.text.toString() == "0" ||
            etOne.text.toString() == "0" ||
            etHundred.text.toString() == "00" ||
            etFifty.text.toString() == "00" ||
            etTen.text.toString() == "00" ||
            etFive.text.toString() == "00" ||
            etOne.text.toString() == "00" ||
            etHundred.text.toString() == "000" ||
            etFifty.text.toString() == "000" ||
            etTen.text.toString() == "000" ||
            etFive.text.toString() == "000" ||
            etOne.text.toString() == "000") {
            CommonUtils.showMessage(context, context.getString(R.string.add_money_zero_warning))
            return true
        } else {
            return false
        }
    }

    fun validateMoneyCount(): Boolean {
        resetTextValue()
        if (etHundred.text.isNotEmpty() && etHundred.text.toString().toInt() != 0) {
            tvHundred.text = etHundred.text
        }
        if (etFifty.text.isNotEmpty() && etFifty.text.toString().toInt() != 0) {
            tvFifty.text = etFifty.text
        }
        if (etTen.text.isNotEmpty() && etTen.text.toString().toInt() != 0) {
            tvTen.text = etTen.text
        }
        if (etFive.text.isNotEmpty() && etFive.text.toString().toInt() != 0) {
            tvFive.text = etFive.text
        }
        if (etOne.text.isNotEmpty() && etOne.text.toString().toInt() != 0) {
            tvOne.text = etOne.text
        }

        if (etHundred.text.isNotEmpty() ||
            etFifty.text.isNotEmpty() ||
            etTen.text.isNotEmpty() ||
            etFive.text.isNotEmpty() ||
            etOne.text.isNotEmpty()) {
            return false
        } else {
            CommonUtils.showMessage(context, context.getString(R.string.add_money_warning))
            return true
        }
    }

    fun resetTextValue() {
        tvHundred.text = "0"
        tvFifty.text = "0"
        tvTen.text = "0"
        tvFive.text = "0"
        tvOne.text = "0"
    }

    fun addMoney(port: String, quantity: Int): JSONObject {
        val box = JSONObject()
        box.put("Name", port)
        box.put("ID", "00")
        box.put("Quantity", quantity)
        return box
    }

    override fun onReceived(response: String) {
        KioskPrinter.addLog("shop_id:" + Config.shop_id)
        KioskPrinter.addLog("CASH_MODULE:" + response)
        if (validateIsResetAll(response)) return
        if (validateIsAddMoney(response)) return
        validateIsGetMoney(response)
    }

    fun validateIsGetMoney(response: String) {
        if (response.contains("GetMoney")) {
            if (validateResponseIsFail(response)) return
            val received: TransactionModel = gson.fromJson(response, TransactionModel::class.java)
            received.cashBoxStatus.forEach {
                when (it.denomination) {
                    100 -> {
                        tvHundred.text = it.quantity.toString()
                        SharePrefsUtils.putHundredInventory(context, it.quantity)
                    }

                    50 -> {
                        tvFifty.text = it.quantity.toString()
                        SharePrefsUtils.putFiftyDollarInventory(context, it.quantity)
                    }

                    10 -> {
                        tvTen.text = it.quantity.toString()
                        SharePrefsUtils.putTenDollarInventory(context, it.quantity)
                    }

                    5 -> {
                        tvFive.text = it.quantity.toString()
                        SharePrefsUtils.putFiveDollarInventory(context, it.quantity)
                    }

                    1 -> {
                        tvOne.text = it.quantity.toString()
                        SharePrefsUtils.putOneDollarInventory(context, it.quantity)
                    }
                }
            }
        }
    }

    fun validateIsAddMoney(response: String): Boolean {
        if (response.contains("AddMoney")) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    btnAddMoneyBack.visibility = View.GONE
                    btnAddMoneyExit.visibility = View.GONE
                    tvAddMoneyCheckTitle.text = context.getString(R.string.add_money_success)
                    ivAddMoneySuccess.setImageResource(R.drawable.success)
                }
            }
            return true
        }
        return false
    }

    fun validateIsResetAll(response: String): Boolean {
        if (response.contains("ResetAll")) {
            resetAllThread?.let {
                if (!resetAllThread!!.isInterrupted) {
                    resetAllThread!!.interrupt()
                }
            }
            return true
        }
        return false
    }

    fun validateResponseIsFail(response: String): Boolean {
        if (response.contains("Fail")) {
            if (!activity.isFinishing) {
                dialog = showErrorDialog(
                    R.string.cash_get_money_error_title,
                    R.string.cash_get_money_error_message
                )
                dialog.show(activity.fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
            }
            return true
        }
        return false
    }

    override fun onServerReceived(response: String) {
        KioskPrinter.addLog("shop_id:" + Config.shop_id)
        KioskPrinter.addLog("CASH_MODULE:" + response)
        val serverReceived: ServerReceivedModel = gson.fromJson(response, ServerReceivedModel::class.java)
        validateIsDoorOpen(serverReceived)
        validateIsDoorClose(serverReceived)
    }

    fun validateIsDoorOpen(serverReceived: ServerReceivedModel) {
        if (serverReceived.command.equals("DoorOpen")) {
            GlobalScope.launch(Dispatchers.Main) {
                tvOptionTitle.text = context.getString(R.string.door_open)
                btnAddMoney.visibility = View.VISIBLE
                btnGetMoney.visibility = View.VISIBLE
                btnOptionExit.visibility = View.VISIBLE
            }
        }
    }

    fun validateIsDoorClose(serverReceived: ServerReceivedModel) {
        if (serverReceived.command.equals("DoorClose")) {
            GlobalScope.launch(Dispatchers.Main) {
                tvOptionTitle.text = context.getString(R.string.door_close)
                btnAddMoney.visibility = View.GONE
                btnGetMoney.visibility = View.GONE
                btnOptionExit.visibility = View.GONE
            }
        }
    }

    fun showErrorDialog(title: Int, message: Int): AlertDialogFragment {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment
            .setTitle(title)
            .setMessage(message)
            .setConfirmButton({
                resetAllThread = ClientThread(this, cashModuleFactory.resetAll())
                resetAllThread!!.start()
                alertDialogFragment.dismiss()
            })
            .setUnCancelAble()
        return alertDialogFragment
    }
}
