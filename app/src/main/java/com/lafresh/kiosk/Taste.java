package com.lafresh.kiosk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Taste {
    public Product product;
    public int tasteId;
    public String tasteName;
    public int mutex;
    public JSONObject jsonObject;
    public View view;
    public TextView tvTasteName;
    public GridLayout glDetail;
    public ArrayList<Detail> details = new ArrayList<>();

    public Taste(Product product) {
        this.product = product;
    }

    public void set_byjo(JSONObject jo) {
        this.jsonObject = jo;
        try {
            tasteId = jo.getInt("taste_id");
            tasteName = jo.getString("taste_name");
            mutex = jo.getInt("mutex");
            JSONArray details = jo.getJSONArray("detail");
            this.details.clear();
            for (int i = 0; i < details.length(); i++) {
                Detail detail = new Detail(this);
                detail.setTasteData(details.getJSONObject(i));
                this.details.add(detail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUI(Context ct) {
        try {
            LayoutInflater ift_01 = LayoutInflater.from(ct);
            view = ift_01.inflate(R.layout.row_taste, null);
            tvTasteName = view.findViewById(R.id.tv_taste_name);
            glDetail = view.findViewById(R.id.gl_detail);
            tvTasteName.setText(tasteName);
            glDetail.removeAllViews();
            for (Detail detail : details) {
                detail.updateUI(ct);
                glDetail.addView(detail.view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
