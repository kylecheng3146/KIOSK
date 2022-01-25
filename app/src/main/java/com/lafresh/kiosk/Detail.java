package com.lafresh.kiosk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lafresh.kiosk.type.FlavorType;

import org.json.JSONObject;

public class Detail {
    public Taste taste;
    public int tasteSno;
    public String name;
    public int price;
    public JSONObject jsonObject;
    public View view;
    public Context context;
    public TextView tvName;
    public Detail me = this;

    public boolean selected;

    public Detail(Taste taste) {
        this.taste = taste;
        selected = false;
    }

    public void setTasteData(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        try {
            tasteSno = jsonObject.getInt("taste_sno");
            name = jsonObject.getString("name");
            price = jsonObject.getInt("price");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUI(Context context) {
        this.context = context;
        LayoutInflater ift_01 = LayoutInflater.from(context);
        view = ift_01.inflate(R.layout.v_detail, null);
        tvName = view.findViewById(R.id.tv_name);

        tvName.setText(name);
        if (price != 0) {
            if(BuildConfig.FLAVOR == FlavorType.lanxin.name()){
                tvName.setText(name + "\n+$" + price);
            }else{
                tvName.setText(name + "(+" + price + ")");
            }
        }

        if (taste.mutex == 1) {
            setActionsMutex();
        } else {
            setActionsMultiple();
        }
    }

    public void setActionsMutex() {
        tvName.setOnClickListener(view -> {
            selected = !selected;
            for (Detail detail : taste.details) {
                detail.selected = detail == me && selected;
                int r_bg = detail.selected ? R.drawable.btn_selector_pressed : R.drawable.btn_selector;
                detail.tvName.setBackgroundResource(r_bg);

            }

            taste.product._tasteDialog.updateUI();
        });
    }


    public void setActionsMultiple() {
        tvName.setOnClickListener(view -> {
            selected = !selected;
            int r_bg = 0;
            r_bg = selected ? R.drawable.btn_selector_pressed : R.drawable.btn_selector;
            tvName.setBackgroundResource(r_bg);
            if(taste.product._tasteDialog != null){
                taste.product._tasteDialog.updateUI();
            }else{
                taste.product.d_combItemList.updateUI();
            }
        });
    }
}
