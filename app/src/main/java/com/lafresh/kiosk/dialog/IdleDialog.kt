package com.lafresh.kiosk.dialog

import android.app.Dialog
import android.content.Context
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.Window
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.Kiosk
import com.lafresh.kiosk.R
import com.lafresh.kiosk.activity.HomeActivity
import com.lafresh.kiosk.activity.ShopActivity
import com.lafresh.kiosk.utils.ClickUtil

class IdleDialog(ct: Context) : Dialog(ct, android.R.style.Theme_Translucent_NoTitleBar) {

    fun setActions() {
        findViewById<View>(R.id.btn_backHome).setOnClickListener(View.OnClickListener {
            if (ClickUtil.isFastDoubleClick()) {
                return@OnClickListener
            }
            dismiss()
            if (ShopActivity.now != null) {
                ShopActivity.now!!.Finish()
            }
            HomeActivity.now!!.stopIdleProof()
            HomeActivity.now!!.closeAllActivities()
        })
        findViewById<View>(R.id.btn_continue).setOnClickListener { dismiss() }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Kiosk.idleCount = Config.idleCount
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        var showing = false
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Kiosk.hidePopupBars(this)
        setContentView(R.layout.d_idle)
        setActions()
        val countDownTimer: CountDownTimer = object : CountDownTimer((Config.idleWarnTime * 1000).toLong(), 1000) {
            override fun onTick(l: Long) {}
            override fun onFinish() {
                Kiosk.idleCount = Config.idleCount
                HomeActivity.now!!.stopIdleProof()
                findViewById<View>(R.id.btn_backHome).performClick()
            }
        }
        countDownTimer.start()
        setOnShowListener { showing = true }
        setOnDismissListener {
            showing = false
            countDownTimer.cancel()
        }
    }
}
