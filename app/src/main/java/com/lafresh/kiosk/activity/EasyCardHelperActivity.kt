package com.lafresh.kiosk.activity

import android.os.Bundle
import android.view.View
import android.widget.*
import com.lafresh.kiosk.Config
import com.lafresh.kiosk.R
import com.lafresh.kiosk.easycard.EasyCard
import com.lafresh.kiosk.easycard.EasyCardPay
import com.lafresh.kiosk.easycard.EcResTag
import com.lafresh.kiosk.easycard.model.EcCheckoutData
import com.lafresh.kiosk.httprequest.ApiRequest
import com.lafresh.kiosk.httprequest.ApiRequest.ApiListener
import com.lafresh.kiosk.httprequest.GetEcSettle3ApiRequest
import com.lafresh.kiosk.httprequest.SetEcSettle3ApiRequest
import com.lafresh.kiosk.printer.KioskPrinter
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils
import com.lafresh.kiosk.utils.Json
import com.lafresh.kiosk.utils.LogUtil

class EasyCardHelperActivity : BaseActivity(), View.OnClickListener {
    lateinit var etBatchNo: EditText
    lateinit var btnCheckout: Button
    lateinit var btnBack: Button
    lateinit var scrollView: ScrollView
    lateinit var tvMsg: TextView
    lateinit var easyCardPay: EasyCardPay
    lateinit var proceedingBatchNo: String
    lateinit var yearMonth: String
    var date = 1
    var no = 1
    override fun setUI() {
    }

    override fun setActions() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easycardhelper)
        initViews()
    }

    override fun onDestroy() {
        easyCardPay.release()
        super.onDestroy()
    }

    private fun initViews() {
        etBatchNo = findViewById(R.id.etBatchNo)
        btnCheckout = findViewById(R.id.btnCheckout)
        btnCheckout.setOnClickListener(this)
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener(this)
        scrollView = findViewById(R.id.scrollView)
        tvMsg = findViewById(R.id.tvMsg)
        easyCardPay = EasyCardPay(this)
    }

    private fun appendMessage(msg: String) {
        var msg = msg
        msg = """
            
            $msg
            """.trimIndent()
        tvMsg.append(msg)
        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
    }

    override fun onClick(v: View) {
        if (ClickUtil.isFastDoubleClick()) {
            Toast.makeText(this, "Fast Double Click", Toast.LENGTH_SHORT).show()
            return
        }
        when (v.id) {
            R.id.btnCheckout -> startCheckout()
            R.id.btnBack -> {
                CommonUtils.intentActivityAndFinish(this, SplashActivity::class.java)
            }
        }
    }

    private fun startCheckout() {
        val batchNo = etBatchNo.text.toString().trim { it <= ' ' }
        if (batchNo.length > 0) {
            enableButton(false)
            etBatchNo.setText("")
            proceedingBatchNo = batchNo
            appendMessage("BatchNo:$proceedingBatchNo，開始讀取交易紀錄。")
            GetEcSettle3ApiRequest(batchNo).setApiListener(ecSettle3Listener()).go()
        } else {
            appendMessage("請輸入批次號碼")
        }
    }

    //    private void startCheckoutOneMonth() {
    //        enableButton(false);
    //        easyCardPay = new EasyCardPay(this);
    //        int year = datePicker.getYear();
    //        int month = datePicker.getMonth() + 1;
    //        if (month < 10) {
    //            year *= 10;
    //        }
    //        yearMonth = String.valueOf(year).substring(2) + month;
    //        String msg = "開始補結帳整個月份:" + yearMonth + "\n在停止前請不要離開";
    //        tvMsg.setText(msg);
    //        checkout();
    //    }
    private fun checkout() {
        if (date < 31 || no < 3) {
            val batchNo = batchNo
            proceedingBatchNo = batchNo
            GetEcSettle3ApiRequest(batchNo).setApiListener(ecSettle3Listener()).go()
        } else {
            date = 1
            no = 1
            appendMessage("補結帳完成")
            enableButton(true)
        }
    }

    private fun ecSettle3Listener(): ApiListener {
        return object : ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {
                runOnUiThread {
                    val ecCheckoutData = Json.fromJson(body, EcCheckoutData::class.java)
                    if (ecCheckoutData.code == 0) {
                        if (ecCheckoutData.total_Txn_count > 0) {
                            // 有交易紀錄
                            val totCount = ecCheckoutData.total_Txn_count.toString()
                            val msg = ("BatchNo:" + proceedingBatchNo + " 有交易紀錄，筆數:" + totCount +
                                    "/金額:" + ecCheckoutData.total_Txn_amt + "。進行悠遊卡結帳")
                            appendMessage(msg)
                            easyCardPay.setListener(checkoutListener(ecCheckoutData))
                            easyCardPay.checkout(proceedingBatchNo, totCount, ecCheckoutData.total_Txn_amt)
                        } else {
                            val msg = "BatchNo:$proceedingBatchNo 無交易紀錄。"
                            appendMessage(msg)
                            enableButton(true)
                            //                                updateNumber();
//                                checkout();
                        }
                    }
                }
            }

            override fun onFail() {
                runOnUiThread {
                    val msg = "取得悠遊卡結帳資料失敗，BatchNo=$proceedingBatchNo\n停止結帳"
                    appendMessage(msg)
                    enableButton(true)
                }
            }
        }
    }

    private fun checkoutListener(ecCheckoutData: EcCheckoutData): EasyCardPay.Listener {
        return object : EasyCardPay.Listener {
            override fun onResult(result: String) {
                val configData = """悠遊卡結帳結果 authKey:${Config.authKey} shopId:${Config.shop_id} kioskId:${Config.kiosk_id}"""
                LogUtil.writeLogToLocalFile("$configData\n卡機回傳:$result")
                val resultCode = easyCardPay.findTag(result, EcResTag.T3901)
                val t3904 = easyCardPay.findTag(result, EcResTag.T3904)
                val isSuccess = "0" == resultCode
                val isBroken = t3904 != null && t3904.startsWith("66") && "-119" == resultCode
                if (isSuccess) {
                    val msg = "BatchNo=$proceedingBatchNo，結帳成功。"
                    appendMessage(msg)
                    val resData = EasyCard.parseCheckoutData(result, easyCardPay, ecCheckoutData)
                    SetEcSettle3ApiRequest(resData).setRetry(3)
                            .setApiListener(setEcSettleListener(resData)).go()
                    KioskPrinter.getPrinter().printEasyCardCheckOutData(ecCheckoutData)
                    //                    updateNumber();
//                    checkout();
                } else if (isBroken) {
                    // 請暫停使用悠遊卡交易，並請進行報修換機流程
                    appendMessage(getString(R.string.ezCardDongleError))
                    val msg = "發生錯誤，BatchNo=$proceedingBatchNo\n停止結帳"
                    appendMessage(msg)
                    appendMessage(result)
                } else {
                    val msg = "發生錯誤，BatchNo=$proceedingBatchNo\n停止結帳"
                    appendMessage(msg)
                    appendMessage(result)
                }
                enableButton(true)
            }

            override fun onNoICERINI() {}
        }
    }

    private fun setEcSettleListener(resData: EcCheckoutData): ApiListener {
        return object : ApiListener {
            override fun onSuccess(apiRequest: ApiRequest, body: String) {
                runOnUiThread {
                    val msg = "回傳資料至後台成功，BatchNo=$proceedingBatchNo"
                    appendMessage(msg)
                }
            }

            override fun onFail() {
                runOnUiThread {
                    val msg = "回傳悠遊卡資料失敗，BatchNo=$proceedingBatchNo"
                    appendMessage(msg)
                }
            }
        }
    }

    private val batchNo: String
        private get() {
            var dayString = date.toString()
            val noString = "0$no"
            if (date < 10) {
                dayString = "0$dayString"
            }
            return yearMonth + dayString + noString
        }

    private fun updateNumber() {
        if (no == 3) {
            date++
            no = 1
        } else {
            no++
        }
    }

    private fun enableButton(enable: Boolean) {
        btnBack.isEnabled = enable
        btnCheckout.isEnabled = enable
    }
}
