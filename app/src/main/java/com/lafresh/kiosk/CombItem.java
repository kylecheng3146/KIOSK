package com.lafresh.kiosk;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.lafresh.kiosk.dialog.ProductSelectDialog;
import com.lafresh.kiosk.dialog.TasteDialog;
import com.lafresh.kiosk.httprequest.model.GetCombRes;
import com.lafresh.kiosk.utils.Json;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CombItem {
    public Context context;
    public ArrayList<Product> products = new ArrayList<>();
    public View view;
    public View rowTop;
    public TextView tvName, tvTaste;
    public TextView tvTasteDetailsTop;
    public TextView tvComboName;
    public Button btnChange, btnTaste;
    public Product productSelected;
    public String subject;
    public double comboPrice;

    public interface OnChangeListener {
        void onChange(String productId);
    }

    public OnChangeListener onChangeListener;

    public void init(GetCombRes.CombBean combBean) {
        products.clear();
        try {
            Product defaultProduct = new Product();
            defaultProduct.comb_sno = combBean.getComb_sno();
            String jsonString = Json.toJson(combBean);
            JSONObject jsonObject = new JSONObject(jsonString);
            defaultProduct.setData(jsonObject);
            defaultProduct.comb_type = 2;
            comboPrice = Double.parseDouble(combBean.getPrice());
            defaultProduct.getProductVO().setStop_sell(combBean.isStopSale());
            subject = combBean.getSubject();

            products.add(defaultProduct);

            List<GetCombRes.CombBean.DetailBean> detailList = combBean.getDetail();
            for (GetCombRes.CombBean.DetailBean detailBean : detailList) {
                if (detailBean.isHidden()) {
                    continue;
                }
                Product product = new Product();
                product.initCombDetail(detailBean);
                products.add(product);
            }

            productSelected = defaultProduct;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUI(Context ct, boolean hasTaste) {
        this.context = ct;
        if (view == null) {
            LayoutInflater ift_01 = LayoutInflater.from(ct);
            view = ift_01.inflate(R.layout.row_combitem, null);
        }

        tvName = view.findViewById(R.id.tv_name);
        tvTaste = view.findViewById(R.id.tv_taste);
        btnChange = view.findViewById(R.id.btn_change);
        btnTaste = view.findViewById(R.id.btn_taste);
        if(!hasTaste){
            btnTaste.setVisibility(View.GONE);
        }else{
            btnTaste.setVisibility(View.VISIBLE);
        }
        tvTaste.setText(productSelected.detailList_get());
        tvName.setText(productSelected.prod_name1);

        setActions(ct);

        if (rowTop == null) {
            LayoutInflater ift_01 = LayoutInflater.from(ct);
            rowTop = ift_01.inflate(R.layout.row_combitem_tastedetail, null);
        }
        tvComboName = rowTop.findViewById(R.id.tv_combItemName);
        tvTasteDetailsTop = rowTop.findViewById(R.id.tv_details);
        tvComboName.setText(subject);
        tvTasteDetailsTop.setText("-" + productSelected.detailList_get());
    }

    public void setActions(final Context ct) {
        btnChange.setOnClickListener(view -> {
            ProductSelectDialog d_productSelect = new ProductSelectDialog(ct, CombItem.this, comboPrice);
            d_productSelect.show();
        });
        btnTaste.setOnClickListener(view -> showTasteDialog());
    }

    public void showTasteDialog() {
        TasteDialog tasteDialog = new TasteDialog(context, productSelected);
        tasteDialog.show();
        tasteDialog.clickOkListener = () -> {
            tvTaste.setText(productSelected.detailList_get());
            tvTasteDetailsTop.setText("-" + productSelected.detailList_get());
            try {
                onChangeListener.onChange(productSelected.prod_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
