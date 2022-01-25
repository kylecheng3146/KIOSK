package com.lafresh.kiosk;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lafresh.kiosk.activity.CheckOutActivity;
import com.lafresh.kiosk.dialog.AlertDialogFragment;
import com.lafresh.kiosk.httprequest.AddOrderApiRequest;
import com.lafresh.kiosk.httprequest.ApiRequest;
import com.lafresh.kiosk.httprequest.model.OrderResponse;
import com.lafresh.kiosk.utils.ClickUtil;
import com.lafresh.kiosk.utils.FormatUtil;
import com.lafresh.kiosk.utils.Json;
import com.nuwarobotics.service.IClientId;
import com.nuwarobotics.service.agent.NuwaRobotAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageButton;

public class CheckOutOptionActivity extends AppCompatActivity implements View.OnClickListener {
    GifImageButton gifBtnConfirmOrder;
    Button Btn_PointDiscount, Btn_CouponDiscount;
    Button btn_cancel;
    TextView tv_msg, tv_total;
    LinearLayout ll_order;
    RelativeLayout rl_discount;
    RelativeLayout rlDiscount;
    RelativeLayout rlTicketDiscount;
    RelativeLayout rlOriginPrice;
    TextView tvDiscount;
    TextView tvTicketDiscount;
    TextView tvOriginPrice;
    Button btnRemoveTicket;
    private D_Loading d_loading;
    private NuwaRobotAPI mRobotAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_checkoutoption);
        HomeActivity.now.activities.add(this);
        Kiosk.hidePopupBars(this);
        setUI();
        setActions();

        ordersFromServer_Get(Bill.fromServer.worder_id);

        //Nuwa SDK
        mRobotAPI = NuwaRobotAPI.getInst();

        if(mRobotAPI == null){
            mRobotAPI = new NuwaRobotAPI(this, new IClientId(getPackageName()));
        }
    }

    void setUI() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        tv_msg = findViewById(R.id.tv_msg);
        tv_total = findViewById(R.id.tv_total);
        ll_order = findViewById(R.id.ll_order);
        Btn_PointDiscount = findViewById(R.id.Btn_PointDiscount);
        Btn_CouponDiscount = findViewById(R.id.Btn_CouponDiscount);
        rl_discount = findViewById(R.id.rl_discount);

        rlDiscount = findViewById(R.id.rlDiscount);
        rlTicketDiscount = findViewById(R.id.rlTicketDiscount);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTicketDiscount = findViewById(R.id.tvTicketDiscount);
        tvOriginPrice = findViewById(R.id.tvOriginPrice);
        btnRemoveTicket = findViewById(R.id.btnRemoveTicket);
        rlOriginPrice = findViewById(R.id.rlOriginPrice);

        gifBtnConfirmOrder = findViewById(R.id.gifBtnConfirmOrder);

        gifBtnConfirmOrder.requestFocus();
        btn_cancel = findViewById(R.id.btn_cancel);
        ll_order.removeAllViews();
        gifBtnConfirmOrder.setVisibility(View.INVISIBLE);
        Btn_CouponDiscount.setVisibility(Bill.Now.isMember ? View.VISIBLE : View.INVISIBLE);
        rl_discount.setVisibility(Bill.Now.isMember ? View.VISIBLE : View.GONE);
        d_loading = new D_Loading(this);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        Kiosk.checkAndChangeUi(this, Config.titleLogoPath, ivLogo);

//        ImageView ivAd = findViewById(R.id.ivAd);
//        Kiosk.checkAndChangeUi(this, Config.bannerImg, ivAd);
    }

    void setActions() {
        gifBtnConfirmOrder.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        Btn_CouponDiscount.setOnClickListener(this);
        findViewById(R.id.btn_restart).setOnClickListener(this);
        btnRemoveTicket.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.gifBtnConfirmOrder:
                confirmOrder();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_restart:
                new BackHomeDialog(CheckOutOptionActivity.this).show();
                break;
            case R.id.Btn_CouponDiscount:
                showTicketDialog();
                break;
            case R.id.btnRemoveTicket:
                Bill.Now.al_ticketSelected.clear();
                rlTicketDiscount.setVisibility(View.GONE);
                Bill.Now.setTicket_weborder02();
                ordersFromServer_Get(Bill.fromServer.worder_id);
                break;
            default:
                break;
        }
    }

    private void confirmOrder() {
        if (Bill.fromServer.total == 0) {
            //直接付款，為了可直接使用商品兌換券
//            PrintReceiptDialog dialog = new PrintReceiptDialog(Act_CheckOutOption.this);
//            dialog.show();

            //#6197 因兌換券功能未啟用，擋下金額為0 避免造成錯誤
            String errMsg = getString(R.string.totalIsZero);
            Kiosk.showMessageDialog(CheckOutOptionActivity.this, errMsg);
            enableConfirmButton(false);
            return;
        }
        Intent intent = new Intent(CheckOutOptionActivity.this, CheckOutActivity.class);
        startActivity(intent);
    }

    private void showTicketDialog() {
        final D_Ticket d_ticket = new D_Ticket(CheckOutOptionActivity.this);
        d_ticket.show();
        d_ticket.lsn = new D_Ticket.LSN() {
            @Override
            public void onOK() {
                boolean hasRedeemTicket = false;

                //remove repeat product about exchange ticket
                ArrayList<Order> al_orderToRemove = new ArrayList<>();
                for (Ticket ticket : Bill.Now.al_ticketSelected) {
                    if (ticket.KindType.equals(Ticket.PRODUCT_TYPE)) {
                        hasRedeemTicket = true;
                        for (Order order : Bill.Now.AL_Order) {
                            if (order.product.prod_id.equals(ticket.ProdID)) {
                                al_orderToRemove.add(order);
                            }
                        }
                    }
                }

                for (Order order : al_orderToRemove) {
                    Bill.Now.AL_Order.remove(order);
                }

                Bill.Now.setTicket_weborder02(); //set discount json
                d_ticket.dismiss();
                //無選擇商品也無兌換商品則不連API
                if (Bill.Now.AL_Order.size() > 0 || hasRedeemTicket) {
                    ordersFromServer_Get(Bill.fromServer.worder_id);
                } else {
                    enableConfirmButton(false);
                    Bill.Now.al_ticketSelected.clear();
                    Bill.fromServer.getOrderResponse().setWeborder02(null);
                    Bill.fromServer.getOrderResponse().setDiscount(0);
                    updateUI();
                }
            }
        };
    }

    private void enableConfirmButton(boolean b) {
        if (b) {
            gifBtnConfirmOrder.setOnClickListener(this);
            gifBtnConfirmOrder.setBackgroundResource(R.drawable.gif_confirm_order);
        } else {
            gifBtnConfirmOrder.setOnClickListener(null);
            gifBtnConfirmOrder.setBackgroundResource(R.drawable.checkout_disabled);
        }
    }

    /**
     * @param orderId 空字串會取新的worderId,傳入則會沿用。
     */
    void ordersFromServer_Get(final String orderId) {
        try {
            JSONObject jo_weborder01 = new JSONObject();
            JSONObject jo_weborder011 = new JSONObject();
            JSONObject jo_weborder021 = new JSONObject();
            JSONArray ja_weborder01 = new JSONArray();
            JSONArray ja_weborder011 = new JSONArray();

            for (int i = 0; i < Bill.Now.AL_Order.size(); i++) {
                Product product = Bill.Now.AL_Order.get(i).product;
                //construct the weborder01
                product.worder_sno = ja_weborder01.length() + 1;
                JSONObject jo_data_weborder01 = Weborder01.setJObyProduct(product, product.iscomb, null);
                ja_weborder01.put(jo_data_weborder01);
                if (product.iscomb == 1) {
                    for (int j = 0; j < product.al_combItem.size(); j++) {
                        Product subProduct = product.al_combItem.get(j).productSelected;
                        //套餐子項數量  跟主項一樣
                        subProduct.count = product.count;

                        subProduct.worder_sno = ja_weborder01.length() + 1;
                        JSONObject jo_data_ = Weborder01.setJObyProduct(subProduct, 2, String.valueOf(product.worder_sno));
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
                        for (Detail detail : taste.al_detail) {
                            if (detail.selected) {
                                JSONObject jo_data_weborder011 = new JSONObject();
                                jo_data_weborder011.put("worder_sno", product_.worder_sno);
                                jo_data_weborder011.put("taste_id", taste.taste_id + "");
                                jo_data_weborder011.put("taste_sno", detail.taste_sno + "");
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

            JSONObject jo_data_Empty = new JSONObject();
            jo_weborder021.put("data", jo_data_Empty);
            Bill.Now.addTicketWebOrder021(Bill.Now.webOrder021Array);

            AddOrderApiRequest addOrderApiRequest = new AddOrderApiRequest(orderId, "0", "0",
                    Bill.getTime_forSubmit(), jo_weborder01.toString(), jo_weborder011.toString(),
                    new JSONObject().put("data", Bill.Now.ja_data_wo02).toString(),
                    new JSONObject().put("data", Bill.Now.webOrder021Array).toString(), Bill.Now.getjo_ticket().toString());
            addOrder(addOrderApiRequest);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private void addOrder(AddOrderApiRequest addOrderApiRequest) {
        d_loading.show();
        addOrderApiRequest.setApiListener(addOrderListener()).setRetry(3).go();
    }

    private ApiRequest.ApiListener addOrderListener() {
        return new ApiRequest.ApiListener() {
            @Override
            public void onSuccess(final ApiRequest apiRequest, final String body) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        d_loading.dismiss();
                        OrderResponse orderResponse = Json.fromJson(body, OrderResponse.class);
                        if (orderResponse.getCode() == 0) {
                            //新增orderId，讓Firebase Log可以查詢。此ID會以最後設定的覆蓋
//                            Crashlytics.setUserIdentifier(orderResponse.getWorder_id());
                            bill_update(orderResponse);
                            tv_msg.setVisibility(View.INVISIBLE);
                            gifBtnConfirmOrder.setVisibility(View.VISIBLE);
                        } else {
                            String msg = orderResponse.getMessage();
                            String errorShow = getString(R.string.errorOccur) + msg;
                            tv_total.setText(msg);
                            tv_msg.setText(errorShow);
                        }
                    }
                });
            }

            @Override
            public void onFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        d_loading.dismiss();
                        tv_total.setText(getString(R.string.connectionError));
                        final AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
                        alertDialogFragment.setTitle(R.string.connectionError)
                                .setMessage(R.string.tryLater)
                                .setConfirmButton(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!ClickUtil.isFastDoubleClick()) {
                                            alertDialogFragment.dismiss();
                                            ordersFromServer_Get(Bill.fromServer.worder_id);
                                        }
                                    }
                                })
                                .setCancelButton(R.string.returnHomeButton, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!ClickUtil.isFastDoubleClick()) {
                                            new BackHomeDialog(CheckOutOptionActivity.this).show();
                                        }
                                    }
                                })
                                .setUnCancelAble()
                                .show(getFragmentManager(), AlertDialogFragment.MESSAGE_DIALOG);
                    }
                });
            }
        };
    }

    void bill_update(OrderResponse orderResponse) {
        try {
            Bill.fromServer = new Bill();
            Bill.fromServer.worder_id = orderResponse.getWorder_id();
            Bill.fromServer.total = orderResponse.getTotal();
            Bill.fromServer.setOrderResponse(orderResponse);
            Bill.fromServer.AL_Order = Bill.Now.AL_Order;
            updateUI();
            mRobotAPI.startTTS(getResources().getString(R.string.robot_sentence_bill_total,
                    String.valueOf(Bill.fromServer.total)));
            mRobotAPI.motionPlay(getResources().getString(R.string.robot_bill_motion), false);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    void updateUI() {
        ll_order.removeAllViews();

        //webOrder01
        for (final Order order : Bill.fromServer.AL_Order) {
            order.updateUI_checkOutOptions(this);
            order.lsn_cco = new Order.LSN_CCO() {
                @Override
                public void remove() {
                    //有折價券不可清空購物車
                    if (Bill.Now.AL_Order.size() == 1) {
                        if (hasTicket(Ticket.DISCOUNT_TYPE)) {
                            Kiosk.showMessageDialog(CheckOutOptionActivity.this, getString(R.string.removeCouponFirst));
                            return;
                        }
                    }

                    Order order_Shop = Bill.Now.getOrderBy_worder_sno(order.product.worder_sno);
                    Bill.Now.order_remove(order_Shop);
                    if (Bill.Now.AL_Order.size() > 0 || hasTicket(Ticket.PRODUCT_TYPE)) {
                        ordersFromServer_Get(Bill.fromServer.worder_id);
                    } else {
                        ll_order.removeView(order.v_checkOutOptions);
                        rlDiscount.setVisibility(View.GONE);
                        rlOriginPrice.setVisibility(View.GONE);

                        String pricePrefix = getString(R.string.pricePrefix);
                        tv_total.setText(pricePrefix + "0");
                        enableConfirmButton(false);

                        //檢查有無使用兌換券
                        if (hasTicket(Ticket.PRODUCT_TYPE)) {
                            enableConfirmButton(true);
                        }
                    }
                }
            };

            ll_order.addView(order.v_checkOutOptions);
        }

        //票券
        for (Ticket ticket : Bill.Now.al_ticketSelected) {
            ticket.updateUI_COO(this);
            ll_order.addView(ticket.v_COO);
            if (ticket.KindType.equals("6")) {
                enableConfirmButton(true);
            }
        }

        String pricePrefix = getString(R.string.pricePrefix);
        String total = pricePrefix + Bill.fromServer.total;
        tv_total.setText(total);

        OrderResponse orderResponse = Bill.fromServer.getOrderResponse();

        //折價紅框
        if (orderResponse.getDiscount() != 0) {
            rlDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText(pricePrefix + orderResponse.getDiscount());
            rlOriginPrice.setVisibility(View.VISIBLE);
            int originPrice = Math.abs(orderResponse.getDiscount()) + orderResponse.getTot_sales();
            tvOriginPrice.setText(pricePrefix + originPrice);
            tvOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            rlDiscount.setVisibility(View.GONE);
            rlOriginPrice.setVisibility(View.GONE);
        }

        //票券紅框
        if (orderResponse.getWeborder02() != null && orderResponse.getWeborder02().size() > 0) {
            String ticketDiscount = Bill.Now.getTicketDiscount();
            ticketDiscount = "-" + FormatUtil.removeDot(ticketDiscount);
            rlTicketDiscount.setVisibility(View.VISIBLE);
            tvTicketDiscount.setText(pricePrefix + ticketDiscount);
        } else {
            rlTicketDiscount.setVisibility(View.GONE);
        }
    }

    private boolean hasTicket(String type) {
        for (Ticket ticket : Bill.Now.al_ticketSelected) {
            if (ticket.KindType.equals(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onUserInteraction() {
        Kiosk.idleCount = Config.idleCount;
    }

    public void onDestroy() {
        HomeActivity.now.activities.remove(this);
        super.onDestroy();
    }
}
