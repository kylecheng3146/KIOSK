package com.lafresh.kiosk.httprequest.model;

import java.util.List;

public class GetSaleMethodRes {

    /**
     * code : 0
     * message : 取餐方式資料
     * datacnt : 3
     * data : [{"method_id":"1","name":"內用","isaddr":"0","servicecharge":"1","vip_nondisc":"0","is_fare":"0"},{"method_id":"2","name":"外帶","isaddr":"0","servicecharge":"0","vip_nondisc":"0","is_fare":"0"},{"method_id":"5","name":"自取","isaddr":"0","servicecharge":"0","vip_nondisc":"0","is_fare":"0"}]
     */

    private int code;
    private String message;
    private int datacnt;
    private List<SaleMethod> data;

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

    public List<SaleMethod> getData() {
        return data;
    }

    public void setData(List<SaleMethod> data) {
        this.data = data;
    }

    public static class SaleMethod {
        /**
         * method_id : 1
         * name : 內用
         * isaddr : 0
         * servicecharge : 1
         * vip_nondisc : 0
         * is_fare : 0
         */

        private String method_id;
        private String name;
        private String isaddr;
        private String servicecharge;
        private String vip_nondisc;
        private String is_fare;

        public String getMethod_id() {
            return method_id;
        }

        public void setMethod_id(String method_id) {
            this.method_id = method_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIsaddr() {
            return isaddr;
        }

        public void setIsaddr(String isaddr) {
            this.isaddr = isaddr;
        }

        public String getServicecharge() {
            return servicecharge;
        }

        public void setServicecharge(String servicecharge) {
            this.servicecharge = servicecharge;
        }

        public String getVip_nondisc() {
            return vip_nondisc;
        }

        public void setVip_nondisc(String vip_nondisc) {
            this.vip_nondisc = vip_nondisc;
        }

        public String getIs_fare() {
            return is_fare;
        }

        public void setIs_fare(String is_fare) {
            this.is_fare = is_fare;
        }
    }
}
