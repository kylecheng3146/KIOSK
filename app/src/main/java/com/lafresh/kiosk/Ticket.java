package com.lafresh.kiosk;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Ticket {
    public static final String DISCOUNT_TYPE = "1";
    public static final String PRODUCT_TYPE = "6";
    public JSONObject jsonObject;
    public String subject;
    public String tkno;
    public String ProdValue;
    public String KindType;
    public String ProdID;
    public boolean isChecked = false;
    public String points = "1";
    public View v_select, view;

    public void setTicketData(JSONObject jsonObject) {
        try {
            subject = jsonObject.getString("subject");
            tkno = jsonObject.getString("tkno");
            ProdValue = jsonObject.getString("ProdValue");
            KindType = jsonObject.getString("KindType");
            ProdID = jsonObject.getString("ProdID");
            points = jsonObject.getString("points");
            isChecked = getChecked();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getChecked() {
        boolean isChecked = false;
        for (Ticket ticket : Bill.Now.al_ticketSelected) {
            if (tkno.equals(ticket.tkno)) {
                isChecked = true;
            }
        }

        return isChecked;
    }

    public void updateSelectUI(final Context ct) {
        RelativeLayout rl_ticket;
        TextView tv_subject;
        final ImageView iv_check;

        v_select = LayoutInflater.from(ct).inflate(R.layout.v_ticket_select, null);
        rl_ticket = v_select.findViewById(R.id.rl_ticket);
        tv_subject = v_select.findViewById(R.id.tv_subject);
        iv_check = v_select.findViewById(R.id.iv_check);

        iv_check.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);

        tv_subject.setText(getSubject(ct));

        rl_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Bill.Now.AL_Order.size() == 0 && KindType.equals(DISCOUNT_TYPE)) {
                    Kiosk.showMessageDialog(ct, ct.getString(R.string.noProductNoCoupon));
                    return;
                }

                iv_check.setVisibility(iv_check.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                isChecked = (iv_check.getVisibility() == View.VISIBLE);
            }
        });
    }

    public void updateUI(Context ct) {
        view = LayoutInflater.from(ct).inflate(R.layout.v_ticket_coo, null);
        TextView tvSubject;
        TextView tvDiscount;

        tvSubject = view.findViewById(R.id.tv_subject);
        tvDiscount = view.findViewById(R.id.tv_discount);

        tvSubject.setText(getSubject(ct));
        tvDiscount.setVisibility(KindType.equals("1") ? View.VISIBLE : View.INVISIBLE);

        int discount = (int) Double.parseDouble(ProdValue);
        tvDiscount.setText("-" + discount);
    }

    public String getSubject(Context ct) {
        String type = "";
        if (KindType.equals("1")) {
            type = ct.getResources().getString(R.string.discountPrefix);
        }

        if (KindType.equals("6")) {
            type = ct.getResources().getString(R.string.exchangePrefix);
        }

        return type + subject;
    }

    public JSONObject getjo_weborder021() {
        JSONObject jo_ticket = new JSONObject();
        try {
            jo_ticket.put("pay_id", "Q004");
            jo_ticket.put("proof_no", tkno);
            jo_ticket.put("proof_no2", tkno);
            jo_ticket.put("proof_cnt", "1");
            jo_ticket.put("amount", ProdValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jo_ticket;
    }
}
