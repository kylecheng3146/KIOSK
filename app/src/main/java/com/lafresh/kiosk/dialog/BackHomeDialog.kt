package com.lafresh.kiosk.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.Kiosk
import com.lafresh.kiosk.R
import com.lafresh.kiosk.activity.HomeActivity
import com.lafresh.kiosk.activity.ShopActivity
import com.lafresh.kiosk.utils.ClickUtil

@RequiresApi(Build.VERSION_CODES.M)
class BackHomeDialog(context: Context) : Dialog(context, android.R.style.Theme_Translucent_NoTitleBar) {
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

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        ShopActivity.AL_DialogToClose!!.remove(this)
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        Kiosk.hidePopupBars(this)
        setContentView(R.layout.d_backhome)
        ShopActivity.AL_DialogToClose!!.add(this)
        setActions()
    }
}
