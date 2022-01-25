package com.lafresh.kiosk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.lafresh.kiosk.Bill;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.Post;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.Ticket;
import com.lafresh.kiosk.activity.ShopActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.M)
public class TicketDialog extends Dialog {
    Context ct;

    GridLayout gl_ticket;
    Button btn_ok, btn_back;
    ProgressBar pgb;

    private ArrayList<Ticket> al_ticket = new ArrayList<>();

    public interface LSN {
        void onOK();
    }

    public LSN lsn;

    public TicketDialog(final Context ct) {
        super(ct, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Kiosk.hidePopupBars(this);
        setContentView(R.layout.d_ticket);
        this.ct = ct;

        ShopActivity.AL_DialogToClose.add(this);

        setUI();
        setActions();
        getTickets();
    }

    void setUI() {
        gl_ticket = findViewById(R.id.gl_ticket);
        btn_back = findViewById(R.id.btn_back);
        btn_ok = findViewById(R.id.btn_ok);
        pgb = findViewById(R.id.pgb);
        pgb.setVisibility(View.INVISIBLE);
        btn_ok.setVisibility(View.INVISIBLE);
    }


    void setActions() {
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bill.Now.al_ticketSelected.clear();
                for (Ticket ticket : al_ticket) {
                    if (ticket.isChecked) {
                        Bill.Now.al_ticketSelected.add(ticket);
                    }
                }
                try {
                    lsn.onOK();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    private void getTickets() {
        al_ticket.clear();
        gl_ticket.removeAllViews();
        Post post = new Post(ct, Config.ApiRoot + "/webservice/ticket/");
        post.addField("authkey", Config.authKey);
        post.addField("op", "get_ticket_list"); //ok
        post.addField("acckey", Config.acckey);
        post.addField("type", "");

        pgb.setVisibility(View.VISIBLE);
        post.SetPostListener(new Post.PostListener() {
            @Override
            public void OnFinish(String Response) {
                try {
                    JSONObject jo = new JSONObject(Response);
                    JSONArray ja = jo.getJSONArray("ticket");
                    for (int i = 0; i < ja.length(); i++) {
                        Ticket ticket = new Ticket();
                        ticket.setTicketData(ja.getJSONObject(i));
                        ticket.updateSelectUI(ct);

                        al_ticket.add(ticket);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                updateUI();
            }
        });
        post.GO();
    }

    void updateUI() {
        btn_ok.setVisibility(View.VISIBLE);
        pgb.setVisibility(View.INVISIBLE);
        for (Ticket ticket : al_ticket) {
            gl_ticket.addView(ticket.v_select);
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
        ShopActivity.AL_DialogToClose.remove(this);

    }
}
