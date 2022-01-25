package com.lafresh.kiosk.model;

import java.util.List;

/**
 * 八拍子票券資料結構
 */
public class TicketOrderInfo {

    /**
     * invoiceInfos : {"taxIDNumber":null,"id":202,"category":"TICKET","source":"LAFRESH","invoiceType":"2-COPIES","isElectronicInvoice":true,"isInvoicePrinted":true,"invoicePrefix":"AX","invoiceNumber":"35555556","invoiceRandomCode":"1235","invoiceCreateAt":"2020-04-18T09:32:00.000Z","invoiceYearMonth":"202004","carrierType":null,"carrierClearCode":null,"carrierHiddenCode":null,"taxFreePrice":361,"taxPrice":19,"totalPrice":380,"updatedAt":"2020-04-30T03:55:06.576Z","createdAt":"2020-04-30T03:55:06.576Z"}
     * orderInfos : {"verificationCode":"Ldqie8f51","status":"NORMAL","paymentMethod":"CASH","profit":380,"visitingDate":"2020-04-30","visitingStartTime":"13:00:00","visitingEndTime":"17:00:00","customerName":null,"customerEmail":null,"customerPhone":null,"isSubscribed":false,"createdAt":"2020-04-30T03:52:58.000Z","updatedAt":"2020-04-30T03:55:06.589Z","deletedAt":null,"MemberId":null,"SaleExhibitionId":12,"InvoiceId":202,"SaleExhibition":{"id":12,"type":"NORMAL","SaleExhibitionInfos":[{"id":79,"language":"zh-TW","cover":"https://cdn.imgbin.com/13/14/17/imgbin-giant-bicycles-logo-giant-lakeside-mountain-bike-vip-membership-card-shoe-vTpvqcfxNvD4Ed6faLcBHQsU5.jpg","name":"腳踏車！！！！！！！"},{"id":80,"language":"zh-CN","cover":"https://cdn.imgbin.com/13/14/17/imgbin-giant-bicycles-logo-giant-lakeside-mountain-bike-vip-membership-card-shoe-vTpvqcfxNvD4Ed6faLcBHQsU5.jpg","name":"脚踏车"},{"id":81,"language":"en-US","cover":"https://cdn.imgbin.com/13/14/17/imgbin-giant-bicycles-logo-giant-lakeside-mountain-bike-vip-membership-card-shoe-vTpvqcfxNvD4Ed6faLcBHQsU5.jpg","name":"BBBBBBBBBike exhibition"}]},"OrderTickets":[{"verificationCode":"4SOfzrUySE","sendStatus":"NORMAL","profit":380,"remainCouponPrice":200,"Ticket":{"id":17,"type":"NORMAL","price":400,"couponPrice":100,"couponQuantity":2,"TicketInfos":[{"language":"zh-TW","name":"一般體驗券"}]}}]}
     */

    private InvoiceInfosBean invoiceInfos;
    private OrderInfosBean orderInfos;

    public InvoiceInfosBean getInvoiceInfos() {
        return invoiceInfos;
    }

    public void setInvoiceInfos(InvoiceInfosBean invoiceInfos) {
        this.invoiceInfos = invoiceInfos;
    }

    public OrderInfosBean getOrderInfos() {
        return orderInfos;
    }

    public void setOrderInfos(OrderInfosBean orderInfos) {
        this.orderInfos = orderInfos;
    }

    public static class InvoiceInfosBean {
        /**
         * taxIDNumber : null
         * id : 202
         * category : TICKET
         * source : LAFRESH
         * invoiceType : 2-COPIES
         * isElectronicInvoice : true
         * isInvoicePrinted : true
         * invoicePrefix : AX
         * invoiceNumber : 35555556
         * invoiceRandomCode : 1235
         * invoiceCreateAt : 2020-04-18T09:32:00.000Z
         * invoiceYearMonth : 202004
         * carrierType : null
         * carrierClearCode : null
         * carrierHiddenCode : null
         * taxFreePrice : 361
         * taxPrice : 19
         * totalPrice : 380
         * updatedAt : 2020-04-30T03:55:06.576Z
         * createdAt : 2020-04-30T03:55:06.576Z
         */

        private Object taxIDNumber;
        private int id;
        private String category;
        private String source;
        private String invoiceType;
        private boolean isElectronicInvoice;
        private boolean isInvoicePrinted;
        private String invoicePrefix;
        private String invoiceNumber;
        private String invoiceRandomCode;
        private String invoiceCreateAt;
        private String invoiceYearMonth;
        private Object carrierType;
        private Object carrierClearCode;
        private Object carrierHiddenCode;
        private int taxFreePrice;
        private int taxPrice;
        private int totalPrice;
        private String updatedAt;
        private String createdAt;

        public Object getTaxIDNumber() {
            return taxIDNumber;
        }

        public void setTaxIDNumber(Object taxIDNumber) {
            this.taxIDNumber = taxIDNumber;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getInvoiceType() {
            return invoiceType;
        }

        public void setInvoiceType(String invoiceType) {
            this.invoiceType = invoiceType;
        }

        public boolean isIsElectronicInvoice() {
            return isElectronicInvoice;
        }

        public void setIsElectronicInvoice(boolean isElectronicInvoice) {
            this.isElectronicInvoice = isElectronicInvoice;
        }

        public boolean isIsInvoicePrinted() {
            return isInvoicePrinted;
        }

        public void setIsInvoicePrinted(boolean isInvoicePrinted) {
            this.isInvoicePrinted = isInvoicePrinted;
        }

        public String getInvoicePrefix() {
            return invoicePrefix;
        }

        public void setInvoicePrefix(String invoicePrefix) {
            this.invoicePrefix = invoicePrefix;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }

        public String getInvoiceRandomCode() {
            return invoiceRandomCode;
        }

        public void setInvoiceRandomCode(String invoiceRandomCode) {
            this.invoiceRandomCode = invoiceRandomCode;
        }

        public String getInvoiceCreateAt() {
            return invoiceCreateAt;
        }

        public void setInvoiceCreateAt(String invoiceCreateAt) {
            this.invoiceCreateAt = invoiceCreateAt;
        }

        public String getInvoiceYearMonth() {
            return invoiceYearMonth;
        }

        public void setInvoiceYearMonth(String invoiceYearMonth) {
            this.invoiceYearMonth = invoiceYearMonth;
        }

        public Object getCarrierType() {
            return carrierType;
        }

        public void setCarrierType(Object carrierType) {
            this.carrierType = carrierType;
        }

        public Object getCarrierClearCode() {
            return carrierClearCode;
        }

        public void setCarrierClearCode(Object carrierClearCode) {
            this.carrierClearCode = carrierClearCode;
        }

        public Object getCarrierHiddenCode() {
            return carrierHiddenCode;
        }

        public void setCarrierHiddenCode(Object carrierHiddenCode) {
            this.carrierHiddenCode = carrierHiddenCode;
        }

        public int getTaxFreePrice() {
            return taxFreePrice;
        }

        public void setTaxFreePrice(int taxFreePrice) {
            this.taxFreePrice = taxFreePrice;
        }

        public int getTaxPrice() {
            return taxPrice;
        }

        public void setTaxPrice(int taxPrice) {
            this.taxPrice = taxPrice;
        }

        public int getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(int totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }

    public static class OrderInfosBean {
        /**
         * verificationCode : Ldqie8f51
         * status : NORMAL
         * paymentMethod : CASH
         * profit : 380
         * visitingDate : 2020-04-30
         * visitingStartTime : 13:00:00
         * visitingEndTime : 17:00:00
         * customerName : null
         * customerEmail : null
         * customerPhone : null
         * isSubscribed : false
         * createdAt : 2020-04-30T03:52:58.000Z
         * updatedAt : 2020-04-30T03:55:06.589Z
         * deletedAt : null
         * MemberId : null
         * SaleExhibitionId : 12
         * InvoiceId : 202
         * SaleExhibition : {"id":12,"type":"NORMAL","SaleExhibitionInfos":[{"id":79,"language":"zh-TW","cover":"https://cdn.imgbin.com/13/14/17/imgbin-giant-bicycles-logo-giant-lakeside-mountain-bike-vip-membership-card-shoe-vTpvqcfxNvD4Ed6faLcBHQsU5.jpg","name":"腳踏車！！！！！！！"},{"id":80,"language":"zh-CN","cover":"https://cdn.imgbin.com/13/14/17/imgbin-giant-bicycles-logo-giant-lakeside-mountain-bike-vip-membership-card-shoe-vTpvqcfxNvD4Ed6faLcBHQsU5.jpg","name":"脚踏车"},{"id":81,"language":"en-US","cover":"https://cdn.imgbin.com/13/14/17/imgbin-giant-bicycles-logo-giant-lakeside-mountain-bike-vip-membership-card-shoe-vTpvqcfxNvD4Ed6faLcBHQsU5.jpg","name":"BBBBBBBBBike exhibition"}]}
         * OrderTickets : [{"verificationCode":"4SOfzrUySE","sendStatus":"NORMAL","profit":380,"remainCouponPrice":200,"Ticket":{"id":17,"type":"NORMAL","price":400,"couponPrice":100,"couponQuantity":2,"TicketInfos":[{"language":"zh-TW","name":"一般體驗券"}]}}]
         */

        private String verificationCode;
        private String status;
        private String paymentMethod;
        private int profit;
        private String visitingDate;
        private String visitingStartTime;
        private String visitingEndTime;
        private Object customerName;
        private Object customerEmail;
        private Object customerPhone;
        private boolean isSubscribed;
        private String createdAt;
        private String updatedAt;
        private Object deletedAt;
        private Object MemberId;
        private int SaleExhibitionId;
        private int InvoiceId;
        private SaleExhibitionBean SaleExhibition;
        private List<OrderTicketsBean> OrderTickets;

        public String getVerificationCode() {
            return verificationCode;
        }

        public void setVerificationCode(String verificationCode) {
            this.verificationCode = verificationCode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public int getProfit() {
            return profit;
        }

        public void setProfit(int profit) {
            this.profit = profit;
        }

        public String getVisitingDate() {
            return visitingDate;
        }

        public void setVisitingDate(String visitingDate) {
            this.visitingDate = visitingDate;
        }

        public String getVisitingStartTime() {
            return visitingStartTime;
        }

        public void setVisitingStartTime(String visitingStartTime) {
            this.visitingStartTime = visitingStartTime;
        }

        public String getVisitingEndTime() {
            return visitingEndTime;
        }

        public void setVisitingEndTime(String visitingEndTime) {
            this.visitingEndTime = visitingEndTime;
        }

        public Object getCustomerName() {
            return customerName;
        }

        public void setCustomerName(Object customerName) {
            this.customerName = customerName;
        }

        public Object getCustomerEmail() {
            return customerEmail;
        }

        public void setCustomerEmail(Object customerEmail) {
            this.customerEmail = customerEmail;
        }

        public Object getCustomerPhone() {
            return customerPhone;
        }

        public void setCustomerPhone(Object customerPhone) {
            this.customerPhone = customerPhone;
        }

        public boolean isIsSubscribed() {
            return isSubscribed;
        }

        public void setIsSubscribed(boolean isSubscribed) {
            this.isSubscribed = isSubscribed;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Object getDeletedAt() {
            return deletedAt;
        }

        public void setDeletedAt(Object deletedAt) {
            this.deletedAt = deletedAt;
        }

        public Object getMemberId() {
            return MemberId;
        }

        public void setMemberId(Object MemberId) {
            this.MemberId = MemberId;
        }

        public int getSaleExhibitionId() {
            return SaleExhibitionId;
        }

        public void setSaleExhibitionId(int SaleExhibitionId) {
            this.SaleExhibitionId = SaleExhibitionId;
        }

        public int getInvoiceId() {
            return InvoiceId;
        }

        public void setInvoiceId(int InvoiceId) {
            this.InvoiceId = InvoiceId;
        }

        public SaleExhibitionBean getSaleExhibition() {
            return SaleExhibition;
        }

        public void setSaleExhibition(SaleExhibitionBean SaleExhibition) {
            this.SaleExhibition = SaleExhibition;
        }

        public List<OrderTicketsBean> getOrderTickets() {
            return OrderTickets;
        }

        public void setOrderTickets(List<OrderTicketsBean> OrderTickets) {
            this.OrderTickets = OrderTickets;
        }

        public static class SaleExhibitionBean {
            /**
             * id : 12
             * type : NORMAL
             * SaleExhibitionInfos : [{"id":79,"language":"zh-TW","cover":"https://cdn.imgbin.com/13/14/17/imgbin-giant-bicycles-logo-giant-lakeside-mountain-bike-vip-membership-card-shoe-vTpvqcfxNvD4Ed6faLcBHQsU5.jpg","name":"腳踏車！！！！！！！"},{"id":80,"language":"zh-CN","cover":"https://cdn.imgbin.com/13/14/17/imgbin-giant-bicycles-logo-giant-lakeside-mountain-bike-vip-membership-card-shoe-vTpvqcfxNvD4Ed6faLcBHQsU5.jpg","name":"脚踏车"},{"id":81,"language":"en-US","cover":"https://cdn.imgbin.com/13/14/17/imgbin-giant-bicycles-logo-giant-lakeside-mountain-bike-vip-membership-card-shoe-vTpvqcfxNvD4Ed6faLcBHQsU5.jpg","name":"BBBBBBBBBike exhibition"}]
             */

            private int id;
            private String type;
            private List<SaleExhibitionInfosBean> SaleExhibitionInfos;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<SaleExhibitionInfosBean> getSaleExhibitionInfos() {
                return SaleExhibitionInfos;
            }

            public void setSaleExhibitionInfos(List<SaleExhibitionInfosBean> SaleExhibitionInfos) {
                this.SaleExhibitionInfos = SaleExhibitionInfos;
            }

            public static class SaleExhibitionInfosBean {
                /**
                 * id : 79
                 * language : zh-TW
                 * cover : https://cdn.imgbin.com/13/14/17/imgbin-giant-bicycles-logo-giant-lakeside-mountain-bike-vip-membership-card-shoe-vTpvqcfxNvD4Ed6faLcBHQsU5.jpg
                 * name : 腳踏車！！！！！！！
                 */

                private int id;
                private String language;
                private String cover;
                private String name;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getLanguage() {
                    return language;
                }

                public void setLanguage(String language) {
                    this.language = language;
                }

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
        }

        public static class OrderTicketsBean {
            /**
             * verificationCode : 4SOfzrUySE
             * sendStatus : NORMAL
             * profit : 380
             * remainCouponPrice : 200
             * Ticket : {"id":17,"type":"NORMAL","price":400,"couponPrice":100,"couponQuantity":2,"TicketInfos":[{"language":"zh-TW","name":"一般體驗券"}]}
             */

            private String verificationCode;
            private String sendStatus;
            private int profit;
            private int remainCouponPrice;
            private TicketBean Ticket;

            public String getVerificationCode() {
                return verificationCode;
            }

            public void setVerificationCode(String verificationCode) {
                this.verificationCode = verificationCode;
            }

            public String getSendStatus() {
                return sendStatus;
            }

            public void setSendStatus(String sendStatus) {
                this.sendStatus = sendStatus;
            }

            public int getProfit() {
                return profit;
            }

            public void setProfit(int profit) {
                this.profit = profit;
            }

            public int getRemainCouponPrice() {
                return remainCouponPrice;
            }

            public void setRemainCouponPrice(int remainCouponPrice) {
                this.remainCouponPrice = remainCouponPrice;
            }

            public TicketBean getTicket() {
                return Ticket;
            }

            public void setTicket(TicketBean Ticket) {
                this.Ticket = Ticket;
            }

            public static class TicketBean {
                /**
                 * id : 17
                 * type : NORMAL
                 * price : 400
                 * couponPrice : 100
                 * couponQuantity : 2
                 * TicketInfos : [{"language":"zh-TW","name":"一般體驗券"}]
                 */

                private int id;
                private String type;
                private int price;
                private int couponPrice;
                private int couponQuantity;
                private List<TicketInfosBean> TicketInfos;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public int getPrice() {
                    return price;
                }

                public void setPrice(int price) {
                    this.price = price;
                }

                public int getCouponPrice() {
                    return couponPrice;
                }

                public void setCouponPrice(int couponPrice) {
                    this.couponPrice = couponPrice;
                }

                public int getCouponQuantity() {
                    return couponQuantity;
                }

                public void setCouponQuantity(int couponQuantity) {
                    this.couponQuantity = couponQuantity;
                }

                public List<TicketInfosBean> getTicketInfos() {
                    return TicketInfos;
                }

                public void setTicketInfos(List<TicketInfosBean> TicketInfos) {
                    this.TicketInfos = TicketInfos;
                }

                public static class TicketInfosBean {
                    /**
                     * language : zh-TW
                     * name : 一般體驗券
                     */

                    private String language;
                    private String name;

                    public String getLanguage() {
                        return language;
                    }

                    public void setLanguage(String language) {
                        this.language = language;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }
                }
            }
        }
    }
}
