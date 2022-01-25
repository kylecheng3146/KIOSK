package com.lafresh.kiosk.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import com.lafresh.kiosk.*
import com.lafresh.kiosk.type.EasyCardTransactionType
import com.lafresh.kiosk.utils.CommonUtils

/**
 * Created by Kevin on 2020/8/20.
 */
@RequiresApi(Build.VERSION_CODES.M)
class EasyCardLoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var btnVisitor: Button
    private lateinit var btnEasyCardLogin: Button

    override fun setUI() {
        btnVisitor = findViewById(R.id.btn_visitor)
        btnEasyCardLogin = findViewById(R.id.btn_easycard_login)
    }

    override fun setActions() {
        btnVisitor.setOnClickListener(this)
        btnEasyCardLogin.setOnClickListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easycard_login)
        HomeActivity.now!!.activities.add(this)
        HomeActivity.now!!.stopIdleProof()
        setUI()
        setActions()
        initLogo()
        HomeActivity.now!!.idleProof()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_easycard_login -> {
                Config.easyCardTransactionType = EasyCardTransactionType.LOGIN
                CommonUtils.intentActivity(this, EasyCardActivity::class.java)
            }
            R.id.btn_visitor -> {
                Config.easyCardTransactionType = EasyCardTransactionType.NONE
                CommonUtils.intentActivity(this, TimeIntervalActivity::class.java)
            }
        }
    }
}
