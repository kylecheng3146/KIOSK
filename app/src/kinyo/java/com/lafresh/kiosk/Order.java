package com.lafresh.kiosk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lafresh.kiosk.manager.OrderManager;
import com.lafresh.kiosk.utils.FormatUtil;
import com.lafresh.kiosk.utils.OnRefreshListener;


public class Order {
    String name;
    String pricePrefix;

    public Product product;
    public int count;
    public boolean isUsePoint = false;
    public double totalPrice = 0;

    View v_bottom;
    View v_unfold;
    View v_checkOutOptions;

    Button btn_remove;
    Order me = this;
    private OnRefreshListener onRefreshListener;
    interface LSN_CCO {
        void remove();
    }

    LSN_CCO lsn_cco;


    public Order(Product product) {
        this.product = product;
        count = product.count;
        name = product.prod_name1;
        isUsePoint = product.getProductVO().getUsePoint();
    }


    public void updateUI(Context ct) {
        pricePrefix = ct.getResources().getString(R.string.pricePrefix);
        LayoutInflater ift_01 = LayoutInflater.from(ct);
        View V = ift_01.inflate(R.layout.row_order, null);
        TextView TV_Content = V.findViewById(R.id.TV_Content);
        TextView TV_Name = V.findViewById(R.id.tv_name);
        TextView TV_UnitPrice = V.findViewById(R.id.TV_UnitPrice);
        TextView TV_Count = V.findViewById(R.id.TV_Count);
        TextView TV_OrderPrice = V.findViewById(R.id.TV_OrderPrice);
        TextView tvPoint = V.findViewById(R.id.tvPoint);
        btn_remove = V.findViewById(R.id.btn_remove);

        TV_Name.setText(name + "X" + count);

        TV_Content.setText(product.comb_type == 1 ? product.combDetail_Get() : product.detailList_get());
        //unitPrice=product.price_get();
        TV_UnitPrice.setText(product.price_get() + "");
        TV_Count.setText(count + "");
        TV_OrderPrice.setText(pricePrefix + FormatUtil.removeDot(product.total_get()));

        TV_Count.setVisibility(View.INVISIBLE);
        TV_UnitPrice.setVisibility(View.INVISIBLE);
        v_bottom = V;

        final int appliedPoint = Integer.parseInt(product.getProductVO().getRedeem_point()) * count;
        //判斷已經使用點數兌換
        if(isUsePoint){
            tvPoint.setVisibility(View.VISIBLE);
            tvPoint.setText(String.format(ct.getString(R.string.usedPoint), String.valueOf(appliedPoint)));
            TV_OrderPrice.setText(pricePrefix+"0");
        }


        btn_remove.setOnClickListener(view -> {
            Bill.Now.order_remove(me);
            OrderManager.getInstance().removeItem(me);
            totalPrice -= me.totalPrice;
            refreshPointStatus((OnRefreshListener) ct,appliedPoint);
        });

        totalPrice += product.total_get();
        updateUI_unfold(ct);
    }

    private void refreshPointStatus(OnRefreshListener ct ,int appliedPoint) {

        if(appliedPoint == 0){
            return;
        }

        if(OrderManager.getInstance().isLogin() && product.getProductVO().getUsePoint()){
            OrderManager.getInstance().getMember().setAppliedPoints(OrderManager.getInstance().getMember().getAppliedPoints() - appliedPoint);
            OrderManager.getInstance().getMember().setPoints(String.valueOf(Integer.parseInt(OrderManager.getInstance().getMember().getPoints()) + appliedPoint));
            onRefreshListener = ct;
            onRefreshListener.onRefresh();
        }
    }

    private void updateUI_unfold(Context ct) {
        LayoutInflater ift_01 = LayoutInflater.from(ct);
        View V = ift_01.inflate(R.layout.row_order, null);
        TextView TV_Content = V.findViewById(R.id.TV_Content);
        TextView TV_Name = V.findViewById(R.id.tv_name);
        TextView TV_UnitPrice = V.findViewById(R.id.TV_UnitPrice);
        TextView TV_Count = V.findViewById(R.id.TV_Count);
        TextView TV_OrderPrice = V.findViewById(R.id.TV_OrderPrice);
        TextView tvPoint = V.findViewById(R.id.tvPoint);
        btn_remove = V.findViewById(R.id.btn_remove);

        TV_Name.setText(name + "X" + count);

        TV_Content.setText(product.comb_type == 1 ? product.combDetail_Get() : product.detailList_get());
        //unitPrice=product.price_get();
        TV_UnitPrice.setText(product.price_get() + "");
        TV_Count.setText(count + "");
        TV_OrderPrice.setText(pricePrefix + FormatUtil.removeDot(product.total_get()));

        TV_Count.setVisibility(View.INVISIBLE);
        TV_UnitPrice.setVisibility(View.INVISIBLE);

        final int appliedPoint = Integer.parseInt(product.getProductVO().getRedeem_point()) * count;
        //判斷已經使用點數兌換
        if(isUsePoint){
            tvPoint.setVisibility(View.VISIBLE);
            tvPoint.setText(String.format(ct.getString(R.string.usedPoint), String.valueOf(appliedPoint)));
            TV_OrderPrice.setText(pricePrefix+"0");
        }

        btn_remove.setOnClickListener(view -> {
            Bill.Now.order_remove(me);
            OrderManager.getInstance().removeItem(me);
            totalPrice -= me.totalPrice;
            refreshPointStatus((OnRefreshListener) ct ,appliedPoint);
        });

        v_unfold = V;
    }

    public void updateUI_checkOutOptions(Context ct) {
        LayoutInflater ift_01 = LayoutInflater.from(ct);
        View V = ift_01.inflate(R.layout.row_order, null);
        TextView TV_Content = V.findViewById(R.id.TV_Content);
        TextView TV_Name = V.findViewById(R.id.tv_name);
        TextView TV_UnitPrice = V.findViewById(R.id.TV_UnitPrice);
        TextView TV_Count = V.findViewById(R.id.TV_Count);
        TextView TV_OrderPrice = V.findViewById(R.id.TV_OrderPrice);
        TextView tvPoint = V.findViewById(R.id.tvPoint);
        btn_remove = V.findViewById(R.id.btn_remove);


        pricePrefix = ct.getString(R.string.pricePrefix);
        String prefix = product.isExchange ? ct.getResources().getString(R.string.exchangePrefix) : "";
        TV_Name.setText(prefix + name + "X" + count);

        TV_Content.setText(product.comb_type == 1 ? product.combDetail_Get() : product.detailList_get());
        //unitPrice=product.price_get();
        TV_UnitPrice.setText(product.price_get() + "");
        TV_Count.setText(count + "");
        TV_OrderPrice.setText(pricePrefix + FormatUtil.removeDot(totalPrice));

        //判斷已經使用點數兌換
        if(isUsePoint){
            tvPoint.setVisibility(View.VISIBLE);
            tvPoint.setText(String.format(ct.getString(R.string.usedPoint), String.valueOf(Integer.parseInt(product.getProductVO().getRedeem_point())*count)));
            TV_OrderPrice.setText(pricePrefix+"0");
        }

        v_checkOutOptions = V;

        setActions_COO();
    }

    void setActions_COO() {
        v_checkOutOptions.findViewById(R.id.btn_remove).setOnClickListener(view -> {
            try {
                lsn_cco.remove();
                if(OrderManager.getInstance().isLogin() && isUsePoint){
                    int point = Integer.parseInt(OrderManager.getInstance().getMember().getPoints());
                    int appliedPoints = OrderManager.getInstance().getMember().getAppliedPoints();
                    OrderManager.getInstance()  .getMember().setPoints(String.valueOf(point + Integer.parseInt(product.getProductVO().getRedeem_point())*count));
                    OrderManager.getInstance()  .getMember().setAppliedPoints(appliedPoints - (Integer.parseInt(product.getProductVO().getRedeem_point())*count));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
