package com.lafresh.kiosk.pipay;

public class PayOrder {

    /**
     * bill_id : 12345678987654321
     * seq_no : 0000000001
     * barcode : PI89928FECDD9D6949
     * amt : 100
     * sale_amt : 100
     * bill_amt : 100
     * proxy_amt : 100
     * nondiscount : 1
     * nondiscount_amt : 100
     * channel_id : LAFRESH
     * store_no : 222222
     * reg_id : 2
     * items : 統一豆漿*2 $40,統一米漿*3 $60#$#$0000000000
     * create_time : 20140929174422
     */

    private String authkey;
    private String shop_id;

    private String bill_id;//特店交易編號
    private String seq_no;//收銀機交易序號
    private String barcode;
    private int amt;
    private int sale_amt;//N
    private int bill_amt;//N
    private int proxy_amt;//N
    private String nondiscount;//N
    private int nondiscount_amt;//N
    private String channel_id;//商店代號 由後台設定
    private String store_no;//門市代碼
    private String reg_id;//POS機號
    private String items;   /**
                            商品說明
                            後綴文字為 #$#$0000000000
                            10位數0與1， 目前前五碼分別代表菸、酒、點 數(遊戲)卡、電話儲值卡、奶粉 ，
                            若有該類型商 品則填1，後七碼為保留欄位;例如有菸及點數 卡，則填入下列內容:
                            #$#$1010000000**/
    private String create_time;//交易送出時間格式 YYYYMMDDHHIISS

    public String getAuthkey() {
        return authkey;
    }

    public void setAuthkey(String authkey) {
        this.authkey = authkey;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getBill_id() {
        return bill_id;
    }

    public void setBill_id(String bill_id) {
        this.bill_id = bill_id;
    }

    public String getSeq_no() {
        return seq_no;
    }

    public void setSeq_no(String seq_no) {
        this.seq_no = seq_no;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getAmt() {
        return amt;
    }

    public void setAmt(int amt) {
        this.amt = amt;
    }

    public int getSale_amt() {
        return sale_amt;
    }

    public void setSale_amt(int sale_amt) {
        this.sale_amt = sale_amt;
    }

    public int getBill_amt() {
        return bill_amt;
    }

    public void setBill_amt(int bill_amt) {
        this.bill_amt = bill_amt;
    }

    public int getProxy_amt() {
        return proxy_amt;
    }

    public void setProxy_amt(int proxy_amt) {
        this.proxy_amt = proxy_amt;
    }

    public String getNondiscount() {
        return nondiscount;
    }

    public void setNondiscount(String nondiscount) {
        this.nondiscount = nondiscount;
    }

    public int getNondiscount_amt() {
        return nondiscount_amt;
    }

    public void setNondiscount_amt(int nondiscount_amt) {
        this.nondiscount_amt = nondiscount_amt;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getStore_no() {
        return store_no;
    }

    public void setStore_no(String store_no) {
        this.store_no = store_no;
    }

    public String getReg_id() {
        return reg_id;
    }

    public void setReg_id(String reg_id) {
        this.reg_id = reg_id;
    }

    public String getItems() {
        return items;
    }

    /**
     * @param items 品名*qty $amt, next
     * */
    public void setItems(String items) {
        //若有煙、酒、點數卡、儲值卡、奶粉等商品，後綴才要改變
        this.items = items + "#$#$0000000000";
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
