package com.lafresh.kiosk.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.lafresh.kiosk.*
import com.lafresh.kiosk.fragment.NumberKeyboardFragment
import com.lafresh.kiosk.type.ScanType
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils

/**
 * Created by Kyle on 2020/10/12.
 */
@RequiresApi(Build.VERSION_CODES.M)
class EarnPointsWayActivity : BaseActivity(), View.OnClickListener {

    var clMember: ConstraintLayout? = null
    var clCellPhone: ConstraintLayout? = null
    var btnReturn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earn_points_way)
        HomeActivity.now!!.activities.add(this)
        HomeActivity.now!!.stopIdleProof()

        setUI()
        setActions()

        initLogo()
        HomeActivity.now!!.idleProof()
    }

    override fun setUI() {
        clMember = findViewById(R.id.cl_member)
        clCellPhone = findViewById(R.id.cl_phone_number)
        btnReturn = findViewById(R.id.btn_return)
    }

    override fun setActions() {
        clMember!!.setOnClickListener(this)
        clCellPhone!!.setOnClickListener(this)
        btnReturn!!.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (ClickUtil.isFastDoubleClick()) return
        when (view!!.id) {
            R.id.cl_member -> {
                Config.scanType = ScanType.LOGIN.toString()
                CommonUtils.intentActivityAndFinish(this, ScanActivity::class.java)
            }

            R.id.cl_phone_number -> {
                showNumberLeyboardDialogFragment()
            }

            R.id.btn_return -> {
                if (Config.intentFrom.equals("DiningOptionActivity")) {
                    CommonUtils.intentActivityAndFinish(this, DiningOptionActivity::class.java)
                } else {
                    finish()
                }
            }
        }
    }

    private fun showNumberLeyboardDialogFragment() {
        val numberKeyboardFragment = NumberKeyboardFragment()
        numberKeyboardFragment.setCancelable(false)
        numberKeyboardFragment.show(this.fragmentManager, "DIALOG")
    }
}
