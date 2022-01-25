package com.lafresh.kiosk.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.lafresh.kiosk.*
import com.lafresh.kiosk.activity.*
import com.lafresh.kiosk.dialog.CashDialog
import com.lafresh.kiosk.fragment.AlertDialogFragment
import com.lafresh.kiosk.httprequest.model.CreateOrderRes
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.*
import com.lafresh.kiosk.type.EasyCardTransactionType
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.type.PaymentsType
import com.lafresh.kiosk.type.ScanType
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils
import com.lafresh.kiosk.utils.ProgressUtils
import retrofit2.Response

/**
 * Created by Kyle on 2020/11/9.
 */

@RequiresApi(Build.VERSION_CODES.M)
class PaymentsAdapter(private val mContext: Context, private val mData: List<PaymentMethod>) : RecyclerView.Adapter<PaymentsAdapter.ViewHolder>() {
    // 建立ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 宣告元件
        val btnType: Button = itemView.findViewById(R.id.btn_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 連結項目布局檔list_item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payments, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        // 設置tvTitle要顯示的內容\
        when (mData[position].id) {
            PaymentsType.TAIWAN_PAY.name -> {
                if (BuildConfig.FLAVOR.equals(FlavorType.liangshehan.name).or(BuildConfig.FLAVOR.equals(FlavorType.bafang.name).or(BuildConfig.FLAVOR.equals(FlavorType.FormosaChang.name).or(BuildConfig.FLAVOR.equals(FlavorType.mwd.name))))) {
                    holder.btnType.text = mContext.getString(R.string.taiwan_pay)
                } else {
                    holder.btnType.setBackgroundResource(R.drawable.icon_taiwan_pay)
                }
                holder.btnType.setOnClickListener {
                    if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
                    if (isSetJourdenessPickupMethod()) {
                        isNewApiFlow(PaymentsType.TAIWAN_PAY.name)
                    }
                }
            }
            PaymentsType.LINE_PAY.name -> {
                if (BuildConfig.FLAVOR.equals(FlavorType.liangshehan.name).or(BuildConfig.FLAVOR.equals(FlavorType.bafang.name).or(BuildConfig.FLAVOR.equals(FlavorType.FormosaChang.name).or(BuildConfig.FLAVOR.equals(FlavorType.mwd.name))))) {
                    holder.btnType.text = mContext.getString(R.string.line_pay)
                } else {
                    holder.btnType.setBackgroundResource(R.drawable.icon_line_pay)
                }
                holder.btnType.setOnClickListener {
                    if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
                    if (isSetJourdenessPickupMethod()) {
                        isNewApiFlow(PaymentsType.LINE_PAY.name)
                    }
                }
            }

            PaymentsType.CREDIT_CARD.name -> {
                if (BuildConfig.FLAVOR.equals(FlavorType.liangshehan.name).or(BuildConfig.FLAVOR.equals(FlavorType.bafang.name).or(BuildConfig.FLAVOR.equals(FlavorType.FormosaChang.name).or(BuildConfig.FLAVOR.equals(FlavorType.mwd.name))))) {
                    when {
                        Config.useNcccByKiosk.not() -> {
                            holder.btnType.setBackgroundResource(R.drawable.btn_disabled)
                            holder.btnType.text = mContext.getString(R.string.creditCard)
                            holder.btnType.isEnabled = false
                        }

                        else -> {
                            holder.btnType.text = mContext.getString(R.string.creditCard)
                        }
                    }
                } else {
                    holder.btnType.setBackgroundResource(R.drawable.icon_credit_card)
                }
                holder.btnType.setOnClickListener {
                    if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
                    if (isSetJourdenessPickupMethod()) {
                        isNewApiFlow(PaymentsType.CREDIT_CARD.name)
                    }
                }
            }

            PaymentsType.Easy_Wallet.name -> {
                if (BuildConfig.FLAVOR.equals(FlavorType.liangshehan.name).or(BuildConfig.FLAVOR.equals(FlavorType.bafang.name).or(BuildConfig.FLAVOR.equals(FlavorType.FormosaChang.name).or(BuildConfig.FLAVOR.equals(FlavorType.mwd.name))))) {
                    holder.btnType.text = mContext.getString(R.string.easy_wallet)
                } else {
                    if(BuildConfig.FLAVOR == FlavorType.lanxin.name){
                        holder.btnType.setBackgroundResource(R.drawable.icon_easy_card)
                    }else{
                        holder.btnType.setBackgroundResource(R.drawable.icon_easy_wallet)
                    }
                }
                holder.btnType.setOnClickListener {
                    if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
                    if (isSetJourdenessPickupMethod()) {
                        isNewApiFlow(PaymentsType.Easy_Wallet.name)
                    }
                }
            }

            PaymentsType.NCCC_EASY_CARD.name, PaymentsType.NCCC_IPASS.name, PaymentsType.NCCC_ICASH.name, PaymentsType.NCCC_HAPPY_CASH.name -> {
                if (BuildConfig.FLAVOR.equals(FlavorType.liangshehan.name).or(BuildConfig.FLAVOR.equals(FlavorType.bafang.name).or(BuildConfig.FLAVOR.equals(FlavorType.FormosaChang.name).or(BuildConfig.FLAVOR.equals(FlavorType.mwd.name))))) {
                    holder.btnType.text = mData[position].name
                } else {
                    when (mData[position].id) {
                        PaymentsType.NCCC_EASY_CARD.name -> {
                            holder.btnType.setBackgroundResource(R.drawable.icon_easy_card)
                        }
                        PaymentsType.NCCC_ICASH.name -> {
                            holder.btnType.setBackgroundResource(R.drawable.icon_icash)
                        }
                        PaymentsType.NCCC_IPASS.name -> {
                            holder.btnType.setBackgroundResource(R.drawable.icon_ipass)
                        }
                        PaymentsType.NCCC_HAPPY_CASH.name -> {
                            holder.btnType.setBackgroundResource(R.drawable.icon_happy_cash)
                        }
                    }
                }
                holder.btnType.setOnClickListener {
                    if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
                    if (isSetJourdenessPickupMethod()) {
                        isNewApiFlow(mData[position].id)
                    }
                }
            }

            PaymentsType.CASH.name -> {
                if (BuildConfig.FLAVOR.equals(FlavorType.liangshehan.name).or(BuildConfig.FLAVOR.equals(FlavorType.bafang.name).or(BuildConfig.FLAVOR.equals(FlavorType.FormosaChang.name).or(BuildConfig.FLAVOR.equals(FlavorType.mwd.name))))) {
                    holder.btnType.text = mContext.getString(R.string.pay_by_cash_with_space)
                } else {
                    holder.btnType.setBackgroundResource(R.drawable.icon_cash)
                }
                holder.btnType.setOnClickListener {
                    if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
                    if (isSetJourdenessPickupMethod()) {
                        isNewApiFlow(PaymentsType.CASH.name)
                    }
                }
            }

            PaymentsType.CASH_MODULE.name -> {
                if (BuildConfig.FLAVOR.equals(FlavorType.liangshehan.name).or(BuildConfig.FLAVOR.equals(FlavorType.bafang.name).or(BuildConfig.FLAVOR.equals(FlavorType.FormosaChang.name).or(BuildConfig.FLAVOR.equals(FlavorType.mwd.name))))) {
                    holder.btnType.text = mContext.getString(R.string.pay_by_cash_module)
                } else {
                    holder.btnType.setBackgroundResource(R.drawable.icon_cash_module)
                }
                holder.btnType.setOnClickListener {
                    if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
                    if (isSetJourdenessPickupMethod()) {
                        isNewApiFlow(PaymentsType.CASH_MODULE.name)
                    }
                }
            }

            PaymentsType.MWD_PAY.name -> {
                if (BuildConfig.FLAVOR.equals(FlavorType.mwd.name)) {
                    holder.btnType.text = mContext.getString(R.string.mwd_pay)
                } else {
                    holder.btnType.setBackgroundResource(R.drawable.icon_cash_module)
                }
                holder.btnType.setOnClickListener {
                    if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
                    if (isSetJourdenessPickupMethod()) {
                        isNewApiFlow(PaymentsType.MWD_PAY.name)
                    }
                }
            }

            PaymentsType.EASY_CARD.name -> {
                if (BuildConfig.FLAVOR.equals(FlavorType.liangshehan.name).or(BuildConfig.FLAVOR.equals(FlavorType.bafang.name).or(BuildConfig.FLAVOR.equals(FlavorType.FormosaChang.name).or(BuildConfig.FLAVOR.equals(FlavorType.mwd.name))))) {
                    when {
                        Config.useEasyCardByKiosk.not() -> {
                            holder.btnType.setBackgroundResource(R.drawable.btn_disabled)
                            holder.btnType.text = mContext.getString(R.string.easyCard)
                            holder.btnType.isEnabled = false
                        }

                        (BuildConfig.FLAVOR == FlavorType.liangshehan.name).or(BuildConfig.FLAVOR == FlavorType.bafang.name) -> {
                            holder.btnType.text = mContext.getString(R.string.easyCard)
                        }

                        else -> {
                            holder.btnType.text = mContext.getString(R.string.easyCard)
                        }
                    }
                } else {
                    holder.btnType.setBackgroundResource(R.drawable.icon_easy_card)
                }
                holder.btnType.setOnClickListener {
                    if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
                    if (isSetJourdenessPickupMethod()) {
                        isNewApiFlow(PaymentsType.EASY_CARD.name)
                    }
                }
            }

            PaymentsType.PI_PAY.name -> {
                holder.btnType.text = mContext.getString(R.string.piPay)
                holder.btnType.setOnClickListener {
                    if (ClickUtil.isFastDoubleClick()) return@setOnClickListener
                    if (isSetJourdenessPickupMethod()) {
                        isNewApiFlow(PaymentsType.PI_PAY.name)
                    }
                }
            }
        }

        // 如果為八方的環境，第二顆按鈕則顯示不同顏色
        setSecondButtonColor(position, holder)
    }

    private fun isSetJourdenessPickupMethod(): Boolean {
        if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name) && Config.isShopping && OrderManager.getInstance().pickupMethod == null) {
            showNoSetPickupMethodDialog()
            return false
        }
        return true
    }

    private fun setSecondButtonColor(position: Int, holder: ViewHolder) {
        if (mData[position].id == PaymentsType.EASY_CARD.name && Config.useEasyCardByKiosk.not()) {
            return
        }

        if (mData[position].id == PaymentsType.CREDIT_CARD.name && Config.useNcccByKiosk.not()) {
            return
        }

        if ((BuildConfig.FLAVOR == FlavorType.bafang.name).and((position + 1) % 2 == 0)) {
            holder.btnType.setBackgroundResource(R.drawable.selector_btn_large_yellow_circle)
        }
    }

    private fun isNewApiFlow(type: String) {
        if (ClickUtil.isFastDoubleClick()) {
            return
        }
        if (Config.isNewOrderApi) {
            addPaymentInfo(type)
        } else {
            toPaymentPage(type)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    private fun toPaymentPage(type: String) {
        val bundle = Bundle()
        when (type) {
            PaymentsType.CREDIT_CARD.name -> {
                bundle.putString("esvcIndicator", "N")
                CommonUtils.intentActivity(mContext as AppCompatActivity, NCCCActivity::class.java, bundle)
            }
            PaymentsType.EASY_CARD.name -> {
                Config.easyCardTransactionType = EasyCardTransactionType.PAY
                CommonUtils.intentActivity(mContext as AppCompatActivity, EasyCardActivity::class.java)
            }
            PaymentsType.NCCC_EASY_CARD.name, PaymentsType.NCCC_IPASS.name, PaymentsType.NCCC_ICASH.name, PaymentsType.NCCC_HAPPY_CASH.name -> {
                bundle.putString("esvcIndicator", "E")
                CommonUtils.intentActivity(mContext as AppCompatActivity, NCCCActivity::class.java, bundle)
            }
            PaymentsType.LINE_PAY.name -> {
                Config.scanType = ScanType.LINE_PAY.name
                CommonUtils.intentActivity(mContext as AppCompatActivity, ScanActivity::class.java)
            }
            PaymentsType.TAIWAN_PAY.name -> {
                Config.scanType = ScanType.TAIWAN_PAY.name
                CommonUtils.intentActivity(mContext as AppCompatActivity, ScanActivity::class.java)
            }
            PaymentsType.PI_PAY.name -> {
                Config.scanType = ScanType.PI_PAY.name
                CommonUtils.intentActivity(mContext as AppCompatActivity, ScanActivity::class.java)
            }
            PaymentsType.Easy_Wallet.name -> {
                Config.scanType = ScanType.EASY_WALLET.name
                CommonUtils.intentActivity(mContext as AppCompatActivity, ScanActivity::class.java)
            }
            PaymentsType.MWD_PAY.name -> {
                Config.scanType = ScanType.MWD_PAY.name
                CommonUtils.intentActivity(mContext as AppCompatActivity, ScanActivity::class.java)
            }
            PaymentsType.CASH.name -> {
                CashDialog(mContext).show()
            }
            PaymentsType.CASH_MODULE.name -> {
                CommonUtils.intentActivity(mContext as AppCompatActivity, CashPaymentActivity::class.java)
            }
            else -> { }
        }
    }

    private fun addPaymentInfo(type: String) {
        ProgressUtils.instance!!.showProgressDialog(mContext as AppCompatActivity)
        val manager = OrderManager.getInstance()
        val tempPayment = Payment()
        tempPayment.type = type
        tempPayment.payment_amount = manager.totalFee.toInt()
        val receiptData = OrderManager.getInstance().receiptData
        when (type) {
            PaymentsType.CASH.name -> {
                receiptData.takeNumber = false
            }
            else -> {
                receiptData.takeNumber = !Config.disableReceiptModule
            }
        }
        manager.receiptData = receiptData
        manager.setTempPayment(tempPayment)
        createOrUpdateOrder(manager, type)
    }

    private fun createOrUpdateOrder(manager: OrderManager, type: String) {
        manager.createOrUpdateOrder(manager.total.toDouble(), object : OrderManager.OrderListener {
            override fun onSuccess(response: Response<*>?) {
                ProgressUtils.instance!!.hideProgressDialog()
                val res = response?.body() as CreateOrderRes?
                if (res != null) {
                    when {
                        res.is_get_receipt == false.and(type == PaymentsType.CASH.name) -> {
                            toPaymentPage(type)
                        }

                        res.is_get_receipt == false && type != "" -> {
                            showNoReceiptDialog(manager)
                        }

                        else -> {
                            toPaymentPage(type)
                        }
                    }
                }
            }

            override fun onRetry() {
                ProgressUtils.instance!!.hideProgressDialog()
                addPaymentInfo(type)
            }
        }, mContext as Activity?)
    }

    private fun showNoSetPickupMethodDialog() {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment.setTitle(R.string.not_set_pickup_method)
        alertDialogFragment.setMessage(mContext.getString(R.string.not_set_pickup_method))
        alertDialogFragment.setConfirmButton {
            if (!ClickUtil.isFastDoubleClick()) {
                alertDialogFragment.dismiss()
            }
        }
        alertDialogFragment.setCancelButton(R.string.returnHomeButton) {
            alertDialogFragment.dismiss()
            if (ShopActivity.now != null) {
                ShopActivity.now!!.Finish()
            }
            HomeActivity.now!!.stopIdleProof()
            HomeActivity.now!!.closeAllActivities()
        }
        alertDialogFragment.setUnCancelAble()
        alertDialogFragment.show((mContext as AppCompatActivity).fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }

    private fun showNoReceiptDialog(manager: OrderManager) {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment.setTitle(R.string.not_get_receipt)
        alertDialogFragment.setMessage(mContext.getString(R.string.not_get_receipt_message))
        alertDialogFragment.setConfirmButton {
            if (!ClickUtil.isFastDoubleClick()) {
                alertDialogFragment.dismiss()
            }
            ProgressUtils.instance!!.showProgressDialog(mContext as AppCompatActivity)
            createOrUpdateOrder(manager, "")
        }
        alertDialogFragment.setCancelButton(R.string.returnHomeButton) {
            alertDialogFragment.dismiss()
            if (ShopActivity.now != null) {
                ShopActivity.now!!.Finish()
            }
            HomeActivity.now!!.stopIdleProof()
            HomeActivity.now!!.closeAllActivities()
        }
        alertDialogFragment.setUnCancelAble()
        alertDialogFragment.show((mContext as AppCompatActivity).fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }
}
