package com.lafresh.kiosk.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {

    /**
     * id : STORE2005010001
     * current_state : ACCEPTED
     * placed_at : 2020-05-01T22:25:49Z
     * pickup_at : 2020-05-01T22:25:49Z
     * type : DELIVERY
     * client_device : RETAIL
     * store : {"id":"A01","name":"台南店"}
     * customer : {"name":"王大明","mobile":"0910123456","address":"台南市安平區健康三街999號"}
     * payments : [{"type":"CASH","payment_amount":1000}]
     * charges : {"total":{"amount":1100,"currency_code":"TWD","formatted_amount":"1100"},"sub_total":{"amount":1100,"currency_code":"TWD","formatted_amount":"1100"},"discount":{"amount":100,"currency_code":"TWD","formatted_amount":"100"},"total_fee":{"amount":1000,"currency_code":"TWD","formatted_amount":"1000"},"points": {
     *       "before_transaction": 120,
     *       "exchange_amount": 10,
     *       "after_transaction": 21,
     *       "add": 1
     *     }}
     * cart : {"items":[{"id":"P001","title":"番茄泡菜炒豆包","quantity":2,"price":{"unit_price":{"amount":40000,"currency_code":"TWD","formatted_amount":"400"},"total_price":{"amount":80000,"currency_code":"TWD","formatted_amount":"800"}},"selected_modifier_groups":[{"id":"1","title":"辣度","selected_items":[{"id":"1-1","title":"小辣","quantity":1,"price":{"unit_price":{"amount":0,"currency_code":"TWD","formatted_amount":"0"},"total_price":{"amount":0,"currency_code":"TWD","formatted_amount":"0"}},"selected_modifier_groups":null}],"removed_items":null}]},{"id":"P002","title":"紫蘇籽油腐乳炒飯","special_instructions":"不要洋蔥","quantity":1,"price":{"unit_price":{"amount":20000,"currency_code":"TWD","formatted_amount":"200"},"total_price":{"amount":20000,"currency_code":"TWD","formatted_amount":"200"}},"selected_modifier_groups":[{"id":"2","title":"加飯","selected_items":[{"id":"2-1","title":"白飯","quantity":1,"price":{"unit_price":{"amount":0,"currency_code":"TWD","formatted_amount":"0"},"total_price":{"amount":0,"currency_code":"TWD","formatted_amount":"0"}},"selected_modifier_groups":null}],"removed_items":null},{"id":"3","title":"加胡椒","selected_items":[{"id":"3-1","title":"胡椒","quantity":0,"price":{"unit_price":{"amount":0,"currency_code":"TWD","formatted_amount":"0"},"total_price":{"amount":0,"currency_code":"TWD","formatted_amount":"0"}},"selected_modifier_groups":null}],"removed_items":null}]}]}
     * receipt : {"tax_ID_number":"24436074","npoban":"","carrier_type":"","carrier":""}
     * special_instructions : 訂單備註內容
     * tickets : [ { "number": "A776479" } ],
     */

    private String id;
    private String current_state;
    private String placed_at;
    private String pickup_at;
    private String type;
    private String client_device;
    private StoreBean store;
    private Customer customer;
    private Charges charges;
    private Cart cart;
    private ReceiptData receipt;
    private String special_instructions;
    private List<Payment> payments;
    private List<TicketsBean> tickets;
    private String display_id;
    private String table_number;
    private String activity_message;
    private PickupMethod pickupMethod;
    private String kiosk_type;

    @SerializedName("kiosk_id")
    private String kioskId;

    public PickupMethod getPickupMethod() {
        return pickupMethod;
    }

    public void setPickupMethod(PickupMethod pickupMethod) {
        this.pickupMethod = pickupMethod;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrent_state() {
        return current_state;
    }

    public void setCurrent_state(String current_state) {
        this.current_state = current_state;
    }

    public String getPlaced_at() {
        return placed_at;
    }

    public void setPlaced_at(String placed_at) {
        this.placed_at = placed_at;
    }

    public String getPickup_at() {
        return pickup_at;
    }

    public void setPickup_at(String pickup_at) {
        this.pickup_at = pickup_at;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClient_device() {
        return client_device;
    }

    public void setClient_device(String client_device) {
        this.client_device = client_device;
    }

    public StoreBean getStore() {
        return store;
    }

    public void setStore(StoreBean store) {
        this.store = store;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Charges getCharges() {
        return charges;
    }

    public void setCharges(Charges charges) {
        this.charges = charges;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public ReceiptData getReceipt() {
        return receipt;
    }

    public void setReceipt(ReceiptData receipt) {
        this.receipt = receipt;
    }

    public String getSpecial_instructions() {
        return special_instructions;
    }

    public void setSpecial_instructions(String special_instructions) {
        this.special_instructions = special_instructions;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public List<TicketsBean> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketsBean> tickets) {
        this.tickets = tickets;
    }

    public String getDisplay_id() {
        return display_id;
    }

    public void setDisplay_id(String display_id) {
        this.display_id = display_id;
    }

    public String getTable_number() {
        return table_number;
    }

    public void setTable_number(String table_number) {
        this.table_number = table_number;
    }

    public String getKioskId() {
        return kioskId;
    }

    public void setKioskId(String kioskId) {
        this.kioskId = kioskId;
    }

    public String getKioskType() {
        return kiosk_type;
    }

    public void setKioskType(String kiosk_type) {
        this.kiosk_type = kiosk_type;
    }

    public String getActivityMessage() {
        return activity_message;
    }

    public void setActivityMessage(String activity_message) {
        this.activity_message = activity_message;
    }
    public static class StoreBean {
        /**
         * id : A01
         * name : 台南店
         */

        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class TicketsBean {
        /**
         * number : A776479
         */

        private String number;
        private int price;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }
    }

    public static class Customer {
        /**
         * name : 王大明
         * mobile : 0910123456
         * address : 台南市安平區健康三街999號
         */

        private String name;
        private String mobile;
        private String address;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class Cart {
        private List<Item> items;

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }
    }
}
