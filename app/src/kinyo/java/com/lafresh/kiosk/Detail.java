package com.lafresh.kiosk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

public class Detail {
    Taste taste;
    public int taste_sno;
    public String name;
    public int price;
    JSONObject jo;
    View v;
    Context ct;
    TextView tv_name;
    Detail me = this;

    public boolean selected;

    public Detail(Taste taste) {
        this.taste = taste;
        selected = false;
    }

    void set_byjo(JSONObject jo) {
        this.jo = jo;
        try {
            taste_sno = jo.getInt("taste_sno");
            name = jo.getString("name");
            price = jo.getInt("price");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateUI(Context ct) {
        this.ct = ct;
        LayoutInflater ift_01 = LayoutInflater.from(ct);
        v = ift_01.inflate(R.layout.v_detail, null);
        tv_name = v.findViewById(R.id.tv_name);

        tv_name.setText(name);
        if (price != 0) {
            tv_name.setText(name + "(+" + price + ")");
        }

        if (taste.mutex == 1) {
            setActionsMutex();
        } else {
            setActionsMultiple();
        }
    }

    void set_actions_mutex() {
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = !selected;
                for (Detail detail : taste.al_detail) {
                    //detail.selected=false;
                    detail.selected = detail == me ? selected : false;

                    //int color = detail.selected ? Color.YELLOW:Color.TRANSPARENT;
                    //detail.tv_name.setBackgroundColor(color);

                    int r_bg = detail.selected ?  R.drawable.check_pressed : R.drawable.check ;
                    detail.tv_name.setBackgroundResource(r_bg);

                }

                taste.product._tasteDialog.updateUI();
            }
        });
    }


    void set_actions_multiple() {
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = !selected;
                //int color = selected ? Color.YELLOW:Color.TRANSPARENT;
                //tv_name.setBackgroundColor(color);
                int r_bg = selected ? R.drawable.check_pressed:R.drawable.check;
                tv_name.setBackgroundResource(r_bg);
                taste.product._tasteDialog.updateUI();
            }
        });
    }
}
