package com.lafresh.kiosk.nccc;

public class NCCCCode {

    public static String getResMessage(String resCode) {
        switch (resCode) {
            case "0000":
                return "交易成功";

            case "0001":
                return "交易失敗(0001)\n" +
                        "Error(主機拒絕或是卡片拒絕)";

            case "0002":
                return "請聯絡銀行(0002)";

            case "0003":
                return "交易逾時(0003)";

            case "0004":
                return "操作錯誤(0004)";

            case "0005":
                return "通訊失敗(0005)";

            case "0006":
                return "使用者終止交易(0006)";

            case "0009":
                return "非參加機構卡片(0009)";

            case "0010":
                return "電文錯誤(0010)";

            case "0018":
                return "請重新感應卡片(0018)";

            case "1001":
                return "信用卡及電票皆結帳失敗(1001)";

            case "1003":
                return "信用卡結帳失敗，電票結帳成功(1003)";

            default:
                return "不明錯誤";
        }
    }

    public static String getCardTypeName(String cardType) {
        switch (cardType) {
            case "01":
                return "自有品牌卡";

            case "02":
                return "VISA";

            case "03":
                return "MASTER";

            case "04":
                return "JCB";

            case "05":
                return "AE";

            case "06":
                return "CUP";

            case "07":
                return "DISCOVER";

            case "08":
                return "SMART PAY";

            case "11":
                return "ECC(悠遊卡)";

            case "12":
                return "iPASS(一卡通)";

            case "13":
                return "iCash(愛金卡)";

            case "14":
                return "HappyCash(有錢卡)";

            case "15":
                return "社員卡";

            default:
                return "";
        }
    }
}
