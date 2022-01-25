package com.lafresh.kiosk;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.lafresh.kiosk.dialog.ProductComboDialog;
import com.lafresh.kiosk.dialog.TasteDialog;
import com.lafresh.kiosk.httprequest.model.GetCombRes;
import com.lafresh.kiosk.manager.OrderManager;
import com.lafresh.kiosk.type.FlavorType;
import com.lafresh.kiosk.utils.Arith;
import com.lafresh.kiosk.utils.ClickUtil;
import com.lafresh.kiosk.utils.CommonUtils;
import com.lafresh.kiosk.utils.ComputationUtils;
import com.lafresh.kiosk.utils.FormatUtil;
import com.lafresh.kiosk.utils.Json;

import org.json.JSONObject;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Product {
    //將商品資料以物件儲存
    private com.lafresh.kiosk.shoppingcart.model.Product productVO;
    public JSONObject jo;
    public boolean isExchange = false;
    public String prod_id;
    public String prod_name1;
    public String imgfile1;
    public ProductCategory prodCate;
    public double spe_price = 0;
    public double item_disc = 0;
    public TasteDialog _tasteDialog;
    public ProductComboDialog d_combItemList;
    public Context ct;
    public View v;
    public View v_combItemChange;
    public Button btn_purchase;
    public String prod_content = "";
    public String combQty = "1";
    public String comb_sno = "";
    public String comb_sale_sno = "";
    public double price1, price2;
    public int count = 1;
    public int iscomb = 0;
    public int comb_type = 0;
    public int worder_sno;
    public String ticketNo;
    public ArrayList<CombItem> al_combItem = new ArrayList<>();
    public ArrayList<Taste> al_taste = new ArrayList<>();
    private double price;
    private double combPrice;

    public interface OnChangeListener {
        public void onChange();
    }

    public OnChangeListener onChangeListener;


    public Product(ProductCategory prodCate) {
        this.prodCate = prodCate;
    }

    public Product() {
    }

    public com.lafresh.kiosk.shoppingcart.model.Product getProductVO() {
        return productVO;
    }

    public void setProductVO(com.lafresh.kiosk.shoppingcart.model.Product productVO) {
        this.productVO = productVO;
    }

    public void setData(JSONObject jo) {
        this.jo = jo;
        productVO = Json.fromJson(jo.toString(), com.lafresh.kiosk.shoppingcart.model.Product.class);
        try {
            prod_id = jo.getString("prod_id");
            prod_name1 = CommonUtils.INSTANCE.switchProductNameType(jo);
            productVO.setProd_name1(CommonUtils.INSTANCE.switchProductNameType(jo));
//            imgfile1 = jo.getString("imgfile1");
            price = jo.has("price") ? jo.getDouble("price") : 0;
//            price1 = jo.has("price1")?(int)Math.floor(Double.parseDouble(jo.getString("price1"))):0 ;
            //判斷如果為套餐重新設定價格
//            if(jo.getInt("iscomb") == 1){
//                price1 =  jo.getDouble("combPrice");
//            }
            //price1_Str = jo.has("price1")?jo.getString("price1"):"";
            price2 = jo.has("price2") ? Math.round(Double.parseDouble(jo.getString("price2"))) : 0;
            iscomb = jo.has("iscomb") ? jo.getInt("iscomb") : 0;
            prod_content = jo.has("prod_content") ? jo.getString("prod_content") : "";
            comb_type = iscomb;
            combQty = jo.has("quantity") ? jo.getString("quantity") : "1";
            //spe_price = jo.has("spe_price")?jo.getString("spe_price"):"0";
            if (productVO.getSpe_price() != null) {
                String specialPrice = productVO.getSpe_price();
                spe_price = ComputationUtils.parseDouble(specialPrice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set_byjo(JSONObject jo) {
        this.jo = jo;
        productVO = Json.fromJson(jo.toString(), com.lafresh.kiosk.shoppingcart.model.Product.class);
        try {
            prod_id = jo.getString("prod_id");
            prod_name1 = CommonUtils.INSTANCE.switchProductNameType(jo);
            productVO.setProd_name1(CommonUtils.INSTANCE.switchProductNameType(jo));
//            if(jo.getString("imgfile1") != null){
//                imgfile1 = jo.getString("imgfile1");
//            }
            //price1 = jo.has("price1")?(int)Math.floor(Double.parseDouble(jo.getString("price1"))):0 ;
            price1 = jo.has("price1") ? jo.getDouble("price1") : 0;
            //判斷如果為套餐重新設定價格
            if(jo.getInt("iscomb") == 1){
                combPrice =  jo.getDouble("combPrice");
            }
            //price1_Str = jo.has("price1")?jo.getString("price1"):"";
            price2 = jo.has("price2") ? Math.round(Double.parseDouble(jo.getString("price2"))) : 0;
            iscomb = jo.has("iscomb") ? jo.getInt("iscomb") : 0;
            prod_content = jo.has("prod_content") ? jo.getString("prod_content") : "";
            comb_type = iscomb;
            combQty = jo.has("quantity") ? jo.getString("quantity") : "1";
            //spe_price = jo.has("spe_price")?jo.getString("spe_price"):"0";
            if (productVO.getSpe_price() != null) {
                String specialPrice = productVO.getSpe_price();
                spe_price = ComputationUtils.parseDouble(specialPrice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initCombDetail(GetCombRes.CombBean.DetailBean detailBean) {
        String jsonString = Json.toJson(detailBean);
        productVO = Json.fromJson(jsonString, com.lafresh.kiosk.shoppingcart.model.Product.class);
        productVO.setStop_sell(detailBean.isStopSale());

        prod_id = detailBean.getProd_id();
        comb_sno = detailBean.getComb_sno();
        prod_name1 = detailBean.getProd_name1();
        imgfile1 = detailBean.getImgfile1();
        combQty = detailBean.getQuantity() + "";
        comb_type = 2;
        price1 = ComputationUtils.parseDouble(detailBean.getPrice());
        price2 = 0;
    }

    public void updateUI(Context ct) {
        this.ct = ct;

        LayoutInflater ift_01 = LayoutInflater.from(ct);
        v = ift_01.inflate(R.layout.v_product, null);
        TextView tv_price = v.findViewById(R.id.tv_price);
        btn_purchase = v.findViewById(R.id.btn_purchase);
        TextView tv_prodname = v.findViewById(R.id.tv_prodname);
        ImageView iv_product = v.findViewById(R.id.iv_product);
        TextView tv_spePrice = v.findViewById(R.id.tv_spePrize);
        ImageView iv_spePrice = v.findViewById(R.id.iv_spePrice);
        ImageView ivRedeemPoint = v.findViewById(R.id.iv_redeem_point);
        TextView tv_slash = v.findViewById(R.id.tv_slash);
        TextView tvRedeemPoint = v.findViewById(R.id.tv_redeem_point);

        tv_prodname.setText(prod_name1);

        String pricePrefix = ct.getString(R.string.pricePrefix);
        if(combPrice != 0.0){
            tv_price.setText(pricePrefix + FormatUtil.removeDot(combPrice));
        }else{
            tv_price.setText(pricePrefix + FormatUtil.removeDot(price1));
        }

//        tv_price.setText(pricePrefix + price1);
        if (spe_price == 0) {
            tv_spePrice.setVisibility(View.GONE);
            iv_spePrice.setVisibility(View.GONE);
            tv_slash.setVisibility(View.GONE);
        } else {
            tv_spePrice.setText(pricePrefix + FormatUtil.removeDot(spe_price));
            tv_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (productVO.getImgfile1() != null) {
            CommonUtils.loadImage(ct,iv_product,Config.productImgPath + productVO.getImgfile1());
        }

        //判斷是否可累積點數
        if("1".equals(productVO.getIsredeem())
                && OrderManager.getInstance().isLogin()
                && Double.parseDouble(OrderManager.getInstance().getMember().getPoints())  > Integer.parseInt(productVO.getRedeem_point())){
            ivRedeemPoint.setVisibility(View.VISIBLE);
            tvRedeemPoint.setVisibility(View.VISIBLE);
            tvRedeemPoint.setText(productVO.getRedeem_point()+"點");
            iv_spePrice.setVisibility(View.GONE);
        }

        if (productVO.isStop_sell()) {
            TextView tvStopSale = v.findViewById(R.id.tvStopSale);
            tvStopSale.setVisibility(View.VISIBLE);
            tvStopSale.bringToFront();
        } else {
            set_actions();
        }
    }

    public void updateUI_combItem(Context ct, double comboPrice) {
        LayoutInflater layoutInflater = LayoutInflater.from(ct);
        v_combItemChange = layoutInflater.inflate(R.layout.v_product_combitem, null);
        TextView tv_price = v_combItemChange.findViewById(R.id.tv_price);
        TextView tv_prodname = v_combItemChange.findViewById(R.id.tv_prodname);
        ImageView iv_product = v_combItemChange.findViewById(R.id.iv_product);
        tv_prodname.setText(prod_name1);
        tv_prodname.setTextSize(28);
        tv_price.setText("+" + getComboPirce(comboPrice));
        tv_price.setTextSize(28);

        if (productVO.getImgfile1() != null) {
            String path = Config.productImgPath + productVO.getImgfile1();
            CommonUtils.loadImage(ct,iv_product,path);
        }

        if (productVO.isStop_sell()) {
            TextView tvStopSale = v_combItemChange.findViewById(R.id.tvStopSale);
            tvStopSale.setVisibility(View.VISIBLE);
            tvStopSale.bringToFront();
        } else {
            v_combItemChange.setOnClickListener(view -> onChangeListener.onChange());
        }
    }

    public void set_actions() {

        v.setOnClickListener(view -> {
            if (ClickUtil.isFastDoubleClick()) return;
            btn_purchase.performClick();
            if (iscomb == 1) {
                combItemList_Show();
            } else {
                taste_showDialog();
            }
        });
    }

    public void combItemList_Show() {
        Product product = new Product(this.prodCate);
        product.set_byjo(jo);
        ProductComboDialog d_combItemList = new ProductComboDialog(ct, product);
        d_combItemList.show();
    }

    public void taste_showDialog() {
        Product product = new Product(this.prodCate);
        product.set_byjo(jo);

        TasteDialog _tasteDialog = new TasteDialog(ct, product);
        _tasteDialog.show();
    }

    public double getUnitPrice() {
        double price_add = 0;
        if (comb_type == 1) {
            price = 0;
            for (CombItem combItem : al_combItem) {
                price = price1;
                if(combItem.productSelected.price1 > 0.0) {
                    price_add = Arith.add(price_add, combItem.productSelected.price1);
                }else{
                    price_add = Arith.add(price_add, combItem.productSelected.price);
                }

                for (Detail detail : combItem.productSelected.selectedDetails_Get()) {
                    String combQty = combItem.productSelected.combQty;
                    price_add += detail.price * ComputationUtils.parseInt(combQty);
                }
            }
        }

        if (comb_type == 2) {
            if(price1 > 0.0) {
                price = 0;
                price_add = Arith.add(price_add, price1);
            }
        }

        if (comb_type == 0) {
            if(price1 > 0.0) {
                price = 0;
                price_add = Arith.add(price_add, price1);
            }
        };
        price += price_add;
        return price;
    }

    public double price_get() {
        double price_add = 0;
        double totalPrice = spe_price == 0 ? combPrice != 0.0 ? combPrice :price1 : spe_price;
        if (comb_type == 0) {
            for (Detail detail : selectedDetails_Get()) {
                price_add += detail.price;
            }
            totalPrice += price_add;
        }

        if(comb_type != 0 && BuildConfig.FLAVOR.equals(FlavorType.hongya.name())){
            for (Detail detail : selectedDetails_Get()) {
                price_add += detail.price;
            }
        }

        if (comb_type == 1) {
             totalPrice = spe_price == 0 ? price1 : spe_price;
            for (CombItem combItem : al_combItem) {
                if(combItem.productSelected.price1 > 0.0) {
                    price_add = Arith.add(price_add, combItem.productSelected.price1);
                }else{
                    price_add = Arith.add(price_add, combItem.productSelected.price);
                }

                for (Detail detail : combItem.productSelected.selectedDetails_Get()) {
                    String combQty = combItem.productSelected.combQty;
                    price_add += detail.price * ComputationUtils.parseInt(combQty);
                }
            }
            totalPrice += price_add;
        }

        if (comb_type == 2) {

            if(price1 > 0.0) {
                totalPrice = 0;
                price_add = Arith.add(price_add, price1);
            }else{
                price_add = Arith.add(price_add, price);
            }
            totalPrice += price_add;
        }

//        price = price1;
        return totalPrice;
    }

    public double getSpecialPrice() {
        double price_add = 0;

        if (comb_type == 0) {
            for (Detail detail : selectedDetails_Get()) {
                price_add += detail.price;
            }
        }

        if(comb_type != 0 && BuildConfig.FLAVOR.equals(FlavorType.hongya.name())){
            for (Detail detail : selectedDetails_Get()) {
                price_add += detail.price;
            }
        }

        if (comb_type == 1) {
            for (CombItem combItem : al_combItem) {
                price_add = Arith.add(price_add, Double.parseDouble(combItem.productSelected.getComboPirce(combItem.comboPrice)));
                for (Detail detail : combItem.productSelected.selectedDetails_Get()) {
                    String combQty = combItem.productSelected.combQty;
                    price_add += detail.price * ComputationUtils.parseInt(combQty);
                }
            }
        }
        double price = 0;
        price = spe_price == 0 ? combPrice != 0.0 ? combPrice :price1 : spe_price;
        price += price_add;
        return price;
    }

    public String get_priceStr() {
        String d = spe_price == 0 ? combPrice != 0.0 ? combPrice + "" :price1 + "" : spe_price + "";
        String e = d.substring(d.length() - 2);

        if (e.equals(".0")) {
            d = d.replace(".0", "");
        }

        return d;
    }

    public String getComboPirce(double comboPrice) {
        String d = spe_price == 0 ? combPrice != 0.0 ? combPrice + "" :price1 + "" : spe_price + "";
        if(!d.equals("0.0")){
            d = (price1 - comboPrice) +"";
        }
        String e = d.substring(d.length() - 2);

        if (e.equals(".0")) {
            d = d.replace(".0", "");
        }

        return d;
    }

    public double total_get() {
        double total = price_get() * count;
        total += item_disc;
        return total;
    }

    public double getTotalSpecialPrice() {
        double total = getSpecialPrice() * count;
        total += item_disc;
        return total;
    }

    public String detailList_get() {
        String list = "";
        for (Taste taste : al_taste) {
            for (Detail detail : taste.details) {
                if (detail.selected) {
                    String name = detail.price == 0 ? detail.name : detail.name + "(+" + detail.price + ")";
                    if (list.length() == 0) {
                        list = list + name;
                    } else {
                        list = list + "/" + name;
                    }
                }
            }
        }

        return list;
    }

    public String tasteMemo_get() {
        String list = "";
        for (Taste taste : al_taste) {
            for (Detail detail : taste.details) {
                if (detail.selected) {
                    String name = detail.price == 0 ? detail.name : detail.name + "(+" + detail.price + ")";
                    if (list.length() == 0) {
                        list = list + name;
                    } else {
                        list = list + ";" + name;
                    }
                }
            }
        }

        return list;
    }

    public String combDetail_Get() {
        String List = "";
        for (CombItem combItem : al_combItem) {
            String combString = combItem.productSelected.prod_name1 + ":" + combItem.productSelected.detailList_get();
            List = List + (List.equals("") ? "" : "/") + combString;
        }

        return List;
    }

    public ArrayList<Detail> selectedDetails_Get() {
        ArrayList<Detail> ar_detail = new ArrayList<>();
        for (Taste taste : al_taste) {
            for (Detail detail : taste.details) {
                if (detail.selected) {
                    ar_detail.add(detail);
                }
            }
        }

        return ar_detail;
    }

    public void changeButtonBackground(boolean selected) {
        int bg = selected ? R.drawable.btn_selector_pressed : R.drawable.btn_selector;
        v_combItemChange.findViewById(R.id.btn_change).setBackgroundResource(bg);
    }
}
