package com.lafresh.kiosk.httprequest.model;

import java.util.List;

public class GetOrderInfoRes {

    /**
     * datacnt : 1
     * weborder00 : {"worder_id":"10600119080700781","shop_id":"106001","shop_name":null,"status":"4","status_name":"結案","input_date":"2019-08-07 17:45:33","meal_date":"2019-08-07 18:15","sale_method":"2","method_name":"外帶","rec_name":"Alex","rec_mobile":"09123456789","tot_sales":375,"rec_addr":"","tot_point":0,"discount":0,"cancel_reason":null,"table_no":"","fare":0,"total":375,"storefrom":"KIOSK","group_buy_id":"0","group_name":null,"rec_city":"0","takeno":"k781","secretary_id":"0","secretary_name":null,"secretary_leader_name":""}
     * ticket_discount : 0
     * weborder01 : [{"prod_name1":null,"enable":null,"stop_sale":null,"company_id":"82332528","shop_id":"106001","worder_id":"10600119080700781","worder_sno":"1","prod_id":"UC06000003","sale_price":"90.0000","qty":"2.0000","item_disc":".0000","prom_id":null,"prom_sno":null,"price_type":"0","free_emp":null,"free_memo":null,"comb_sale_sno":null,"comb_sno":null,"comb_type":"0","comb_qty":".0000","taste_memo":"","request_memo":null,"redeem_point":null,"tk_bno":null,"tk_eno":null,"dis_type":"0","dis_number":"100.0000","pack_sale_sno":null,"pack_sno":null,"pack_type":"0","pack_qty":".0000","prom_qty_type":null,"prom_qty_id":null,"prom_qty_sno":null,"prom_qty_qty":null,"proptype":null,"itemdisc_total":"0","szfprod_id":null,"tkno":null,"weborder011":[],"weborder014":[]},{"prod_name1":null,"enable":null,"stop_sale":null,"company_id":"82332528","shop_id":"106001","worder_id":"10600119080700781","worder_sno":"2","prod_id":"UC06000002","sale_price":"110.0000","qty":"1.0000","item_disc":".0000","prom_id":null,"prom_sno":null,"price_type":"0","free_emp":null,"free_memo":null,"comb_sale_sno":null,"comb_sno":null,"comb_type":"0","comb_qty":".0000","taste_memo":"","request_memo":null,"redeem_point":null,"tk_bno":null,"tk_eno":null,"dis_type":"0","dis_number":"100.0000","pack_sale_sno":null,"pack_sno":null,"pack_type":"0","pack_qty":".0000","prom_qty_type":null,"prom_qty_id":null,"prom_qty_sno":null,"prom_qty_qty":null,"proptype":null,"itemdisc_total":"0","szfprod_id":null,"tkno":null,"weborder011":[],"weborder014":[]},{"prod_name1":null,"enable":null,"stop_sale":null,"company_id":"82332528","shop_id":"106001","worder_id":"10600119080700781","worder_sno":"3","prod_id":"UC03000003","sale_price":"15.0000","qty":"3.0000","item_disc":".0000","prom_id":null,"prom_sno":null,"price_type":"0","free_emp":null,"free_memo":null,"comb_sale_sno":null,"comb_sno":null,"comb_type":"0","comb_qty":".0000","taste_memo":"","request_memo":null,"redeem_point":null,"tk_bno":null,"tk_eno":null,"dis_type":"0","dis_number":"100.0000","pack_sale_sno":null,"pack_sno":null,"pack_type":"0","pack_qty":".0000","prom_qty_type":null,"prom_qty_id":null,"prom_qty_sno":null,"prom_qty_qty":null,"proptype":null,"itemdisc_total":"0","szfprod_id":null,"tkno":null,"weborder011":[],"weborder014":[]},{"prod_name1":null,"enable":null,"stop_sale":null,"company_id":"82332528","shop_id":"106001","worder_id":"10600119080700781","worder_sno":"4","prod_id":"UC04200001","sale_price":"25.0000","qty":"1.0000","item_disc":".0000","prom_id":null,"prom_sno":null,"price_type":"0","free_emp":null,"free_memo":null,"comb_sale_sno":null,"comb_sno":null,"comb_type":"0","comb_qty":".0000","taste_memo":"","request_memo":null,"redeem_point":null,"tk_bno":null,"tk_eno":null,"dis_type":"0","dis_number":"100.0000","pack_sale_sno":null,"pack_sno":null,"pack_type":"0","pack_qty":".0000","prom_qty_type":null,"prom_qty_id":null,"prom_qty_sno":null,"prom_qty_qty":null,"proptype":null,"itemdisc_total":"0","szfprod_id":null,"tkno":null,"weborder011":[],"weborder014":[]},{"prod_name1":null,"enable":null,"stop_sale":null,"company_id":"82332528","shop_id":"106001","worder_id":"10600119080700781","worder_sno":"5","prod_id":"UC04100002","sale_price":"15.0000","qty":"1.0000","item_disc":".0000","prom_id":null,"prom_sno":null,"price_type":"0","free_emp":null,"free_memo":null,"comb_sale_sno":null,"comb_sno":null,"comb_type":"0","comb_qty":".0000","taste_memo":"","request_memo":null,"redeem_point":null,"tk_bno":null,"tk_eno":null,"dis_type":"0","dis_number":"100.0000","pack_sale_sno":null,"pack_sno":null,"pack_type":"0","pack_qty":".0000","prom_qty_type":null,"prom_qty_id":null,"prom_qty_sno":null,"prom_qty_qty":null,"proptype":null,"itemdisc_total":"0","szfprod_id":null,"tkno":null,"weborder011":[],"weborder014":[]}]
     * weborder02 : [{"company_id":"82332528","shop_id":"106001","worder_id":"10600119080700781","pay_id":"K","amount":"375.0000","old_amount":".0000","relate_id":"234570257|574|500|74","isinv":"1","approveCode":null,"Request_status":"0","Refund_status":"0"}]
     * weborder021 : []
     * code : 0
     * message : 訂單資料
     */

    private int datacnt;
    private WebOrder00 weborder00;
    private int ticket_discount;
    private int code = -999;
    private String message;
    private List<WebOrder01> weborder01;
    private List<WebOrder02> weborder02;
    private List<?> weborder021;
    private Invoice invoice;

    public int getDatacnt() {
        return datacnt;
    }

    public void setDatacnt(int datacnt) {
        this.datacnt = datacnt;
    }


    public int getTicket_discount() {
        return ticket_discount;
    }

    public void setTicket_discount(int ticket_discount) {
        this.ticket_discount = ticket_discount;
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

    public WebOrder00 getWeborder00() {
        return weborder00;
    }

    public void setWeborder00(WebOrder00 weborder00) {
        this.weborder00 = weborder00;
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

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
