package com.lafresh.kiosk;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.lafresh.kiosk.manager.OrderManager;
import com.lafresh.kiosk.type.FlavorType;
import com.lafresh.kiosk.utils.FormatUtil;
import com.lafresh.kiosk.utils.OnRefreshListener;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Order {
    public String name;
    public String pricePrefix;
    public String priceMoneyUnit;

    public Product product;
    public int count;
    public boolean isUsePoint;
    public boolean isUseCoupon = false;
    public double totalPrice = 0;

    public View v_bottom;
    public View v_unfold;
    public View v_checkOutOptions;

    public Button btn_remove;
    public Order me = this;

    public interface OnRemoveListener {
        void OnRemove();
    }

    public OnRemoveListener onRemoveListener;
    public Order(Product product) {
        this.product = product;
        count = product.count;
        name = product.prod_name1;
        isUsePoint = product.getProductVO().getUsePoint();
    }

    public void updateUI(Context ct) {
        pricePrefix = ct.getResources().getString(R.string.pricePrefix);
        priceMoneyUnit = ct.getResources().getString(R.string.priceMoneyUnit);
        LayoutInflater ift_01 = LayoutInflater.from(ct);
        View view = ift_01.inflate(R.layout.row_order, null);
        TextView tvContent = view.findViewById(R.id.TV_Content);
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvUnitPrice = view.findViewById(R.id.TV_UnitPrice);
        TextView tvCount = view.findViewById(R.id.TV_Count);
        TextView tvOrderPrice = view.findViewById(R.id.TV_OrderPrice);
        TextView tvPoint = view.findViewById(R.id.tvPoint);
        btn_remove = view.findViewById(R.id.btn_remove);

        tvName.setText(name + "X" + count);

        if(BuildConfig.FLAVOR.equals(FlavorType.hongya.name())){
            tvContent.setText(product.combDetail_Get() + product.detailList_get());
        }else{
            tvContent.setText(product.comb_type == 1 ? product.combDetail_Get() : product.detailList_get());
        }
        //unitPrice=product.price_get();
        tvUnitPrice.setText(product.getSpecialPrice() + "");
        tvCount.setText(count + "");
        tvOrderPrice.setText(pricePrefix + FormatUtil.removeDot(product.getTotalSpecialPrice()));

        tvCount.setVisibility(View.INVISIBLE);
        tvUnitPrice.setVisibility(View.INVISIBLE);
        v_bottom = view;

        final int appliedPoint = Integer.parseInt(product.getProductVO().getRedeem_point()) * count;
        checkUsedPoint(ct, tvOrderPrice, tvPoint, appliedPoint);

        btn_remove.setOnClickListener(v -> {
            Bill.Now.order_remove(me);
            OrderManager.getInstance().removeItem(me);
            totalPrice -= me.totalPrice;
            if (!BuildConfig.FLAVOR.equals(FlavorType.liangpin.name())){
                refreshPointStatus((OnRefreshListener) ct,appliedPoint);
            }
        });

        totalPrice += product.getTotalSpecialPrice();
        updateUnfoldUI(ct);
    }

    /**
     * 判斷已經使用點數兌換
     * */
    public void checkUsedPoint(Context context, TextView tvOrderPrice, TextView tvPoint, int appliedPoint) {
        if (isUsePoint) {
            tvPoint.setVisibility(View.VISIBLE);
            tvPoint.setText(String.format(context.getString(R.string.usedPoint), String.valueOf(appliedPoint)));
            tvOrderPrice.setText(pricePrefix + "0");
        }
    }

    private void refreshPointStatus(OnRefreshListener ct ,int appliedPoint) {
        if(appliedPoint == 0){
            return;
        }
        if(OrderManager.getInstance().isLogin() && product.getProductVO().getUsePoint()){
            OrderManager.getInstance().getMember().setAppliedPoints(OrderManager.getInstance().getMember().getAppliedPoints() - appliedPoint);
            OrderManager.getInstance().getMember().setPoints(String.valueOf(Integer.parseInt(OrderManager.getInstance().getMember().getPoints()) + appliedPoint));
            ct.onRefresh();
        }
    }

    private void updateUnfoldUI(Context ct) {
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
        if(BuildConfig.FLAVOR.equals(FlavorType.hongya.name())){
            TV_Content.setText(product.combDetail_Get() + product.detailList_get());
        }else{
            TV_Content.setText(product.comb_type == 1 ? product.combDetail_Get() : product.detailList_get());
        }
        //unitPrice=product.price_get();
        TV_UnitPrice.setText(product.getSpecialPrice() + "");
        TV_Count.setText(count + "");
        TV_OrderPrice.setText(pricePrefix + FormatUtil.removeDot(product.getTotalSpecialPrice()));
        TV_Count.setVisibility(View.INVISIBLE);
        TV_UnitPrice.setVisibility(View.INVISIBLE);
        final int appliedPoint = Integer.parseInt(product.getProductVO().getRedeem_point()) * count;
        //判斷已經使用點數兌換
        checkUsedPoint(ct, TV_OrderPrice, tvPoint, appliedPoint);
        btn_remove.setOnClickListener(view -> {
            Bill.Now.order_remove(me);
            OrderManager.getInstance().removeItem(me);
            totalPrice -= me.totalPrice;
            refreshPointStatus((OnRefreshListener) ct ,appliedPoint);
        });
        v_unfold = V;
    }

    public void updateCheckoutOptionsUI(Context ct) {
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
        priceMoneyUnit = ct.getString(R.string.priceMoneyUnit);
        String prefix = product.isExchange ? ct.getResources().getString(R.string.exchangePrefix) : "";
        TV_Name.setText(prefix + name + "X" + count);
        if(BuildConfig.FLAVOR.equals(FlavorType.hongya.name())){
            TV_Content.setText(product.combDetail_Get() + product.detailList_get());
        }else{
            TV_Content.setText(product.comb_type == 1 ? product.combDetail_Get() : product.detailList_get());
        }
        //unitPrice=product.price_get();
        if(BuildConfig.FLAVOR == FlavorType.lanxin.name()){
            TV_UnitPrice.setText("$" + FormatUtil.removeDot(product.getSpecialPrice()) + "");
            TV_Count.setText("x" + count + "");
            TV_OrderPrice.setText(priceMoneyUnit + FormatUtil.removeDot(totalPrice));
        }else{
            TV_UnitPrice.setText(product.getSpecialPrice() + "");
            TV_Count.setText(count + "");
            TV_OrderPrice.setText(pricePrefix + FormatUtil.removeDot(totalPrice));
        }

        //判斷已經使用點數兌換
        checkUsedPoint(ct, TV_OrderPrice, tvPoint, Integer.parseInt(product.getProductVO().getRedeem_point())*count);
        checkCouponUsed(tvPoint);
        v_checkOutOptions = V;
        setActions();
    }

    /**
     * 判斷是否使用票券
     * */
    public void checkCouponUsed(TextView tvPoint) {
        if(isUseCoupon){
            tvPoint.setVisibility(View.VISIBLE);
            tvPoint.setText("兌換");
        }
    }

    void setActions() {
        v_checkOutOptions.findViewById(R.id.btn_remove).setOnClickListener(view -> {
            try {
                onRemoveListener.OnRemove();
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
