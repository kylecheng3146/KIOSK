package com.lafresh.kiosk.httprequest.model;

import com.lafresh.kiosk.Bill;

import java.util.List;

public class OrderResponse {

    /**
     * datacnt : 1
     * worder_id : 18122500003
     * shop_id : 11
     * status_code : A
     * status : 店長APP已接單
     * input_date : 2018-12-25 15:40:51
     * meal_date : 2018-12-25 16:10
     * sale_method : 2
     * method_name : 外送
     * payment_terms : 1
     * rec_name : 張
     * rec_mobile : 0XXXXXXXXX9
     * tot_sales : 30
     * rec_addr :
     * tot_point : 0
     * tot_discharge : 0
     * discount : 0
     * total : 0
     * takeno : k003
     * exchange : []
     * weborder01 : [{"prod_name1":"牛奶金萱","enable":"1","stop_sale":"0","company_id":"LAJOIN2","shop_id":"11","worder_id":"18122500003","worder_sno":"1","prod_id":"A004005","sale_price":"30.0000","qty":"1.0000","item_disc":".0000","prom_id":null,"prom_sno":null,"price_type":"0","free_emp":null,"free_memo":null,"comb_sale_sno":null,"comb_sno":null,"comb_type":"0","comb_qty":".0000","taste_memo":"","request_memo":null,"redeem_point":null,"tk_bno":null,"tk_eno":null,"dis_type":"0","dis_number":"100.0000","pack_sale_sno":null,"pack_sno":null,"pack_type":"0","pack_qty":".0000","prom_qty_type":null,"prom_qty_id":null,"prom_qty_sno":null,"prom_qty_qty":null,"proptype":null,"itemdisc_total":"0","szfprod_id":null,"tkno":null,"weborder011":[],"weborder014":[]}]
     * weborder02 : [{"company_id":"LAJOIN2","shop_id":"11","worder_id":"18122500003","pay_id":"Q006","amount":"30.0000","old_amount":".0000","relate_id":"18122500003|2018122560354678410","isinv":"1","approveCode":"2018122560354678410","Request_status":"0","Refund_status":"0"}]
     * weborder021 : []
     * time_arr : null
     * code : 0
     * message : 新增訂單成功
     * invoice : {"company_id":"LAJOIN2","shop_id":"11","inv_no_b":"UJ00033224","inv_no_e":"UJ00033224","ticket_id":"18122500003","input_date":"2018-12-25 15:40:52.000","cust_code":"","amount":"30.0000","tax":"30.0000","taxtype":"1","inv_status":"0","mod_date":null,"mod_user":null,"user_id":null,"pos_id":"k1","inv_source":"3","transfer_status":"0","last_update":"2018-12-25 15:40:52.000","Split_id":null,"pkno":"11k1-201812250002","Issend":"0","Issend2":"0","random_no":"7210","npoban":"","buyer_number":"","Inv_fmt":"9","batchnumber":null,"invrec_namt":".0000","invrec_uamt":"32.0000","exchange":"0","change_lnk":null,"printmark":"1","buyer_type":"","creditcard_no":null,"void_date":null,"cancel_date":null,"ret_ticketid":null,"AESKey":null,"GUI_number":"24436074"}
     * sync : {"sync_result":"{\"code\":\"1\",\"msg\":\"CompanyId: LAJOIN2 ShopId: 11 -- 未連線\"}","code":1,"message":"CompanyId: LAJOIN2 ShopId: 11 -- 未連線"}
     * time : ["(1)開始到引用完成:11毫秒","(2)引用完成到驗證完成:10毫秒","(3)驗證完成到取得客戶參數:4毫秒","(4)取得客戶參數到判斷有無訂單編號前:0毫秒","(5)有訂單編號-檢查訂單資料:1毫秒","(6)有訂單編號-檢查訂單資料:5毫秒","(7)刪除訂單明細, 重新insert:22毫秒","(8)取得會員vip_level銷售方式名稱:5毫秒","(10)取得銷售方式名稱:2毫秒","(11)取得總店銷售方式名稱:4毫秒","(12)取得vip 姓名手機:5毫秒","(13)更新主檔:6毫秒","(14)清除主檔以外檔案:9毫秒","(16)寫入訂單鎖:5毫秒","(17)準備寫入訂單明細:12毫秒","(18)檢查商品銷售時段:3毫秒","(19)寫入訂單明細0:7毫秒","(21)準備寫入口味:5毫秒","(23)準備寫入付款方式:0毫秒","(24)寫入付款方式0:5毫秒","檢查訂單有無明細:1毫秒","取得orderList:249毫秒","(25)更新weborder00訂單金額:9毫秒","(26-1)取得weborder00及014的item_idisc總和:1毫秒","(26-2)取得weborder01及商品主檔的部分欄位:8毫秒","(26-3)檢查是否有商品兌換卷:2毫秒","(26-4)撈取011跟014:2毫秒","(26-5)撈取02:2毫秒","(26-6)撈取021:4毫秒","(26)取得訂單詳細資料:24毫秒","(29)檢查是否超過接單金額限制:0毫秒","(30)麥味登流程:0毫秒","(31)檢查是否有抵用票卷:42.505979537964毫秒","(32)檢查是否有商品兌換劵:4.533052444458毫秒","(33)處裡票卷或扣點流程:0.018119812011719毫秒","(34)檢查是否自動接單及其流程:41.838884353638毫秒","(35)麥味登補救措施:498.17895889282毫秒","(36)同步訂單:643.29791069031毫秒","(37)取得分店名稱:4.878044128418毫秒","(38)程式結束，總共耗時:685毫秒"]
     */

    private int datacnt;
    private String worder_id;
    private String shop_id;
    private String status_code;
    private String status;
    private String input_date;
    private String meal_date;
    private String sale_method;
    private String method_name;
    private String payment_terms;
    private String rec_name;
    private String rec_mobile;
    private int tot_sales;
    private String rec_addr;
    private int tot_point;
    private String tot_discharge;
    private int discount;
    private int total;
    private String takeno;
    private Object time_arr;
    private int code = -999;
    private String message;
    private Invoice invoice;
    private List<?> exchange;
    private List<WebOrder01> weborder01;
    private List<WebOrder02> weborder02;
    private List<?> weborder021;
    private List<String> time;

    private String table_no;

    public int getDatacnt() {
        return datacnt;
    }

    public void setDatacnt(int datacnt) {
        this.datacnt = datacnt;
    }

    public String getWorder_id() {
        return worder_id;
    }

    public void setWorder_id(String worder_id) {
        this.worder_id = worder_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInput_date() {
        return input_date;
    }

    public void setInput_date(String input_date) {
        this.input_date = input_date;
    }

    public String getMeal_date() {
        return meal_date;
    }

    public void setMeal_date(String meal_date) {
        this.meal_date = meal_date;
    }

    public String getSale_method() {
        return sale_method;
    }

    public void setSale_method(String sale_method) {
        this.sale_method = sale_method;
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }

    public String getPayment_terms() {
        return payment_terms;
    }

    public void setPayment_terms(String payment_terms) {
        this.payment_terms = payment_terms;
    }

    public String getRec_name() {
        return rec_name;
    }

    public void setRec_name(String rec_name) {
        this.rec_name = rec_name;
    }

    public String getRec_mobile() {
        return rec_mobile;
    }

    public void setRec_mobile(String rec_mobile) {
        this.rec_mobile = rec_mobile;
    }

    public int getTot_sales() {
        return tot_sales;
    }

    public void setTot_sales(int tot_sales) {
        this.tot_sales = tot_sales;
    }

    public String getRec_addr() {
        return rec_addr;
    }

    public void setRec_addr(String rec_addr) {
        this.rec_addr = rec_addr;
    }

    public int getTot_point() {
        return tot_point;
    }

    public void setTot_point(int tot_point) {
        this.tot_point = tot_point;
    }

    public String getTot_discharge() {
        return tot_discharge;
    }

    public void setTot_discharge(String tot_discharge) {
        this.tot_discharge = tot_discharge;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTakeno() {
        return takeno;
    }

    public void setTakeno(String takeno) {
        this.takeno = takeno;
    }

    public Object getTime_arr() {
        return time_arr;
    }

    public void setTime_arr(Object time_arr) {
        this.time_arr = time_arr;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public List<?> getExchange() {
        return exchange;
    }

    public void setExchange(List<?> exchange) {
        this.exchange = exchange;
    }

    public List<WebOrder01> getWeborder01() {
        return weborder01;
    }

    public void setWeborder01(List<WebOrder01> weborder01) {
        this.weborder01 = weborder01;
    }

    public List<WebOrder02> getWeborder02() {
        return weborder02;
    }

    public void setWeborder02(List<WebOrder02> weborder02) {
        this.weborder02 = weborder02;
    }

    public List<?> getWeborder021() {
        return weborder021;
    }

    public void setWeborder021(List<?> weborder021) {
        this.weborder021 = weborder021;
    }

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

    public String getTable_no() {
        return table_no;
    }

    public void setTable_no(String table_no) {
        this.table_no = table_no;
    }

    public static OrderResponse generateFromOrderInfo(GetOrderInfoRes res) {
        OrderResponse os = new OrderResponse();
        os.setCode(res.getCode());
        os.setMessage(res.getMessage());
        os.setWorder_id(Bill.fromServer.worder_id);
        os.setWeborder01(res.getWeborder01());
        os.setWeborder02(res.getWeborder02());
        os.setWeborder021(res.getWeborder021());
        os.setTakeno(res.getWeborder00().getTakeno());
        os.setPayment_terms(Bill.fromServer.get_payment_terms());
        os.setTot_sales(res.getWeborder00().getTot_sales());
        os.setTotal(res.getWeborder00().getTotal());
        os.setDiscount(res.getWeborder00().getDiscount());
        os.setInvoice(res.getInvoice());
        return os;
    }
}
