package com.lafresh.kiosk.creditcardlib.nccc;

import com.lafresh.kiosk.utils.FormatUtil;

/**
 * 固定400碼
 */

public class NCCCTransDataBean {
    private static final String TRANS_TYPE_SALE = "01";
    private static final String TRANS_TYPE_SETTLEMENT = "50";

    private String ecrIndicator = "I";
    private String ecrVersionDate = "      ";
    private String transTypeIndicator = " ";
    private String transType = "01";
    private String esvcIndicator = "E";
    private String hostId = "  ";
    private String receiptNo = "      ";
    private String cardNo = "                   ";
    private String cardSettleQty = "    ";//結帳時=信用卡結帳總筆數，右靠左捕0。失敗為空白
    private String transAmount;
    private String transDate;
    private String transTime;
    private String approvalNo = "         ";
    private String waveCardIndicator = " ";
    private String ecrResponseCode = "    ";
    private String merchantId = "               ";
    private String terminalId = "        ";
    private String expAmount = "            ";
    private String storeId = "                  ";
    private String redeemIndicator = " ";
    private String rdmPaidAmt = "            ";
    private String rdmPoint = "        ";
    private String pointsOfBalance = "        ";
    private String redeemAmt = "            ";
    private String installmentPeriod = "  ";
    private String downPaymentAmount = "            ";
    private String installmentPaymentAmount = "            ";
    private String formalityFee = "            ";
    private String cardType = "  ";
    private String batchNo = "      ";
    private String startTransType = "  ";
    private String mpFlag = " ";
    private String spIssuserId = "        ";
    private String originDate = "        ";
    private String originRrn = "            ";
    private String payItem = "     ";
    private String cardNoHashValue = "                                                  ";
    private String mpResponseCode = "      ";
    private String asmAwardFlag = " ";
    private String mcpIndicator = " ";
    private String bankCode = "   "; //金融機構代碼
    private String reserved = "     ";
    private String hgData = "                                                                              ";

    public static String getTransTypeSale() {
        return TRANS_TYPE_SALE;
    }

    public static String getTransTypeSettlement() {
        return TRANS_TYPE_SETTLEMENT;
    }

    public String getEcrIndicator() {
        return ecrIndicator;
    }

    public String getEcrVersionDate() {
        return ecrVersionDate;
    }

    public String getTransTypeIndicator() {
        return transTypeIndicator;
    }

    public String getTransType() {
        return transType;
    }

    public String getEsvcIndicator() {
        return esvcIndicator;
    }

    public String getHostId() {
        return hostId;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getCardSettleQty() {
        return cardSettleQty;
    }

    public String getTransAmount() {
        return transAmount;
    }

    public String getTransDate() {
        return transDate;
    }

    public String getTransTime() {
        return transTime;
    }

    public String getApprovalNo() {
        return approvalNo;
    }

    public String getWaveCardIndicator() {
        return waveCardIndicator;
    }

    public String getEcrResponseCode() {
        return ecrResponseCode;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public String getExpAmount() {
        return expAmount;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getRedeemIndicator() {
        return redeemIndicator;
    }

    public String getRdmPaidAmt() {
        return rdmPaidAmt;
    }

    public String getRdmPoint() {
        return rdmPoint;
    }

    public String getPointsOfBalance() {
        return pointsOfBalance;
    }

    public String getRedeemAmt() {
        return redeemAmt;
    }

    public String getInstallmentPeriod() {
        return installmentPeriod;
    }

    public String getDownPaymentAmount() {
        return downPaymentAmount;
    }

    public String getInstallmentPaymentAmount() {
        return installmentPaymentAmount;
    }

    public String getFormalityFee() {
        return formalityFee;
    }

    public String getCardType() {
        return cardType;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public String getStartTransType() {
        return startTransType;
    }

    public String getMpFlag() {
        return mpFlag;
    }

    public String getSpIssuserId() {
        return spIssuserId;
    }

    public String getOriginDate() {
        return originDate;
    }

    public String getOriginRrn() {
        return originRrn;
    }

    public String getPayItem() {
        return payItem;
    }

    public String getCardNoHashValue() {
        return cardNoHashValue;
    }

    public String getMpResponseCode() {
        return mpResponseCode;
    }

    public String getAsmAwardFlag() {
        return asmAwardFlag;
    }

    public String getMcpIndicator() {
        return mcpIndicator;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getReserved() {
        return reserved;
    }

    public String getHgData() {
        return hgData;
    }

    public static String getTransData(double amount, String date, String time,String esvcIndicator) {
        NCCCTransDataBean dataBean = new NCCCTransDataBean();
        dataBean.transAmount = dataBean.rightStart(FormatUtil.removeDot(amount) + "00", 12, "0");
        dataBean.transDate = date;
        dataBean.transTime = time;
        dataBean.esvcIndicator = esvcIndicator;
        return dataBean.toString();
    }

    /**
     * 退款電文
     *
     * @param amount     交易金額
     * @param date       交易日期
     * @param time       交易時間
     * @param approvalNo 授權碼
     * @return 組合好的 400 碼電文資料
     */
    public static String getRefundData(int amount, String date, String time, String approvalNo) {
        NCCCTransDataBean dataBean = new NCCCTransDataBean();
        dataBean.transAmount = dataBean.rightStart(amount + "00", 12, "0");
        dataBean.transType = "02"; // 交易別 02 為退貨
        dataBean.transDate = date;
        dataBean.transTime = time;
        int approvalNoLength = approvalNo.length();
        //左靠右補空白 9碼
        dataBean.approvalNo = approvalNo + getEmptyString(9 - approvalNoLength);
        return dataBean.toString();
    }

    /**
     * 交易查詢的data bean
     *
     * @param amount 前一筆交易金額
     * @param date   前一筆交易日期
     * @param time   前一筆交易時間
     * @return 組合好的 400 碼電文資料
     */
    public static String getSearchTransData(double amount, String date, String time,String esvcIndicator) {
        NCCCTransDataBean dataBean = new NCCCTransDataBean();
        dataBean.transType = "62"; // 交易別 62 為交易查詢
        dataBean.transAmount = dataBean.rightStart(FormatUtil.removeDot(amount) + "00", 12, "0");
        dataBean.transDate = date;
        dataBean.transTime = time;
        dataBean.esvcIndicator = esvcIndicator;
        dataBean.startTransType = "01"; // 01 為信用卡交易

        return dataBean.toString();
    }

    public static int parseAmount(String transAmount) {
        int stringLen = transAmount.length();
        transAmount = transAmount.substring(0, stringLen - 2);
        return parseInt(transAmount);
    }

    public static String getSettleData(String date, String time) {
        return "I" + getEmptyString(7) + TRANS_TYPE_SETTLEMENT + "N03" + getEmptyString(41)
                + date + time + getEmptyString(334);
    }

    public static NCCCTransDataBean generateRes(String data) {
        NCCCTransDataBean dataBean = new NCCCTransDataBean();
        if (data.length() >= 400) {
            dataBean.ecrIndicator = data.substring(0, 1);
            dataBean.ecrVersionDate = data.substring(1, 7);
            dataBean.transTypeIndicator = data.substring(7, 8);
            dataBean.transType = data.substring(8, 10);
            dataBean.esvcIndicator = data.substring(10, 11);
            dataBean.hostId = data.substring(11, 13);
            dataBean.receiptNo = data.substring(13, 19);
            dataBean.cardNo = data.substring(19, 38);
            dataBean.cardSettleQty = data.substring(38, 42);
            dataBean.transAmount = data.substring(42, 54);
            dataBean.transDate = data.substring(54, 60);
            dataBean.transTime = data.substring(60, 66);
            dataBean.approvalNo = data.substring(66, 75);
            dataBean.waveCardIndicator = data.substring(75, 76);
            dataBean.ecrResponseCode = data.substring(76, 80);
            dataBean.merchantId = data.substring(80, 95);
            dataBean.terminalId = data.substring(95, 103);
            dataBean.expAmount = data.substring(103, 115);
            dataBean.storeId = data.substring(115, 133);
            dataBean.redeemIndicator = data.substring(133, 134);
            dataBean.rdmPaidAmt = data.substring(134, 146);
            dataBean.rdmPoint = data.substring(146, 154);
            dataBean.pointsOfBalance = data.substring(154, 162);
            dataBean.redeemAmt = data.substring(162, 174);
            dataBean.installmentPeriod = data.substring(174, 176);
            dataBean.downPaymentAmount = data.substring(176, 188);
            dataBean.installmentPaymentAmount = data.substring(188, 200);
            dataBean.formalityFee = data.substring(200, 212);
            dataBean.cardType = data.substring(212, 214);
            dataBean.batchNo = data.substring(214, 220);
            dataBean.startTransType = data.substring(220, 222);
            dataBean.mpFlag = data.substring(222, 223);
            dataBean.spIssuserId = data.substring(223, 231);
            dataBean.originDate = data.substring(231, 239);
            dataBean.originRrn = data.substring(239, 251);
            dataBean.payItem = data.substring(251, 256);
            dataBean.cardNoHashValue = data.substring(256, 306);
            dataBean.mpResponseCode = data.substring(306, 312);
            dataBean.asmAwardFlag = data.substring(312, 313);
            dataBean.mcpIndicator = data.substring(313, 314);
            dataBean.bankCode = data.substring(314, 317);
            dataBean.reserved = data.substring(317, 322);
            dataBean.hgData = data.substring(322, 400);
        }

        return dataBean;
    }

    public static String getEmptyString(int len) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < len; i++) {
            str.append(" ");
        }

        return str.toString();
    }

    private String rightStart(String data, int digit, String defaultData) {
        StringBuilder str = new StringBuilder();
        if (data == null || data.length() == 0) {
            data = "";
        }
        int len = digit - data.length();
        for (int i = 0; i < len; i++) {
            str.append(defaultData);
        }
        return str + data;
    }

    private static boolean isInt(String str) {
        if (str == null || str.length() <= 0) {
            return false;
        }

        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static int parseInt(String value) {
        if (isInt(value)) {
            return Integer.parseInt(value);
        }
        return 0;
    }

    @Override
    public String toString() {
        return ecrIndicator +
                ecrVersionDate +
                transTypeIndicator +
                transType +
                esvcIndicator +
                hostId +
                receiptNo +
                cardNo +
                cardSettleQty +
                transAmount +
                transDate +
                transTime +
                approvalNo +
                waveCardIndicator +
                ecrResponseCode +
                merchantId +
                terminalId +
                expAmount +
                storeId +
                redeemIndicator +
                rdmPaidAmt +
                rdmPoint +
                pointsOfBalance +
                redeemAmt +
                installmentPeriod +
                downPaymentAmount +
                installmentPaymentAmount +
                formalityFee +
                cardType +
                batchNo +
                startTransType +
                mpFlag +
                spIssuserId +
                originDate +
                originRrn +
                payItem +
                cardNoHashValue +
                mpResponseCode +
                asmAwardFlag +
                mcpIndicator +
                bankCode +
                reserved +
                hgData;
    }
}
