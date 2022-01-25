package com.lafresh.kiosk.model;

import java.util.ArrayList;
import java.util.List;

public class Item {

    /**
     * id : P001
     * title : 番茄泡菜炒豆包
     * quantity : 2
     * price : {"unit_price":{"amount":40000,"currency_code":"TWD","formatted_amount":"400"},"total_price":{"amount":80000,"currency_code":"TWD","formatted_amount":"800"}}
     * selected_modifier_groups : [{"id":"1","title":"辣度","selected_items":[{"id":"1-1","title":"小辣","quantity":1,"price":{"unit_price":{"amount":0,"currency_code":"TWD","formatted_amount":"0"},"total_price":{"amount":0,"currency_code":"TWD","formatted_amount":"0"}},"selected_modifier_groups":null}],"removed_items":null}]
     * special_instructions : 不要洋蔥
     * ticket_no : BB0010
     */

    public transient com.lafresh.kiosk.Order order;//刪除比對用
    private String id;
    private String title;
    private String second_title;
    private String abbreviated_title;
    private int quantity;
    private PriceBean price;
    private String special_instructions;
    private String ticket_no;
    private List<SelectedGroup> selected_modifier_groups = new ArrayList<>();
    private int redeem_point;
    private boolean is_hidden;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSecondTitle() {
        return second_title;
    }

    public void setAbbreviatedTitle(String abbreviated_title) {
        this.abbreviated_title = abbreviated_title;
    }

    public String getAbbreviatedTitle() {
        return abbreviated_title;
    }

    public void setSecondTitle(String second_title) {
        this.second_title = second_title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public PriceBean getPrice() {
        return price;
    }

    public void setPrice(PriceBean price) {
        this.price = price;
    }

    public String getSpecial_instructions() {
        return special_instructions;
    }

    public void setSpecial_instructions(String special_instructions) {
        this.special_instructions = special_instructions;
    }
    public String getTicket_no() {
        return ticket_no;
    }

    public void setTicket_no(String ticket_no) {
        this.ticket_no = ticket_no;
    }

    public int getRedeemPoint() {
        return redeem_point;
    }

    public void setRedeemPoint(int redeem_point) {
        this.redeem_point = redeem_point;
    }


    public boolean getIsHidden() {
        return is_hidden;
    }

    public void setIsHidden(boolean is_hidden) {
        this.is_hidden = is_hidden;
    }


    public List<SelectedGroup> getSelected_modifier_groups() {
        return selected_modifier_groups;
    }

    public void setSelected_modifier_groups(List<SelectedGroup> selected_modifier_groups) {
        this.selected_modifier_groups = selected_modifier_groups;
    }

    public static class PriceBean {
        /**
         * unit_price : {"amount":40000,"currency_code":"TWD","formatted_amount":"400"}
         * total_price : {"amount":80000,"currency_code":"TWD","formatted_amount":"800"}
         */

        private StupidPrice unit_price;
        private StupidPrice total_price;
        private int spend_points;

        public StupidPrice getUnit_price() {
            return unit_price;
        }

        public void setUnit_price(StupidPrice unit_price) {
            this.unit_price = unit_price;
        }

        public StupidPrice getTotal_price() {
            return total_price;
        }

        public void setTotal_price(StupidPrice total_price) {
            this.total_price = total_price;
        }

        public int getSpendPoints() {
            return spend_points;
        }

        public void setSpendPoints(int spend_points) {
            this.spend_points = spend_points;
        }

    }

    public static class SelectedGroup {
        /**
         * id : 1
         * title : 辣度
         * selected_items : [{"id":"1-1","title":"小辣","quantity":1,"price":{"unit_price":{"amount":0,"currency_code":"TWD","formatted_amount":"0"},"total_price":{"amount":0,"currency_code":"TWD","formatted_amount":"0"}},"selected_modifier_groups":null}]
         * removed_items : null
         */

        private String id;
        private String title;
        private List<Item> removed_items;
        private List<Item> selected_items;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Item> getRemoved_items() {
            return removed_items;
        }

        public void setRemoved_items(List<Item> removed_items) {
            this.removed_items = removed_items;
        }

        public List<Item> getSelected_items() {
            return selected_items;
        }

        public void setSelected_items(List<Item> selected_items) {
            this.selected_items = selected_items;
        }
    }
}
