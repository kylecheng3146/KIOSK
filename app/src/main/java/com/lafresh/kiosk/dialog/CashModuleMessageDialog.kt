package com.lafresh.kiosk.dialog

import android.app.Dialog
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.Kiosk
import com.lafresh.kiosk.R
import com.lafresh.kiosk.activity.HomeActivity

/**
 * Created by Kyle on 2021/4/29.
 */
@RequiresApi(Build.VERSION_CODES.M)
class CashModuleMessageDialog(activity: AppCompatActivity) : Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar),
    View.OnClickListener {

    private lateinit var btn_confirm: Button
    private lateinit var btn_back: Button
    private lateinit var activity: AppCompatActivity

    fun setUI() {
        btn_confirm = findViewById(R.id.btn_confirm)
        btn_back = findViewById(R.id.btn_back)
    }

    fun setActions() {
        btn_confirm.setOnClickListener(this)
        btn_back.setOnClickListener(this)
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
        this.activity = activity
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_confirm -> {
                dismiss()
            }

            R.id.btn_back -> {
                HomeActivity.now!!.closeAllActivities()
                activity.finish()
                dismiss()
            }
        }
    }
}
