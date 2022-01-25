package com.lafresh.kiosk

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.lafresh.kiosk.dialog.MessageDialog
import com.lafresh.kiosk.fragment.MessageDialogFragment
import com.lafresh.kiosk.fragment.MessageDialogFragment.MessageDialogFragmentListener
import com.lafresh.kiosk.module.GlideApp
import com.lafresh.kiosk.utils.CommonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Kiosk {
    @JvmField
    var idleCount = 30
    fun navBar_prevent(activity: Activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
        activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    @JvmStatic
    fun hidePopupBars(ct: Context) {
        (ct as Activity).window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    @JvmStatic
    fun hidePopupBars(dialog: Dialog) {
        dialog.window!!.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    @JvmStatic
    fun showMessageDialog(context: Context?, message: String?) {
        showMessageDialog(context, message, null)
    }

    @JvmStatic
    fun showMessageDialog(context: Context?, message: String?, listener: MessageDialog.Listener?) {
        showMessageDialog(context, message, listener, 0)
    }

    @JvmStatic
    fun showMessageDialog(context: Context?, message: String?, listener: MessageDialog.Listener?, second: Int) {
        val messageDialog = MessageDialog(context!!)
        messageDialog.setMessage(message)
        messageDialog.setListener(listener)
        if (second > 0) {
            messageDialog.setDelayButton(second)
        }
        messageDialog.show()
    }

    @JvmStatic
    fun showMessageDialogFragment(context: Context, message: String?, listener: MessageDialogFragmentListener?) {
        val activity = context as Activity
        val messageDialogFragment =
            MessageDialogFragment()
        messageDialogFragment.setMessage(message)
        messageDialogFragment.setMessageDialogFragmentListener(listener)
        messageDialogFragment.show(activity.fragmentManager, "DIALOG")
    }

    @JvmStatic
    fun checkAndChangeUi(context: Context, imgPath: String?, ivTarget: ImageView?) {
        if (imgPath != null && ivTarget != null) {
            GlobalScope.launch(Dispatchers.Default) {
                GlideApp.get(context).clearDiskCache()
            }
            GlideApp.get(context).clearMemory()
            CommonUtils.loadImage(context, ivTarget, imgPath)
        }
    }
}
