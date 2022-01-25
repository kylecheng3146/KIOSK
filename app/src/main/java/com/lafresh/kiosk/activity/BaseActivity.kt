package com.lafresh.kiosk.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.KeyEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.Kiosk
import com.lafresh.kiosk.R
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils
import com.lafresh.kiosk.utils.NetworkUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initLogo()
    }
    override fun onStart() {
        super.onStart()
        Kiosk.hidePopupBars(this)
        // 初始化log套件
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    override fun onResume() {
        super.onResume()
        detectNetwork()
    }

    fun initLogo() {
        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        Kiosk.checkAndChangeUi(this, Config.titleLogoPath, ivLogo)
        val ivAd = findViewById<ImageView>(R.id.ivAd)
        Kiosk.checkAndChangeUi(this, Config.bannerImg, ivAd)
    }

    /**
     * 虛擬返回鍵事件
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CommonUtils.showMessage(this, this.getString(R.string.backButtonFeedback))
            // do nothing..
        }
        return false
    }

    fun detectNetwork() {
        if (!NetworkUtils.isOnline(this)) {
            val alertDialogFragment =
                AlertDialogFragment()
            alertDialogFragment
                    .setTitle(R.string.noNetwork)
                    .setMessage(R.string.checkNetwork)
                    .setConfirmButton {
                        if (!ClickUtil.isFastDoubleClick()) {
                            alertDialogFragment.dismiss()
                            detectNetwork()
                        }
                    }
                    .setCancelButton(R.string.back) { finish() }
                    .setUnCancelAble()
                    .show(fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
        }
    }

    abstract fun setUI()
    abstract fun setActions()
}
