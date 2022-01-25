package com.lafresh.kiosk.model;

import java.util.List;

/**
 * Created by Kevin on 2020/8/14.
 */

public class MealTime {
    /**
     * code : 0
     * message : 取餐時段資料
     * meal_time_type : 1
     * date : 2020-08-15
     * datacnt : 96
     * allday_rest : 0
     * next_time : 09:57
     * ext_meal_time :
     * data : [{"time":"00:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"00:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"00:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"00:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"01:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"01:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"01:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"01:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"02:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"02:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"02:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"02:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"03:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"03:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"03:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"03:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"04:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"04:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"04:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"04:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"05:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"05:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"05:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"05:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"06:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"06:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"06:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"06:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"07:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"07:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"07:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"07:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"08:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"08:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"08:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"08:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"09:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"09:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"09:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"09:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"10:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"10:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"10:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"10:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"11:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"11:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"11:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"11:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"12:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"12:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"12:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"12:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"13:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"13:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"13:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"13:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"14:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"14:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"14:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"14:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"15:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"15:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"15:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"15:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"16:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"16:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"16:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"16:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"17:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"17:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"17:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"17:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"18:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"18:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"18:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"18:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"19:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"19:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"19:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"19:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"20:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"20:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"20:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"20:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"21:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"21:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"21:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"21:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"22:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"22:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"22:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"22:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"23:00","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"23:15","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"23:30","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0},{"time":"23:45","gap_amt_limit":"0","order_amt_limit":"0","ismeal":"1","sales_total":0}]
     */

    private int code;
    private String message;
    private String meal_time_type;
    private String date;
    private int datacnt;
    private int allday_rest;
    private String next_time;
    private String ext_meal_time;
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

    public String getMeal_time_type() {
        return meal_time_type;
    }

    public void setMeal_time_type(String meal_time_type) {
        this.meal_time_type = meal_time_type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDatacnt() {
        return datacnt;
    }

    public void setDatacnt(int datacnt) {
        this.datacnt = datacnt;
    }

    public int getAllday_rest() {
        return allday_rest;
    }

    public void setAllday_rest(int allday_rest) {
        this.allday_rest = allday_rest;
    }

    public String getNext_time() {
        return next_time;
    }

    public void setNext_time(String next_time) {
        this.next_time = next_time;
    }

    public String getExt_meal_time() {
        return ext_meal_time;
    }

    public void setExt_meal_time(String ext_meal_time) {
        this.ext_meal_time = ext_meal_time;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * time : 00:00
         * gap_amt_limit : 0
         * order_amt_limit : 0
         * ismeal : 1
         * sales_total : 0
         */

        private String time;
        private String gap_amt_limit;
        private String order_amt_limit;
        private String ismeal;
        private int sales_total;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getGap_amt_limit() {
            return gap_amt_limit;
        }

        public void setGap_amt_limit(String gap_amt_limit) {
            this.gap_amt_limit = gap_amt_limit;
        }

        public String getOrder_amt_limit() {
            return order_amt_limit;
        }

        public void setOrder_amt_limit(String order_amt_limit) {
            this.order_amt_limit = order_amt_limit;
        }

        public String getIsmeal() {
            return ismeal;
        }

        public void setIsmeal(String ismeal) {
            this.ismeal = ismeal;
        }

        public int getSales_total() {
            return sales_total;
        }

        public void setSales_total(int sales_total) {
            this.sales_total = sales_total;
        }
    }
}
