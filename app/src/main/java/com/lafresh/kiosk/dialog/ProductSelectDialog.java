package com.lafresh.kiosk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.lafresh.kiosk.CombItem;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.Product;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.activity.ShopActivity;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ProductSelectDialog extends Dialog {
    Context ct;
    Button btn_ok, btn_back;
    CombItem combItem;
    GridLayout gl_product;
    Product product_seleced;
    double comboPrice;


    public ProductSelectDialog(final Context ct, CombItem combItem, double comboPrice) {
        super(ct, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.d_productselect);
        this.ct = ct;
        this.combItem = combItem;
        this.comboPrice = comboPrice;
        product_seleced = combItem.productSelected;
        ShopActivity.AL_DialogToClose.add(this);
        setUI();
        setActions();
    }

    void setUI() {
        btn_ok = findViewById(R.id.btn_ok);
        btn_back = findViewById(R.id.btn_back);
        gl_product = findViewById(R.id.gl_product);
        gl_product.removeAllViews();

        for (final Product product : combItem.products) {
            product.updateUI_combItem(ct,this.comboPrice);

            gl_product.addView(product.v_combItemChange);
            if (product == combItem.productSelected) {
                int r_bg_select = R.drawable.btn_selector_pressed;
                Button btn_change = product.v_combItemChange.findViewById(R.id.btn_change);
                btn_change.setBackgroundResource(r_bg_select);
            }

            product.onChangeListener = () -> {
                for (Product product1 : combItem.products) {
                    product1.changeButtonBackground(false);
                }
                product.changeButtonBackground(true);
                product_seleced = product;
            };
        }
    }

    void setActions() {
        btn_ok.setOnClickListener(view -> {
            combItem.productSelected = product_seleced;
            combItem.updateUI(ct, true);
            combItem.onChangeListener.onChange(product_seleced.prod_id);
            dismiss();
        });
        btn_back.setOnClickListener(view -> dismiss());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Kiosk.idleCount = Config.idleCount;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
        ShopActivity.AL_DialogToClose.remove(this);
    }
}
