package com.lafresh.kiosk.utils

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.lafresh.kiosk.R

class ProgressUtils {
    private fun createProgressDialog(activity: AppCompatActivity): Dialog? {
        dialog = Dialog(activity)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.getWindow()!!.setBackgroundDrawable(
                ColorDrawable(0))
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCancelable(false)
        try {
            dialog!!.show()
        } catch (e: Exception) {
        }
        return dialog
    }

    fun hideProgressDialog() {
        if (dialog != null && dialog!!.isShowing) {
            try {
                dialog!!.dismiss()
                dialog = null
            } catch (e: Exception) {
            }
        }
    }

    fun showProgressDialog(activity: AppCompatActivity) {
        if (!activity.isFinishing) {
            if (dialog == null) {
                dialog = createProgressDialog(activity)
            } else {
                try {
                    dialog!!.show()
                } catch (e: Exception) {
                    dialog = null
                }
            }
        }
    }

    companion object {
        @JvmStatic
        var instance: ProgressUtils? = null
            get() {
                if (field == null) {
                    field = ProgressUtils()
                }
                return field
            }
            private set
        private var dialog: Dialog? = null
    }
}
