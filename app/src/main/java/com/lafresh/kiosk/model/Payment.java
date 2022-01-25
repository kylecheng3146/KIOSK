package com.lafresh.kiosk.model;

import org.jetbrains.annotations.NotNull;

public class Payment {
    /**
     * type : CASH
     * payment_amount : 1000
     * transaction_id : 2001795436021
     */

    private String type;
    private int payment_amount;
    private String transaction_id;
    private Detail detail;
    @NotNull
    private String relate_id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(int payment_amount) {
        this.payment_amount = payment_amount;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getRelate_id() {
        return relate_id;
    }

    public void setRelate_id(String relate_id) {
        this.relate_id = relate_id;
    }

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public static class Detail {
        private String resource_number;
        private String terminal_data;
        private String auth_code;
        private String reference_no;

        public String getResource_number() {
            return resource_number;
        }

        public void setResource_number(String resource_number) {
            this.resource_number = resource_number;
        }

        public String getTerminal_data() {
            return terminal_data;
        }

        public void setTerminal_data(String terminal_data) {
            this.terminal_data = terminal_data;
        }

        public String getAuth_code() {
            return auth_code;
        }

        public void setAuth_code(String auth_code) {
            this.auth_code = auth_code;
        }

        public String getReference_no() {
            return reference_no;
        }

        public void setReference_no(String reference_no) {
            this.reference_no = reference_no;
        }
    }
}
