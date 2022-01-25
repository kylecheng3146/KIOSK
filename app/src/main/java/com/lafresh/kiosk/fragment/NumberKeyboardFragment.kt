package com.lafresh.kiosk.fragment

import android.app.DialogFragment
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.lafresh.kiosk.*
import com.lafresh.kiosk.activity.CheckOutActivity
import com.lafresh.kiosk.activity.DiningOptionActivity
import com.lafresh.kiosk.activity.HomeActivity
import com.lafresh.kiosk.httprequest.model.CreateOrderRes
import com.lafresh.kiosk.httprequest.model.Member
import com.lafresh.kiosk.manager.LoginManager
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.GuestLoginParams
import com.lafresh.kiosk.model.GuestLoginRes
import com.lafresh.kiosk.model.MemberKey
import com.lafresh.kiosk.type.FlavorType
import com.lafresh.kiosk.utils.ClickUtil
import com.lafresh.kiosk.utils.CommonUtils
import com.lafresh.kiosk.utils.ProgressUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Kyle on 2020/10/16.
 */

@RequiresApi(Build.VERSION_CODES.M)
class NumberKeyboardFragment : DialogFragment(), View.OnClickListener {

    lateinit var btnReturn: Button
    lateinit var etPhoneNumber: EditText
    lateinit var etAddressNumber: EditText
    lateinit var etAddressFloor: EditText
    lateinit var etAddressFloorDash: EditText
    lateinit var button1: Button
    lateinit var button2: Button
    lateinit var button3: Button
    lateinit var button4: Button
    lateinit var button5: Button
    lateinit var button6: Button
    lateinit var button7: Button
    lateinit var button8: Button
    lateinit var button9: Button
    lateinit var button0: Button
    lateinit var btnDash: Button
    lateinit var btnConfirm: Button
    lateinit var buttonDelete: Button
    lateinit var ibDelete: ImageButton
    lateinit var tvAddress: TextView
    var isDeleteClicked = false
    var phoneNumber = ""
    var tempPhoneNumber = ""
    var addressNumber = ""
    var addressFloor = ""
    var addressFloorDash = ""
    var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_DayNight_NoActionBar)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_number_keyboard, container, false)
        HomeActivity.now!!.stopIdleProof()
        etPhoneNumber = view.findViewById(R.id.et_phone_number)
        etPhoneNumber.setInputType(InputType.TYPE_NULL)
        etPhoneNumber.isClickable = false
        etPhoneNumber.requestFocus()
        etAddressNumber = view.findViewById(R.id.et_address_number)
        etAddressNumber.setInputType(InputType.TYPE_NULL)
        etAddressNumber.isClickable = false
        etAddressFloor = view.findViewById(R.id.et_address_floor)
        etAddressFloor.setInputType(InputType.TYPE_NULL)
        etAddressFloor.isClickable = false
        etAddressFloorDash = view.findViewById(R.id.et_address_floor_dash)
        etAddressFloorDash.setInputType(InputType.TYPE_NULL)
        etAddressFloorDash.isClickable = false
        button1 = view.findViewById(R.id.button1)
        button1.setOnClickListener(this)
        button2 = view.findViewById(R.id.button2)
        button2.setOnClickListener(this)
        button3 = view.findViewById(R.id.button3)
        button3.setOnClickListener(this)
        button4 = view.findViewById(R.id.button4)
        button4.setOnClickListener(this)
        button5 = view.findViewById(R.id.button5)
        button5.setOnClickListener(this)
        button6 = view.findViewById(R.id.button6)
        button6.setOnClickListener(this)
        button7 = view.findViewById(R.id.button7)
        button7.setOnClickListener(this)
        button8 = view.findViewById(R.id.button8)
        button8.setOnClickListener(this)
        button9 = view.findViewById(R.id.button9)
        button9.setOnClickListener(this)
        button0 = view.findViewById(R.id.button0)
        button0.setOnClickListener(this)
        btnDash = view.findViewById(R.id.btn_dash)
        btnDash.setOnClickListener(this)
        btnConfirm = view.findViewById(R.id.btn_confirm)
        btnConfirm.setOnClickListener(this)
        btnReturn = view.findViewById(R.id.btn_return)
        btnReturn.setOnClickListener(this)
        buttonDelete = view.findViewById(R.id.button_delete)
        buttonDelete.setOnClickListener(this)
        ibDelete = view.findViewById(R.id.ib_delete)
        ibDelete.setOnClickListener(this)
        tvAddress = view.findViewById(R.id.tv_address)
        tvAddress.text = Config.addressDefault

        val ivLogo = view.findViewById<ImageView>(R.id.ivLogo)
        Kiosk.checkAndChangeUi(activity, Config.titleLogoPath, ivLogo)
        val ivAd = view.findViewById<ImageView>(R.id.ivAd)
        Kiosk.checkAndChangeUi(activity, Config.bannerImg, ivAd)

        phoneNumberTextChanged()
        addressNumberTextChanged()
        addressFloorTextChanged()
        addressFloorDashTextChanged()

        address = Config.addressDefault + "%s號" + "%s樓"
        return view
    }

    private fun addressFloorDashTextChanged() {
        etAddressFloorDash.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    etAddressFloorDash.setSelection(it.length)
                }
            }
        })
    }

    private fun addressFloorTextChanged() {
        etAddressFloor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    etAddressFloor.setSelection(it.length)
                }
            }
        })
    }

    private fun addressNumberTextChanged() {
        etAddressNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    etAddressNumber.setSelection(it.length)
                }
            }
        })
    }

    private fun phoneNumberTextChanged() {
        etPhoneNumber.addTextChangedListener(object : TextWatcher {
            var currentText = ""
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!isDeleteClicked) {
                    currentText = phoneNumber.substring(before, before + 1)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (!isDeleteClicked) {
                    when (s.length) {
                        4 -> {
                            phoneNumber += "-"
                        }

                        5 -> {
                            phoneNumber = phoneNumber.substring(0, phoneNumber.length - 1)
                            phoneNumber += "-"
                            phoneNumber += "*"
                        }

                        6, 7, 8 -> {
                            phoneNumber = phoneNumber.substring(0, phoneNumber.length - 1)
                            phoneNumber += "*"
                        }

                        9 -> {
                            phoneNumber = phoneNumber.substring(0, phoneNumber.length - 1)
                            phoneNumber += "-"
                            phoneNumber += currentText
                        }
                    }
                    if (!isDeleteClicked && (s.length == 8)) {
                        phoneNumber += "-"
                    }
                }
                etPhoneNumber.setSelection(s.length)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setGravity(Gravity.CENTER)
        window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.btn_return -> {
                dismiss()
            }

            R.id.btn_confirm -> {
                // 判斷是否符合手機
                checkFlavorFlow()
            }

            R.id.button1 -> {
                addNumber("1")
            }
            R.id.button2 -> {
                addNumber("2")
            }
            R.id.button3 -> {
                addNumber("3")
            }
            R.id.button4 -> {
                addNumber("4")
            }
            R.id.button5 -> {
                addNumber("5")
            }
            R.id.button6 -> {
                addNumber("6")
            }
            R.id.button7 -> {
                addNumber("7")
            }
            R.id.button8 -> {
                addNumber("8")
            }
            R.id.button9 -> {
                addNumber("9")
            }
            R.id.button0 -> {
                addNumber("0")
            }
            R.id.btn_dash -> {
                addNumber("-")
            }
            R.id.button_delete -> {
                deleteNumber()
            }
            R.id.ib_delete -> {
                isDeleteClicked = true
                phoneNumber = ""
                tempPhoneNumber = ""
                etPhoneNumber.setText("")
            }
        }
    }

    private fun deleteNumber() {
        if (etPhoneNumber.hasFocus()) {
            isDeleteClicked = true
            if (tempPhoneNumber.length > 0) {
                tempPhoneNumber = tempPhoneNumber.substring(0, tempPhoneNumber.length - 1)
            }

            if (phoneNumber.length > 0) {
                phoneNumber = phoneNumber.substring(0, phoneNumber.length - 1)
                if (phoneNumber.length > 0) {
                    if (phoneNumber.substring(phoneNumber.length - 1, phoneNumber.length) == "-") {
                        phoneNumber = phoneNumber.substring(0, phoneNumber.length - 1)
                    }
                }
            }
            etPhoneNumber.setText(phoneNumber)
        }

        if (etAddressNumber.hasFocus()) {
            if (addressNumber.length > 0) {
                addressNumber = addressNumber.substring(0, addressNumber.length - 1)
            }
            etAddressNumber.setText(addressNumber)
        }

        if (etAddressFloor.hasFocus()) {
            if (addressFloor.length > 0) {
                addressFloor = addressFloor.substring(0, addressFloor.length - 1)
            }
            etAddressFloor.setText(addressFloor)
        }

        if (etAddressFloorDash.hasFocus()) {
            if (addressFloorDash.length > 0) {
                addressFloorDash = addressFloorDash.substring(0, addressFloorDash.length - 1)
            }
            etAddressFloorDash.setText(addressFloorDash)
        }
    }

    private fun addNumber(number: String) {

        if (etPhoneNumber.hasFocus()) {
//            CommonUtils.hideSoftKeyboard(etPhoneNumber)
            if (etPhoneNumber.text.length < 12) {
                isDeleteClicked = false
                phoneNumber += number
                tempPhoneNumber += number
                etPhoneNumber.setText(phoneNumber)
            }
        }

        if (etAddressNumber.hasFocus()) {
//            CommonUtils.hideSoftKeyboard(etAddressNumber)
            isDeleteClicked = false
            addressNumber += number
            etAddressNumber.setText(addressNumber)
        }

        if (etAddressFloor.hasFocus()) {
//            CommonUtils.hideSoftKeyboard(etAddressFloor)
            isDeleteClicked = false
            addressFloor += number
            etAddressFloor.setText(addressFloor)
        }

        if (etAddressFloorDash.hasFocus()) {
//            CommonUtils.hideSoftKeyboard(etAddressFloorDash)
            isDeleteClicked = false
            addressFloorDash += number
            etAddressFloorDash.setText(addressFloorDash)
        }
    }

    private fun checkFlavorFlow() {
        when {
            !etPhoneNumber.text.startsWith("09") -> {
                CommonUtils.showMessage(activity, getString(R.string.phone_number_error))
            }

            etPhoneNumber.text.length == 12 -> {
                // 判斷不同客戶走不同流程
                when (BuildConfig.FLAVOR) {
                    // 伽利略走加入地址流程
                    FlavorType.galileo.name -> {
                        addAddressToOrder()
                    }

                    else -> {
                        loginFLow()
                    }
                }
            }

            else -> {
                CommonUtils.showMessage(activity, getString(R.string.phone_number_error))
            }
        }
    }

    private fun addAddressToOrder() {
        when {
            etAddressNumber.text.length == 0 -> {
                CommonUtils.showMessage(activity, "請填寫路段號碼")
            }

            etAddressFloor.text.length == 0 -> {
                CommonUtils.showMessage(activity, "請填寫樓層")
            }

            else -> {
                // 判斷是否有樓的子項是否有填寫.
                Config.phoneNumber = tempPhoneNumber
                if (addressFloorDash.length > 0) {
                    Config.address = String.format(address, addressNumber, addressFloor) + "之" + addressFloorDash
                } else {
                    Config.address = String.format(address, addressNumber, addressFloor)
                }

                createOrUpdateOrder()
            }
        }
    }

    private fun createOrUpdateOrder() {
        ProgressUtils.instance!!.showProgressDialog(activity as AppCompatActivity)
        val manager = OrderManager.getInstance()
        val totalPrice = Bill.Now.PriceBeforeDiscount_Get()
        manager.createOrUpdateOrder(totalPrice, object : OrderManager.OrderListener {
            override fun onSuccess(response: Response<*>) {
                ProgressUtils.instance!!.hideProgressDialog()
                val res = response.body() as CreateOrderRes?
                if (res != null) {
                    manager.orderId = res.id
                    manager.orderTime = res.create_at
                    CommonUtils.intentActivityAndFinish(activity as AppCompatActivity, CheckOutActivity::class.java)
                } else {
                    createOrUpdateOrder()
                }
            }

            override fun onRetry() {
                ProgressUtils.instance!!.hideProgressDialog()
                createOrUpdateOrder()
            }
        }, activity)
    }

    /**
     * 取得會員金鑰後走登入流程，
     * 如果尚未註冊會員則走預註冊流程
     * */
    private fun loginFLow() {
        ProgressUtils.instance!!.showProgressDialog(activity as AppCompatActivity)
        LoginManager().memberKey(tempPhoneNumber, "KIOSK").enqueue(object : Callback<MemberKey> {
            override fun onResponse(call: Call<MemberKey>, response: Response<MemberKey>) {
                if (response.isSuccessful) {
                    Config.acckey = response.body()!!.acckey
                    OrderManager.getInstance().isGuestLogin = true
                    getMemberToken(Config.acckey)
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<MemberKey>, t: Throwable) {
                showErrorDialog()
                ProgressUtils.instance!!.hideProgressDialog()
            }
        })
    }

    private fun callGuestLoginApi() {
        val guestLoginParams = GuestLoginParams()
        guestLoginParams.id = tempPhoneNumber
        guestLoginParams.type = "PHONE_NUMBER"
        guestLogin(guestLoginParams)
    }

    private fun guestLogin(guestLoginParams: GuestLoginParams) {
        LoginManager().guestLogin(guestLoginParams).enqueue(object : Callback<GuestLoginRes> {
            override fun onResponse(call: Call<GuestLoginRes>, response: Response<GuestLoginRes>) {
                if (response.isSuccessful) {
                    Config.acckey = response.body()!!.acckey
                    OrderManager.getInstance().isGuestLogin = true
                    getMemberToken(Config.acckey)
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<GuestLoginRes>, t: Throwable) {
                showErrorDialog()
            }
        })
    }

    private fun checkIntentFrom() {
        if (Config.intentFrom.equals("DiningOptionActivity")) {
            CommonUtils.intentActivityAndFinish(activity as AppCompatActivity, DiningOptionActivity::class.java)
        } else {
            CommonUtils.intentActivityAndFinish(activity as AppCompatActivity, CheckOutActivity::class.java)
        }
    }

    private fun getMemberToken(acckey: String) {
        val loginManager = LoginManager()
        loginManager.setListener(object : LoginManager.Listener {
            override fun onSuccess(member: Member?) {
                ProgressUtils.instance!!.hideProgressDialog()
                Config.token = member!!.token
                Config.group_id = member!!.group_id
                OrderManager.getInstance().isLogin = true
                OrderManager.getInstance().member = member
                checkIntentFrom()
            }

            override fun onFail() {
                ProgressUtils.instance!!.hideProgressDialog()
                val alertDialogFragment =
                    AlertDialogFragment()
                alertDialogFragment
                    .setTitle(R.string.loginFail)
                    .setMessage(getString(R.string.loginFail))
                    .setConfirmButton {
                        if (!ClickUtil.isFastDoubleClick()) {
                            alertDialogFragment.dismiss()
                        }
                    }
                    .setCancelButton(R.string.back) {
                        alertDialogFragment.dismiss()
                        checkIntentFrom()
                    }
                    .setUnCancelAble()
                    .show(fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
            }
        })
        loginManager.getMemberToken(acckey)
    }

    private fun showErrorDialog() {
        val alertDialogFragment =
            AlertDialogFragment()
        alertDialogFragment.setTitle(R.string.not_yet_registered)
        alertDialogFragment.setMessage(getString(R.string.not_yet_registered_message))
        alertDialogFragment.setSubMessage(getString(R.string.not_yet_registered_sub_message))
        alertDialogFragment.setConfirmButton {
            if (!ClickUtil.isFastDoubleClick()) {
                callGuestLoginApi()
                alertDialogFragment.dismiss()
            }
        }
        alertDialogFragment.setCancelButton(R.string.back) {
            alertDialogFragment.dismiss()
        }
        // 判斷如果頁面是尚未開始點餐則顯示開始點餐按鈕
        if (Config.intentFrom.equals("DiningOptionActivity")) {
            alertDialogFragment.setCustomButton(R.string.start_ordering, {
                CommonUtils.intentActivity(activity as AppCompatActivity, DiningOptionActivity::class.java)
                activity.finish()
                dismiss()
            })
        }
        alertDialogFragment.setUnCancelAble()
        alertDialogFragment.show(fragmentManager, AlertDialogFragment.MESSAGE_DIALOG)
    }
}
