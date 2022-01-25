package com.lafresh.kiosk;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lafresh.kiosk.dialog.AlertDialogFragment;
import com.lafresh.kiosk.httprequest.ApiRequest;
import com.lafresh.kiosk.httprequest.GetProductApiRequest;
import com.lafresh.kiosk.shoppingcart.model.ProductCate;
import com.lafresh.kiosk.utils.ClickUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ProdCate {
    private String subject;
    private String serno;
    JSONObject jo;
    View v_top;
    private Context ct_vtop;
    Button btn_prodcate;
    private ProdCate me = this;

    private ImageView iv_top_bg;
    private TextView tv_subject;
    static ProdCate selected;


    void init(ProductCate productCate) {
        subject = productCate.getSubject();
        serno = productCate.getSerno();
    }

    void updateUI_Top(Context ct) {
        this.ct_vtop = ct;
        LayoutInflater ift_01 = LayoutInflater.from(ct);
        v_top = ift_01.inflate(R.layout.v_prodcate_top, null);
        tv_subject = v_top.findViewById(R.id.tv_subject);
        btn_prodcate = v_top.findViewById(R.id.btn_prodcate);
        iv_top_bg = v_top.findViewById(R.id.iv_bg);
        tv_subject.setText(subject);
        iv_top_bg.setBackgroundResource(R.color.kinyo_product_cate_dark_bg);
        tv_subject.setTextColor(Color.WHITE);
    }

    void set_actions(final ShopActivity shopActivity) {
        btn_prodcate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopActivity.enableProdCateButton(false);
                if (ProdCate.selected != null) {
                    ProdCate.selected.iv_top_bg.setBackgroundResource(R.color.kinyo_product_cate_dark_bg);
                }

                ProdCate.selected = me;
                iv_top_bg.setBackgroundResource(R.color.kinyo_background);
                tv_subject.setTextColor(Color.BLACK);
                shopActivity.tv_prodtitle.setVisibility(View.INVISIBLE);
                shopActivity.gl_product.removeAllViews();
                getProduct(shopActivity);
            }
        });
    }

    private void getProduct(final ShopActivity shopActivity) {
        shopActivity.pgb.setVisibility(View.VISIBLE);
        ApiRequest.ApiListener listener = new ApiRequest.ApiListener() {
            @Override
            public void onSuccess(ApiRequest apiRequest, final String body) {
                shopActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HomeActivity.now.idleProof();
                        shopActivity.btn_restart.setOnClickListener(shopActivity);
                        shopActivity.pgb.setVisibility(View.GONE);
                        try {
                            final JSONObject jo = new JSONObject(body);
                            if (jo.getInt("code") == 0) {
                                if (jo.getString("datacnt").equals("0")) {
                                    shopActivity.tv_prodtitle.setVisibility(View.VISIBLE);
                                    shopActivity.tv_prodtitle.setText(shopActivity.getString(R.string.noProductInCate));
                                } else {
                                    Config.productImgPath = jo.getString("imgpath");
                                    JSONArray ja_product = jo.getJSONArray("product");
                                    for (int i = 0; i < ja_product.length(); i++) {
                                        Product product = new Product(me);
                                        product.set_byjo(ja_product.getJSONObject(i));
                                        product.updateUI(ct_vtop);
                                        shopActivity.gl_product.addView(product.v);
                                    }

                                    if (ja_product.length() == 0) {
                                        shopActivity.tv_prodtitle.setVisibility(View.VISIBLE);
                                        shopActivity.tv_prodtitle.setText(shopActivity.getString(R.string.noProductInCate));
                                    }
                                }
                            } else {
                                Toast.makeText(ct_vtop, jo.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(e);
                        } finally {
                            shopActivity.enableProdCateButton(true);
                        }
                    }
                });
            }

            @Override
            public void onFail() {
                shopActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shopActivity.pgb.setVisibility(View.GONE);
                        shopActivity.enableProdCateButton(true);
                        shopActivity.btn_restart.setOnClickListener(shopActivity);
                        HomeActivity.now.idleProof();
                        final AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
                        alertDialogFragment.setTitle(R.string.connectionError)
                                .setMessage(R.string.tryLater)
                                .setConfirmButton(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!ClickUtil.isFastDoubleClick()) {
                                            alertDialogFragment.dismiss();
                                            getProduct(shopActivity);
                                        }
                                    }
                                })
                                .setCancelButton(R.string.returnHomeButton, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        shopActivity.finish();
                                    }
                                })
                                .setUnCancelAble()
                                .show(shopActivity.getFragmentManager(), AlertDialogFragment.MESSAGE_DIALOG);
                    }
                });
            }
        };
        GetProductApiRequest getProductApiRequest = new GetProductApiRequest(serno);
        getProductApiRequest.setConnectTimeout(3);
        getProductApiRequest.setReadTimeout(3);
        getProductApiRequest.setApiListener(listener).setRetry(3).go();
    }
}
