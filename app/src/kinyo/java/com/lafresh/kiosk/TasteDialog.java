package com.lafresh.kiosk;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.lafresh.kiosk.dialog.KeyboardDialog;
import com.lafresh.kiosk.httprequest.ApiRequest;
import com.lafresh.kiosk.utils.ClickUtil;
import com.lafresh.kiosk.utils.FormatUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class TasteDialog extends Dialog {
    Context ct;
    Button btn_OK, btn_less, btn_more, btn_back;
    Button btn_1, btn_2, btn_3, btn_4, btn_5;
    TextView tv_title, tv_count, tv_total, tv_prodname, tv_unitPrice, tv_content;
    ProgressBar pgd_taste;
    ImageView ivKeyboard;
    LinearLayout ll_taste, ll_price;
    RelativeLayout rl_keyboard;

    Product product;

    interface ClickOkListener {
        void onOK();
    }

    ClickOkListener clickOkListener;

    public TasteDialog(final Context ct, Product product) {
        super(ct, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int r_contentView = product.comb_type == 2 ? R.layout.d_taste_prodincomb : R.layout.d_taste_single;
        setContentView(r_contentView);
        Kiosk.hidePopupBars(this);
        this.ct = ct;
        this.product = product;
        product._tasteDialog = this;

        setUI();
        set_actions();

        if (product.selectedDetails_Get().size() == 0 || product.comb_type == 0) {
            tastes_get();
        } else {
            updateUI();
        }
    }

    void setUI() {
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        tv_unitPrice = findViewById(R.id.tv_unitPrice);
        tv_total = findViewById(R.id.tv_total);
        tv_prodname = findViewById(R.id.tv_prodname);
        btn_OK = findViewById(R.id.btn_ok);
        btn_back = findViewById(R.id.btn_back);
        pgd_taste = findViewById(R.id.pgd_taste);
        ll_taste = findViewById(R.id.ll_taste);
        btn_less = findViewById(R.id.btn_less);
        btn_more = findViewById(R.id.btn_more);
        tv_count = findViewById(R.id.tv_count);
        ll_price = findViewById(R.id.ll_price);
        ivKeyboard = findViewById(R.id.iv_keyboard);
        ivKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardDialog keyboard = new KeyboardDialog(ct);
                keyboard.type = KeyboardDialog.DONATE_CODE;
                keyboard.listener = new KeyboardDialog.Listener() {
                    @Override
                    public void onEnter(int count, String text) {
                        tv_count.setText(text);
                        product.count = Integer.parseInt(text + "");
                        updateUI();
                    }
                };
                keyboard.show();
            }
        });

        tv_title.setText(ct.getResources().getString(R.string.chooseFavor));
        tv_count.setText(String.valueOf(product.count));

        if (tv_prodname != null) {
            tv_prodname.setText(product.prod_name1);
        }

        if (tv_unitPrice != null) {
            try {
                String pricePrefix = ct.getResources().getString(R.string.pricePrefix);
                String unitPrice = pricePrefix + product.get_priceStr();
                tv_unitPrice.setText(unitPrice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (ll_taste != null) {
            ll_taste.removeAllViews();
        }

        if (tv_content != null) {
            tv_content.setText(product.prod_content.equals("null") ? "" : Html.fromHtml(product.prod_content));
        }

        if (product.comb_type == 2) {
            ll_price.setVisibility(View.INVISIBLE);
        } else {
            btn_1 = findViewById(R.id.btn_1);
            btn_2 = findViewById(R.id.btn_2);
            btn_3 = findViewById(R.id.btn_3);
            btn_4 = findViewById(R.id.btn_4);
            btn_5 = findViewById(R.id.btn_5);
        }

        rl_keyboard = findViewById(R.id.rl_keyboard);

        ImageView imageView = findViewById(R.id.iv_comb);
        //活動改圖
        if (imageView != null && product.getProductVO().getImgfile1() != null) {
            String path = ApiRequest.PICTURE_URL + product.getProductVO().getImgfile1();
            Glide.with(ct).load(path).fitCenter().centerCrop().into(imageView);
        }
    }

    void set_actions() {
        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product.comb_type != 2) {
                    Order order = new Order(product);
                    order.updateUI(ct);
                    Bill.Now.order_add(order);
                } else {
                    try {
                        clickOkListener.onOK();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                dismiss();
            }
        });

        View.OnClickListener ocl_moreorless = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btn_less) {
                    if (product.count > 1) {
                        product.count--;
                    }
                }

                if (view.getId() == R.id.btn_more) {
                    product.count++;
                }

                updateUI();
            }
        };

        btn_more.setOnClickListener(ocl_moreorless);
        btn_less.setOnClickListener(ocl_moreorless);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if (product.comb_type != 2) {
            View.OnClickListener ocl_setNumber = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    product.count = Integer.parseInt(view.getTag() + "");
                    updateUI();
                }
            };
            btn_1.setOnClickListener(ocl_setNumber);
            btn_2.setOnClickListener(ocl_setNumber);
            btn_3.setOnClickListener(ocl_setNumber);
            btn_4.setOnClickListener(ocl_setNumber);
            btn_5.setOnClickListener(ocl_setNumber);
        }

        if (rl_keyboard != null) {
            rl_keyboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ClickUtil.isFastDoubleClick()) {
                        return;
                    }
                    int count = Integer.parseInt(tv_count.getText().toString());
                    KeyboardDialog keyboardDialog = new KeyboardDialog(ct);
                    keyboardDialog.show();
                    keyboardDialog.listener = new KeyboardDialog.Listener() {
                        @Override
                        public void onEnter(int count, String text) {
                            product.count = count;
                            updateUI();
                        }
                    };
                }
            });
        }
    }

    void tastes_get() {
        Post post = new Post(ct, ApiRequest.PRODUCT_SERVICE_URL);
        post.AddField("authkey", Config.authKey);
        post.AddField("op", "get_taste");
        post.AddField("prod_id", product.prod_id);

        pgd_taste.setVisibility(View.VISIBLE);
        post.SetPostListener(new Post.PostListener() {
            @Override
            public void OnFinish(final String Response) {
                pgd_taste.setVisibility(View.GONE);
                try {
                    final JSONObject jo = new JSONObject(Response);
                    product.al_taste.clear();
                    if (jo.getInt("code") == 0) {
                        try {
                            if (jo.has("taste")) {
                                JSONArray ja_taste = jo.getJSONArray("taste");
                                for (int i = 0; i < ja_taste.length(); i++) {
                                    Taste taste = new Taste(product);
                                    taste.set_byjo(ja_taste.getJSONObject(i));
                                    taste.updateUI(ct);
                                    product.al_taste.add(taste);
                                }

                                if (ja_taste.length() == 0) {
                                    tv_title.setText(ct.getResources().getString(R.string.noTasteOption));
                                }

                            } else {
                                tv_title.setText(ct.getResources().getString(R.string.noTasteOption));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        updateUI();

                    } else {
                        Toast.makeText(ct, jo.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        post.GO();
    }

    void updateUI() {
        try {
            for (Taste taste : product.al_taste) {
                if (taste.v.getParent() != null) {
                    ((ViewGroup) taste.v.getParent()).removeAllViews();
                }
                ll_taste.addView(taste.v);
            }

            String pricePrefix = ct.getResources().getString(R.string.pricePrefix);
            tv_count.setText(String.valueOf(product.count));
            tv_unitPrice.setText(pricePrefix + FormatUtil.removeDot(product.price_get()));
            tv_total.setText(FormatUtil.removeDot(product.total_get() + ""));

            pgd_taste.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Kiosk.idleCount = Config.idleCount;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }
}
