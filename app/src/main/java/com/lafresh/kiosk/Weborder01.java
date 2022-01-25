package com.lafresh.kiosk;


import com.lafresh.kiosk.utils.FormatUtil;

import org.json.JSONObject;

public class Weborder01 {
    String prod_name1;
    int sale_price;
    int qty;

    public void setProductData(JSONObject jo) {
        try {
            prod_name1 = jo.getString("prod_name1");
            sale_price = jo.getInt("sale_price");
            qty = jo.getInt("qty");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static JSONObject setJObyProduct(Product product, int comb_type ,String comb_sale_sno) {
        JSONObject jo_data = new JSONObject();
        try {
            jo_data.put("worder_sno", product.worder_sno);
            jo_data.put("prod_id", product.prod_id);
            jo_data.put("sale_price", product.price1 + "");

            jo_data.put("qty", product.count);
            jo_data.put("comb_type", comb_type + "");
            if (comb_type != 0) {
                jo_data.put("comb_sale_sno", comb_sale_sno);
                jo_data.put("comb_sno", FormatUtil.removeDot(product.comb_sno));
                jo_data.put("comb_qty", product.combQty);
            }
            jo_data.put("pack_type", "0");
            jo_data.put("pack_qty", "0");
            String taste_memo = "";
            try {
                taste_memo = product.tasteMemo_get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            jo_data.put("taste_memo", taste_memo);
            jo_data.put("item_disc", "0");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jo_data;
    }
}
