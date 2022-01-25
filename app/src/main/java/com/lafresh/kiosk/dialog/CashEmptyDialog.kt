package com.lafresh.kiosk.dialog

import android.app.Dialog
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.Kiosk
import com.lafresh.kiosk.R

/**
 * Created by Kyle on 2021/5/3.
 */
class CashEmptyDialog(activity: AppCompatActivity, isFull: Boolean, private val function: () -> Unit) : Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar),
    View.OnClickListener {

    private lateinit var activity: AppCompatActivity
    private lateinit var btnConfirm: Button
    private lateinit var btnBack: Button
    private lateinit var tvMessageTitle: TextView
    private lateinit var tvMessage: TextView
    private var isFull = false
    fun setUI() {
        btnConfirm = findViewById(R.id.btn_confirm)
        btnBack = findViewById(R.id.btn_back)
        tvMessageTitle = findViewById(R.id.tv_message_title)
        tvMessage = findViewById(R.id.tv_message)
    }

    fun setActions() {
        btnConfirm.setOnClickListener(this)
        btnBack.setOnClickListener(this)
        validateCashIsFull()
        tvMessage.text = context.getString(R.string.cash_empty_message)
    }

    fun validateCashIsFull() {
        if (this.isFull) {
            tvMessageTitle.text = context.getString(R.string.cash_full_title)
        } else {
            tvMessageTitle.text = context.getString(R.string.cash_empty_title)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Kiosk.idleCount = Config.idleCount
        return super.dispatchTouchEvent(ev)
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_cash_module_message)
        Kiosk.hidePopupBars(this)
        setUI()
        setActions()
        this.isFull = isFull
        this.activity = activity
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_confirm -> {
                dismiss()
                function()
            }

            R.id.btn_back -> {
                dismiss()
                this.activity.finish()
            }
        }
    }
}
