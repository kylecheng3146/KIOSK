package com.lafresh.kiosk;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lafresh.kiosk.activity.ShopActivity;
import com.lafresh.kiosk.creditcardlib.nccc.NCCCTransDataBean;
import com.lafresh.kiosk.dialog.LoadingDialog;
import com.lafresh.kiosk.easycard.model.EcPayData;
import com.lafresh.kiosk.httprequest.AddOrderApiRequest;
import com.lafresh.kiosk.httprequest.ApiRequest;
import com.lafresh.kiosk.httprequest.ConfirmOrderTask;
import com.lafresh.kiosk.httprequest.GetOrderInfoApiRequest;
import com.lafresh.kiosk.httprequest.model.GetOrderInfoRes;
import com.lafresh.kiosk.httprequest.model.Invoice;
import com.lafresh.kiosk.httprequest.model.OrderResponse;
import com.lafresh.kiosk.httprequest.model.Shop00Config;
import com.lafresh.kiosk.httprequest.model.WebOrder00;
import com.lafresh.kiosk.model.AddOrderBean;
import com.lafresh.kiosk.model.EasyPayResponse;
import com.lafresh.kiosk.pipay.PiRes;
import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.utils.Arith;
import com.lafresh.kiosk.utils.CommonUtils;
import com.lafresh.kiosk.utils.ComputationUtils;
import com.lafresh.kiosk.utils.Json;
import com.lafresh.kiosk.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Bill {
    public boolean isMember = false;
    public boolean In = false;
    public static Bill Now;
    public static Bill fromServer;
    public ArrayList<Order> AL_Order = new ArrayList<>();

    public int total = 0;
    public String worder_id = "";
    private String custCode = "";//統一編號
    private String buyerNumber = "";//載具
    private String npoBan = "";//捐贈碼
    private String tableNo;

    public JSONArray ja_data_wo02 = new JSONArray();
    public JSONArray webOrder021Array = new JSONArray();
    private OrderResponse orderResponse;
    private EcPayData ecPayData;
    private NCCCTransDataBean ncccTransDataBean;
    private String ncccRes;

    public static final String S_PAY_MENT_CASH = "Cash";
    public static final String S_PAY_MENT_LINE_PAY = "LINE Pay";
    public static final String S_PAYMENT_NCCC = "NCCC";
    public static final String S_PAYMENT_PI = "Pi";
    public static final String S_PAYMENT_ZERO = "ZeroPayment";
    public static final String S_PAYMENT_EASYPAY = "easypay";
    private String payment = S_PAY_MENT_CASH;

    public ArrayList<Ticket> al_ticketSelected = new ArrayList<>();

    public LSN lsn;

    public interface LSN {
        void onSubmitOk(String response);
    }

    //for shop
    public double PriceBeforeDiscount_Get() {
        double Price = 0;
        for (Order order : AL_Order) {
            if(!order.isUsePoint){
                Price += order.totalPrice;
            }
        }
        return Price;
    }

    public void order_remove(Order order) {
        Bill.Now.AL_Order.remove(order);
        ShopActivity.now.ll_order.removeView(order.v_bottom);
        ShopActivity.now.ll_orderMore.removeView(order.v_unfold);
        ShopActivity.now.updateUI();
    }

    public void order_add(Order order) {
        Bill.Now.AL_Order.add(order);
        ShopActivity.now.ll_order.addView(order.v_bottom);
        ShopActivity.now.ll_orderMore.addView(order.v_unfold);
        ShopActivity.now.updateUI();
    }

    public void addTicketWebOrder021(JSONArray webOrder021Array) {
        try {
            for (Ticket ticket : Bill.Now.al_ticketSelected) {
                if (ticket.KindType.equals("1")) {
                    webOrder021Array.put(ticket.getjo_weborder021());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public JSONObject getjo_ticket() {
        JSONObject jo_ticket = new JSONObject();
        JSONArray jo_data = new JSONArray();
        try {
            for (Ticket ticket : al_ticketSelected) {
                if (ticket.KindType.equals("6")) {
                    JSONObject jo_inData = new JSONObject();
                    jo_inData.put("tkno", ticket.tkno);
                    jo_data.put(jo_inData);
                }
            }
            jo_ticket.put("data", jo_data);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        return jo_ticket;
    }


    //for submit
    public Order getOrderBy_worder_sno(int no) {
        Order order_ = null;
        for (Order order : AL_Order) {
            if (order.product.worder_sno == no) {
                order_ = order;
            }
        }

        return order_;
    }

    public static String getTime_forSubmit() {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.MINUTE, 30);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        String M = month <= 9 ? "0" + month : month + "";
        String D = day <= 9 ? "0" + day : day + "";
        String hh = hour <= 9 ? "0" + hour : hour + "";
        String mm = minute <= 9 ? "0" + minute : minute + "";
        String ss = second <= 9 ? "0" + second : second + "";
        String YMD = year + "-" + M + "-" + D;
        String hhmmss = hh + ":" + mm + ":" + ss;

        return YMD + " " + hhmmss;
    }

    public String get_payment_terms() {
        String payment_terms = "1";
        if (payment.equals(S_PAY_MENT_CASH)) {
            payment_terms = "0";
        }

        return payment_terms;
    }

    public void weborder02_setEzCardPay() {
        try {
            JSONObject data01_weborder02 = new JSONObject();
            data01_weborder02.put("pay_id", "K");
            data01_weborder02.put("amount", total + "");
            data01_weborder02.put("isinv", "1");
            data01_weborder02.put("shop_id", Config.shop_id);
            data01_weborder02.put("worder_id", Bill.fromServer.worder_id);
            data01_weborder02.put("relate_id", getEcRelateId());
            ja_data_wo02.put(data01_weborder02);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    //卡號(T0200)|扣款前餘額(後兩項相加)|自動加值金額(T0409)|加值前餘額(T0415，如果沒有加值金額就填0)
    private String getEcRelateId() {
        String cardNo = ecPayData.getMifare_ID();
        String beforeAmt = ecPayData.getN_CPU_EV_Before_TXN();
        String autoLoadAmt = ecPayData.getN_AutoLoad_Amount();
        int beforeDeduct = Integer.valueOf(beforeAmt) + Integer.valueOf(autoLoadAmt);
        return cardNo + "|" + beforeDeduct + "|" + autoLoadAmt + "|" + beforeAmt;
    }

    public void weborder02_setLinePay(String approveCode) {
        try {
            JSONObject data01_weborder02 = new JSONObject();
            data01_weborder02.put("pay_id", "Q006");
            data01_weborder02.put("amount", total + "");
            data01_weborder02.put("isinv", "1");
            data01_weborder02.put("approveCode", approveCode);
            data01_weborder02.put("shop_id", Config.shop_id);
            data01_weborder02.put("worder_id", Bill.fromServer.worder_id);
            data01_weborder02.put("relate_id", Bill.fromServer.worder_id + "|" + approveCode);
            ja_data_wo02.put(data01_weborder02);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void setPiWebOrder02(PiRes piRes) {
        try {
            JSONObject data01_weborder02 = new JSONObject();
            data01_weborder02.put("pay_id", "Q012");
            data01_weborder02.put("amount", total + "");
            data01_weborder02.put("isinv", "1");
            data01_weborder02.put("shop_id", Config.shop_id);
            data01_weborder02.put("worder_id", Bill.fromServer.worder_id);
            //'PI' + '|' + self.barcode + '|' + self.transactionId + '|' + self.TradeAmount; pos的格式
            data01_weborder02.put("relate_id", "PI|" + piRes.getBarcode() + "|" + piRes.getWallet_transaction_id() + "|" + piRes.getAmt());
            ja_data_wo02.put(data01_weborder02);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void setNCCCWebOrder02(NCCCTransDataBean data) {
        try {
            JSONObject data01_weborder02 = new JSONObject();
            data01_weborder02.put("pay_id", "Q011");
            data01_weborder02.put("amount", total + "");
            data01_weborder02.put("isinv", "1");
            data01_weborder02.put("approveCode", data.getApprovalNo());
            data01_weborder02.put("shop_id", Config.shop_id);
            data01_weborder02.put("worder_id", Bill.fromServer.worder_id);
            ja_data_wo02.put(data01_weborder02);
            setNCCCWebOrder021(data);
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private void setNCCCWebOrder021(NCCCTransDataBean data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pay_id", "Q011")
                    .put("proof_no", data.getCardNo())
                    .put("proof_no2", data.getReceiptNo() + "|" + data.getTerminalId())
                    .put("amount", NCCCTransDataBean.parseAmount(data.getTransAmount()))
                    .put("credit_authcode", data.getApprovalNo());
            webOrder021Array.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void setTicket_weborder02() {
        try {
            int amount = 0;
            for (Ticket ticket : al_ticketSelected) {
                amount = ((int) Double.parseDouble(ticket.ProdValue)) + amount;
            }

            ja_data_wo02 = new JSONArray();
            if (amount > 0) {
                JSONObject jo = new JSONObject();
                jo.put("pay_id", "Q004");
                jo.put("amount", amount + "");
                jo.put("isinv", "1");

                ja_data_wo02.put(jo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public String getTicketDiscount() {
        double d = 0;
        for (Ticket ticket : al_ticketSelected) {
            double ticketValue = ComputationUtils.parseDouble(ticket.ProdValue);
            d = Arith.add(d, ticketValue);
        }
        return String.valueOf(d);
    }

    public void setWeborder02ByEasyPay(EasyPayResponse response) {
        try {
            JSONObject data01_weborder02 = new JSONObject();
            data01_weborder02.put("pay_id", response.getPay_id());
            data01_weborder02.put("amount", total + "");
            data01_weborder02.put("approveCode", response.getTransaction_id());
            data01_weborder02.put("worder_id", Bill.fromServer.worder_id);
            ja_data_wo02.put(data01_weborder02);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }


    public AddOrderBean constructAddOrderData() {
        AddOrderBean bean = new AddOrderBean();
        try {
            JSONObject jo_weborder01 = new JSONObject();
            JSONObject jo_weborder011 = new JSONObject();
            JSONArray ja_weborder01 = new JSONArray();
            JSONArray ja_weborder011 = new JSONArray();
            JSONArray ja_data_wo02_combine = new JSONArray();

            for (int i = 0; i < Bill.Now.AL_Order.size(); i++) {
                Product product = Bill.Now.AL_Order.get(i).product;
                //construct the weborder01
                product.worder_sno = ja_weborder01.length() + 1;
                //新增comb_sale_sno 欄位 套餐主項第一個為null
                JSONObject jo_data_weborder01 = Weborder01.setJObyProduct(product, product.iscomb,null);
                ja_weborder01.put(jo_data_weborder01);
                if (product.iscomb == 1) {
                    for (int j = 0; j < product.al_combItem.size(); j++) {
                        Product product_ = product.al_combItem.get(j).productSelected;

                        product_.worder_sno = ja_weborder01.length() + 1;
                        //新增comb_sale_sno 欄位 套餐子項為主項的worder_sno
                        JSONObject jo_data_ = Weborder01.setJObyProduct(product_, 2, String.valueOf(product.worder_sno));
                        ja_weborder01.put(jo_data_);
                    }
                }
                //construct the weborder011
                ArrayList<Product> al_product = new ArrayList<>();
                if (product.comb_type == 0) {
                    al_product.add(product);
                }
                if (product.comb_type == 1) {
                    for (CombItem combItem : product.al_combItem) {
                        al_product.add(combItem.productSelected);
                    }
                }
                for (Product product_ : al_product) {
                    for (Taste taste : product_.al_taste) {
                        for (Detail detail : taste.details) {
                            if (detail.selected) {
                                JSONObject jo_data_weborder011 = new JSONObject();
                                jo_data_weborder011.put("worder_sno", product_.worder_sno);
                                jo_data_weborder011.put("taste_id", taste.tasteId + "");
                                jo_data_weborder011.put("taste_sno", detail.tasteSno + "");
                                jo_data_weborder011.put("name", detail.name);
                                jo_data_weborder011.put("price", detail.price + "");
                                ja_weborder011.put(jo_data_weborder011);
                            }
                        }
                    }
                }
            }

            jo_weborder01.put("data", ja_weborder01);
            jo_weborder011.put("data", ja_weborder011);

            for (int i = 0; i < Bill.fromServer.ja_data_wo02.length(); i++) {
                ja_data_wo02_combine.put(Bill.fromServer.ja_data_wo02.get(i));
            }

            for (int i = 0; i < Bill.Now.ja_data_wo02.length(); i++) {
                ja_data_wo02_combine.put(Bill.Now.ja_data_wo02.get(i));
            }

            String webOrder02String = new JSONObject().put("data", ja_data_wo02_combine).toString();

            //webOrder021
            addTicketWebOrder021(webOrder021Array);
            String webOrder021String = new JSONObject().put("data", webOrder021Array).toString();
            String ticketString = Bill.Now.getjo_ticket().toString();

            bean.setAuthKey(Config.authKey);
            bean.setAccKey(Config.acckey);
            bean.setShopId(Config.shop_id);
            bean.setKioskId(Config.kiosk_id);
            bean.setSaleMethod(Bill.Now.In ? "1" : "2");
            bean.setMealDate(getTime_forSubmit());
            bean.setPaymentTerm(get_payment_terms());
            bean.setStatus("1");
            bean.setwOrderId(fromServer.worder_id);
            bean.setWebOrder01(jo_weborder01.toString());
            bean.setWebOrder011(jo_weborder011.toString());
            bean.setWebOrder02(webOrder02String);
            bean.setWebOrder021(webOrder021String);
            bean.setTicket(ticketString);
            bean.setNcccRes(ncccRes);

            if (fromServer.custCode.length() > 0) {
                bean.setCustCode(fromServer.custCode.trim());
            }
            if (fromServer.buyerNumber.length() > 0) {
                bean.setBuyerNo(fromServer.buyerNumber.trim());
            }
            if (fromServer.npoBan.length() > 0) {
                bean.setNpoBan(fromServer.npoBan.trim());
            }
            if (Config.tableNoConfig != Shop00Config.TABLE_NO_DISABLE && Now.In) {
                bean.setTableNo(Now.tableNo);
            }
            if (fromServer.ecPayData != null) {
                bean.setEcData(Json.toJson(ecPayData));
            }
            if (fromServer.ncccTransDataBean != null) {
                bean.setNcccData(Json.toJson(ncccTransDataBean));
            }

        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        return bean;
    }

    public void submit(final Context ct, String payment) {
        this.payment = payment;
        AddOrderBean bean = constructAddOrderData();
        AddOrderApiRequest addOrderApiRequest = new AddOrderApiRequest(bean,Config.mealDate);

        if (fromServer.custCode.length() > 0) {
            addOrderApiRequest.setCust_code(fromServer.custCode.trim());
        }
        if (fromServer.buyerNumber.length() > 0) {
            addOrderApiRequest.setBuyer_number(fromServer.buyerNumber.trim());
        }
        if (fromServer.npoBan.length() > 0) {
            addOrderApiRequest.setNpoban(fromServer.npoBan.trim());
        }
        if (Config.tableNoConfig != Shop00Config.TABLE_NO_DISABLE && Now.In) {
            addOrderApiRequest.setTable_no(Now.tableNo);
        }

        final LoadingDialog d_loading = new LoadingDialog(ct);
        d_loading.show();
        ConfirmOrderTask confirmOrderTask = new ConfirmOrderTask(addOrderApiRequest);
        addOrderApiRequest.setApiListener(addOrderListener(confirmOrderTask, (Activity) ct, d_loading));
        confirmOrderTask.go();

        String requestString = Json.toJson(bean);
        LogUtil.writeLogToLocalFile("add_order_Request=" + requestString);
        FirebaseCrashlytics.getInstance().setUserId(fromServer.worder_id);
        FirebaseCrashlytics.getInstance().log("add order request");
        FirebaseCrashlytics.getInstance().log(requestString);
    }

    //最後錯誤時的重試，要先從查訂單開始
    public void retrySubmit(Context context) {
        AddOrderBean bean = constructAddOrderData();
        AddOrderApiRequest addOrderApiRequest = new AddOrderApiRequest(bean,Config.mealDate);

        if (fromServer.custCode.length() > 0) {
            addOrderApiRequest.setCust_code(fromServer.custCode.trim());
        }
        if (fromServer.buyerNumber.length() > 0) {
            addOrderApiRequest.setBuyer_number(fromServer.buyerNumber.trim());
        }
        if (fromServer.npoBan.length() > 0) {
            addOrderApiRequest.setNpoban(fromServer.npoBan.trim());
        }
        if (Config.tableNoConfig != Shop00Config.TABLE_NO_DISABLE && Now.In) {
            addOrderApiRequest.setTable_no(Now.tableNo);
        }

        final LoadingDialog d_loading = new LoadingDialog(context);
        d_loading.show();
        ConfirmOrderTask confirmOrderTask = new ConfirmOrderTask(addOrderApiRequest);
        addOrderApiRequest.setApiListener(addOrderListener(confirmOrderTask, (Activity) context, d_loading));
        GetOrderInfoApiRequest getOrderInfoApiRequest = new GetOrderInfoApiRequest(Bill.fromServer.worder_id);
        getOrderInfoApiRequest.setApiListener(getOrderInfoListener(confirmOrderTask, (Activity) context, d_loading));
        confirmOrderTask.retryFromGetOrderInfo(getOrderInfoApiRequest);

        String requestString = Json.toJson(bean);
        LogUtil.writeLogToLocalFile("add_order_Request=" + requestString);
        FirebaseCrashlytics.getInstance().setUserId(fromServer.worder_id);
        FirebaseCrashlytics.getInstance().log("add order request");
        FirebaseCrashlytics.getInstance().log(requestString);
    }

    private ApiRequest.ApiListener addOrderListener(final ConfirmOrderTask confirmOrderTask, final Activity activity, final LoadingDialog d_loading) {
        return new ApiRequest.ApiListener() {
            @Override
            public void onSuccess(ApiRequest apiRequest, final String body) {
                orderResponse = Json.fromJson(body, OrderResponse.class);
                if (orderResponse.getCode() == 0 && orderResponse.getInvoice() != null
                        && orderResponse.getInvoice().getAESKey() != null) {
                    Config.invoice_aes_key = orderResponse.getInvoice().getAESKey();
                }
                LogUtil.writeLogToLocalFile("add_order_Response = " + body);
                FirebaseCrashlytics.getInstance().log("add order response");
                FirebaseCrashlytics.getInstance().log(body);
                endConfirmOrder(activity, d_loading, body);
            }

            @Override
            public void onFail() {
                if (confirmOrderTask.getSwitchTimes() > 0) {
                    GetOrderInfoApiRequest getOrderInfoApiRequest = new GetOrderInfoApiRequest(Bill.fromServer.worder_id);
                    getOrderInfoApiRequest.setApiListener(getOrderInfoListener(confirmOrderTask, activity, d_loading));
                    confirmOrderTask.switchToGetOrderInfo(getOrderInfoApiRequest);
                } else {
                    endConfirmOrder(activity, d_loading, getResCode(-99));
                }
            }
        };
    }

    private ApiRequest.ApiListener getOrderInfoListener(final ConfirmOrderTask confirmOrderTask,
                                                        final Activity activity,
                                                        final LoadingDialog d_loading) {
        return new ApiRequest.ApiListener() {
            @Override
            public void onSuccess(ApiRequest apiRequest, final String body) {
                GetOrderInfoRes res = Json.fromJson(body, GetOrderInfoRes.class);
                if (res.getCode() == 0) {
                    WebOrder00 webOrder00 = res.getWeborder00();
                    String status = webOrder00.getStatus();
                    if ("0".equals(status)) {
                        confirmOrderTask.switchToAddOrder();
                    } else {
                        orderResponse = OrderResponse.generateFromOrderInfo(res);
                        endConfirmOrder(activity, d_loading, Json.toJson(orderResponse));
                    }
                    LogUtil.writeLogToLocalFile("get_order_info_Response = " + body);
                    FirebaseCrashlytics.getInstance().log("get order info response");
                    FirebaseCrashlytics.getInstance().log(body);
                } else {
                    onFail();
                }
            }

            @Override
            public void onFail() {
                endConfirmOrder(activity, d_loading, getResCode(-99));
            }
        };
    }

    private void endConfirmOrder(Activity activity, final LoadingDialog d_loading, final String res) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                d_loading.dismiss();
                lsn.onSubmitOk(res);
            }
        });
    }

    /**
     * 給失敗時，塞回傳格式使用。
     */
    private String getResCode(int code) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getInOut_String(Context context) {
        if (Bill.Now.In) {
            return context.getString(R.string.dineIn);
        } else {
            return context.getString(R.string.takeAway);
        }
    }

    public int get_unTax() {
        int tax = 0;
        tax = (int) Math.round(getInvoiceAmount() / 1.05);
        return tax;
    }

    public int getTax() {
        return getInvoiceAmount() - get_unTax();
    }

    String getTotalHex() {
        String Hex = Integer.toHexString(orderResponse.getTotal());
        while (Hex.length() < 8) {
            Hex = "0" + Hex;
        }
        return Hex;
    }

    public String getUnTax_Hex() {
        String Hex = Integer.toHexString(get_unTax());
        while (Hex.length() < 8) {
            Hex = "0" + Hex;

        }
        return Hex;
    }

    public String[] getQR() {
        String QRCode77 = Tool.getQRCode77();
        String QR_All = Tool.getQRCodeData(Bill.fromServer.getOrderResponse().getWeborder01(), QRCode77);
        String[] QRCodes = Tool.splitQRCodeData(QR_All);
        return QRCodes;
    }

    public int getInvoiceAmount() {
        Invoice invoice = orderResponse.getInvoice();
        String invoiceAmount = invoice.getAmount();
        double amount = Double.valueOf(invoiceAmount);
        return (int) amount;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public void setBuyerNumber(String buyerNumber) {
        this.buyerNumber = buyerNumber;
    }

    public void setNpoBan(String npoBan) {
        this.npoBan = npoBan;
    }

    public String getCustCode() {
        return custCode;
    }

    public String getBuyerNumber() {
        return buyerNumber;
    }

    public String getNpoBan() {
        return npoBan;
    }

    public OrderResponse getOrderResponse() {
        return orderResponse;
    }

    public void setOrderResponse(OrderResponse orderResponse) {
        this.orderResponse = orderResponse;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public EcPayData getEcPayData() {
        return ecPayData;
    }

    public void setEcPayData(EcPayData ecPayData) {
        this.ecPayData = ecPayData;
    }

    public NCCCTransDataBean getNcccTransDataBean() {
        return ncccTransDataBean;
    }

    public void setNcccTransDataBean(NCCCTransDataBean ncccTransDataBean) {
        this.ncccTransDataBean = ncccTransDataBean;
    }

    public String getNcccRes() {
        return ncccRes;
    }

    public void setNcccRes(String ncccRes) {
        this.ncccRes = ncccRes;
    }

    public void print(final Context ct) {
        if (KioskPrinter.getPrinter().isConnect()) {
            print_run(ct);
        } else {
            KioskPrinter.addLog("出單機在印單時沒有連線");
            CommonUtils.showMessage(ct, "Printer no connection");
            FirebaseCrashlytics.getInstance().log("Printer"+ "Printer no connection");
        }
    }

    private void print_run(Context ct) {
        KioskPrinter.addLog("開始印單");
        try {
            KioskPrinter printer = KioskPrinter.getPrinter();
            if ("1".equals(orderResponse.getPayment_terms())) {
                //判斷為智葳就列印點數票券
                if("brogent".equals(BuildConfig.FLAVOR)){
                    printer.printGamePoint(ct);
                }else{
                    printer.printReceipt(ct);
                }
                if (orderResponse.getInvoice() == null && !Config.disableReceiptModule) {
                    CommonUtils.showMessage(ct, "無發票資料，訂單編號 = " + orderResponse.getWorder_id());
                } else {
                    String buyerNumber = orderResponse.getInvoice().getBuyer_number();
                    String npoBan = orderResponse.getInvoice().getNpoban();
                    boolean printInvoice = (buyerNumber == null || buyerNumber.length() == 0) && (npoBan == null || npoBan.length() == 0);
                    if (printInvoice) {
                        printer.printEInvoice(ct);
                    }
                }
            } else {
                //如果是智葳版本加上列印點數票券
                if("brogent".equals(BuildConfig.FLAVOR)){
                    printer.printGamePoint(ct);
                }else{
                    printer.printBill(ct);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void printZeroReceipt(Context ct) {
        fromServer.getOrderResponse().setInvoice(null);
        //判斷為智葳就列印點數票券
        if("brogent".equals(BuildConfig.FLAVOR)){
            KioskPrinter.getPrinter().printGamePoint(ct);
        }else{
            KioskPrinter.getPrinter().printReceipt(ct);
        }
    }

    public void setListener(LSN listener) {
        this.lsn = listener;
    }
}
