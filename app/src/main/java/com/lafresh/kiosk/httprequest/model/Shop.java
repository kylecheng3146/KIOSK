package com.lafresh.kiosk.httprequest.model;

import java.util.List;

/**
 * Created by Kevin on 2020/8/20.
 */

public class Shop {
    /**
     * code : 0
     * message : 訊息資料
     * datacnt : 1
     * data : [{"company_id":"82332528","shop_id":"000030","shop_name":"台北訓練機","shop_kind":"1","tel":"","fax":"","zip":"","worktime":"","address":"","longitude":".0000000000","latitude":".0000000000","pov_heading":".0000000000","pov_pitch":".0000000000","pov_zoom":".0000000000","appmeal":"1","htmlbody":"","is_invoice":"0"}]
     */

    private int code;
    private String message;
    private int datacnt;
    private List<DataBean> data;

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

    public int getDatacnt() {
        return datacnt;
    }

    public void setDatacnt(int datacnt) {
        this.datacnt = datacnt;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * company_id : 82332528
         * shop_id : 000030
         * shop_name : 台北訓練機
         * shop_kind : 1
         * tel :
         * fax :
         * zip :
         * worktime :
         * address :
         * longitude : .0000000000
         * latitude : .0000000000
         * pov_heading : .0000000000
         * pov_pitch : .0000000000
         * pov_zoom : .0000000000
         * appmeal : 1
         * htmlbody :
         * is_invoice : 0
         */

        private String company_id;
        private String shop_id;
        private String shop_name;
        private String shop_kind;
        private String tel;
        private String fax;
        private String zip;
        private String worktime;
        private String address;
        private String longitude;
        private String latitude;
        private String pov_heading;
        private String pov_pitch;
        private String pov_zoom;
        private String appmeal;
        private String htmlbody;
        private String is_invoice;

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public String getShop_id() {
            return shop_id;
        }

        public void setShop_id(String shop_id) {
            this.shop_id = shop_id;
        }

        public String getShop_name() {
            return shop_name;
        }

        public void setShop_name(String shop_name) {
            this.shop_name = shop_name;
        }

        public String getShop_kind() {
            return shop_kind;
        }

        public void setShop_kind(String shop_kind) {
            this.shop_kind = shop_kind;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getWorktime() {
            return worktime;
        }

        public void setWorktime(String worktime) {
            this.worktime = worktime;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getPov_heading() {
            return pov_heading;
        }

        public void setPov_heading(String pov_heading) {
            this.pov_heading = pov_heading;
        }

        public String getPov_pitch() {
            return pov_pitch;
        }

        public void setPov_pitch(String pov_pitch) {
            this.pov_pitch = pov_pitch;
        }

        public String getPov_zoom() {
            return pov_zoom;
        }

        public void setPov_zoom(String pov_zoom) {
            this.pov_zoom = pov_zoom;
        }

        public String getAppmeal() {
            return appmeal;
        }

        public void setAppmeal(String appmeal) {
            this.appmeal = appmeal;
        }

        public String getHtmlbody() {
            return htmlbody;
        }

        public void setHtmlbody(String htmlbody) {
            this.htmlbody = htmlbody;
        }

        public String getIs_invoice() {
            return is_invoice;
        }

        public void setIs_invoice(String is_invoice) {
            this.is_invoice = is_invoice;
        }
    }
}
