package com.lafresh.kiosk.httprequest.model;

import java.util.List;

public class LinePayRes {
    /**
     * returnCode : 0000
     * returnMessage : Success.
     * info : {"transactionId":2020042760040098310,"orderId":"10300120042700012","payInfo":[{"method":"BALANCE","amount":110}],"needCheck":"N","balance":0,"transactionDate":"2020-04-27T02:36:39Z"}
     */

    private String returnCode;
    private String returnMessage;
    private Info info;

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public static class Info {
        /**
         * transactionId : 2020042760040098310
         * orderId : 10300120042700012
         * payInfo : [{"method":"BALANCE","amount":110}]
         * needCheck : N
         * balance : 0
         * transactionDate : 2020-04-27T02:36:39Z
         */

        private String transactionId;
        private String orderId;
        private String needCheck;
        private int balance;
        private String transactionDate;
        private List<PayInfoBean> payInfo;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getNeedCheck() {
            return needCheck;
        }

        public void setNeedCheck(String needCheck) {
            this.needCheck = needCheck;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public String getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(String transactionDate) {
            this.transactionDate = transactionDate;
        }

        public List<PayInfoBean> getPayInfo() {
            return payInfo;
        }

        public void setPayInfo(List<PayInfoBean> payInfo) {
            this.payInfo = payInfo;
        }

        public static class PayInfoBean {
            /**
             * method : BALANCE
             * amount : 110
             */

            private String method;
            private int amount;

            public String getMethod() {
                return method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

            public int getAmount() {
                return amount;
            }

            public void setAmount(int amount) {
                this.amount = amount;
            }
        }
    }
}
