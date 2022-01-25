package com.lafresh.kiosk;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lafresh.kiosk.dialog.AlertDialogFragment;
import com.lafresh.kiosk.httprequest.ApiRequest;
import com.lafresh.kiosk.httprequest.GetProdCateApiRequest;
import com.lafresh.kiosk.httprequest.model.GetProdCateRes;
import com.lafresh.kiosk.shoppingcart.model.ProductCate;
import com.lafresh.kiosk.utils.ClickUtil;
import com.lafresh.kiosk.utils.ComputationUtils;
import com.lafresh.kiosk.utils.Json;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageButton;

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_restart, btn_unfold, btn_fold;
    GifImageButton gifBtnCheckout;
    TextView tv_prodtitle;
    TextView tvPriceTitle;
    TextView TV_Price;
    LinearLayout ll_order, LL_ProdCate, ll_orderMore;
    HorizontalScrollView hsv_prodcate;
    GridLayout gl_product;
    ProgressBar pgb;
    RelativeLayout rl_unfold;

    public static ArrayList<Dialog> AL_DialogToClose;
    static public ShopActivity now;
    ArrayList<ProdCate> al_prodcate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_shop);
        HomeActivity.now.activities.add(this);

        AL_DialogToClose = new ArrayList<>();
        now = this;

        setUI();
        setActions();

        getProdCate();
        HomeActivity.now.stopIdleProof();
    }

    void setUI() {
        gifBtnCheckout = findViewById(R.id.gifBtnCheckout);
        hsv_prodcate = findViewById(R.id.hsv_prodcate);
        hsv_prodcate.bringToFront();
        LL_ProdCate = findViewById(R.id.LL_ProdCate);
        ll_order = findViewById(R.id.LL_Order);
        tvPriceTitle = findViewById(R.id.TV_Price_);
        String selectedOrderTitle = getString(R.string.selectedOrders);
        String totalTitle = getString(R.string.totalAmount);
        String pricePrefix = getString(R.string.pricePrefix);
        tvPriceTitle.setText(selectedOrderTitle + " / " + totalTitle + "  " + pricePrefix + " ");
        TV_Price = findViewById(R.id.TV_Price);
        tv_prodtitle = findViewById(R.id.tv_prodtitle);
        gl_product = findViewById(R.id.gl_product);
        pgb = findViewById(R.id.pgd_product);
        TV_Price.setText(Bill.Now.PriceBeforeDiscount_Get() + "");
        gl_product.removeAllViews();
        pgb.setVisibility(View.INVISIBLE);
        btn_restart = findViewById(R.id.btn_restart);

        btn_unfold = findViewById(R.id.btn_unfold);
        rl_unfold = findViewById(R.id.rl_unfold);
//        rl_unfold.setVisibility(View.INVISIBLE);
        btn_fold = findViewById(R.id.btn_fold);
        ll_orderMore = findViewById(R.id.ll_orderMore);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        Kiosk.checkAndChangeUi(this, Config.titleLogoPath, ivLogo);
    }

    void setActions() {
        gifBtnCheckout.setOnClickListener(this);
        btn_unfold.setOnClickListener(this);
        btn_fold.setOnClickListener(this);
        enableCheckoutButton(false);
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.gifBtnCheckout:
                Intent intent = new Intent(this, CheckOutOptionActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_restart:
                new BackHomeDialog(ShopActivity.this).show();
                break;
            case R.id.btn_unfold:
                btn_unfold.setVisibility(View.GONE);
                rl_unfold.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_fold:
                btn_unfold.setVisibility(View.VISIBLE);
                rl_unfold.setVisibility(View.GONE);
                break;
            case R.id.btn_arrow_r:
                hsv_prodcate.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hsv_prodcate.smoothScrollBy(131, 0);
                    }
                }, 100);
                break;
            default:
                break;
        }
    }

    void updateUI_prodCate() {
        LL_ProdCate.removeAllViews();
        for (ProdCate prodCate : al_prodcate) {
            prodCate.updateUI_Top(this);
            prodCate.set_actions(this);
            LL_ProdCate.addView(prodCate.v_top);
        }

        ProdCate.selected.btn_prodcate.performClick();
        hsv_prodcate.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < al_prodcate.size(); i++) {
                    if (al_prodcate.get(i) == ProdCate.selected) {
                        int dx = 131 * (i + 1) - (1080 - 39);
                        if (dx > 0) {
                            hsv_prodcate.smoothScrollBy(dx, 0);
                        }
                    }
                }
            }
        }, 100);
    }

    void updateUI() {
        String totalPrice = Bill.Now.PriceBeforeDiscount_Get();
        boolean isOverLimit = ComputationUtils.parseInt(totalPrice) > Config.orderPriceLimit;
        TV_Price.setText(totalPrice);
        enableCheckoutButton(Bill.Now.AL_Order.size() > 0 && !isOverLimit);
        if (isOverLimit) {
            Kiosk.showMessageDialog(ShopActivity.this, getString(R.string.overPriceLimit));
        }
    }

    private void enableCheckoutButton(boolean b) {
        if (b) {
            gifBtnCheckout.setVisibility(View.VISIBLE);
            gifBtnCheckout.setBackgroundResource(R.drawable.button_checkout);

        } else {
            gifBtnCheckout.setVisibility(View.GONE);
        }
        gifBtnCheckout.setEnabled(b);
    }

    public void Finish() {
        for (Dialog dialog : ShopActivity.AL_DialogToClose) {
            dialog.dismiss();
        }
        ShopActivity.AL_DialogToClose.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        btn_unfold.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Kiosk.hidePopupBars(this);
//        btn_unfold.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        ProdCate.selected = null;
        HomeActivity.now.activities.remove(this);
        AL_DialogToClose = null;
        now = null;
        super.onDestroy();
    }

    @Override
    public void onUserInteraction() {
        Kiosk.idleCount = Config.idleCount;
    }

    private void getProdCate() {
        pgb.setVisibility(View.VISIBLE);
        GetProdCateApiRequest getProdCateApiRequest = new GetProdCateApiRequest();
        getProdCateApiRequest.setConnectTimeout(3);
        getProdCateApiRequest.setReadTimeout(3);
        ApiRequest.ApiListener listener = new ApiRequest.ApiListener() {
            @Override
            public void onSuccess(ApiRequest apiRequest, String body) {
                final GetProdCateRes res = Json.fromJson(body, GetProdCateRes.class);
                if (res.getCode() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pgb.setVisibility(View.GONE);
                            List<ProductCate> cateList = res.getData();
                            initProdCate(cateList);
                        }
                    });
                } else {
                    onFail();
                }
            }

            @Override
            public void onFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pgb.setVisibility(View.GONE);
                        Toast.makeText(ShopActivity.this, getString(R.string.connectionError), Toast.LENGTH_LONG).show();
                        btn_restart.setOnClickListener(ShopActivity.this);
                        HomeActivity.now.idleProof();
                        final AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
                        alertDialogFragment.setTitle(R.string.connectionError)
                                .setMessage(R.string.tryLater)
                                .setConfirmButton(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!ClickUtil.isFastDoubleClick()) {
                                            alertDialogFragment.dismiss();
                                            getProdCate();
                                        }
                                    }
                                })
                                .setCancelButton(R.string.returnHomeButton, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                })
                                .setUnCancelAble()
                                .show(getFragmentManager(), AlertDialogFragment.MESSAGE_DIALOG);
                    }
                });
            }
        };
        getProdCateApiRequest.setApiListener(listener).setRetry(3).go();
    }

    private void initProdCate(List<ProductCate> cateList) {
        for (ProductCate productCate : cateList) {
            ProdCate prodCate = new ProdCate();
            prodCate.init(productCate);
            al_prodcate.add(prodCate);
        }
        ProdCate.selected = al_prodcate.get(0);
        updateUI_prodCate();
    }

    public void enableProdCateButton(Boolean b) {
        for (ProdCate prodCate : al_prodcate) {
            prodCate.btn_prodcate.setEnabled(b);
        }
    }
}
