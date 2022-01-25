package com.lafresh.kiosk.easycard;

public class ErrorCode {
    public static final int SUCCESS = 666;
    public static final int BROKEN = -111;
    public static final int NEED_RETRY = 333;
    public static final int FAIL = 444;

    public static int getStatus(String t3901, String t3904) {
        if (t3901 == null) {
            return FAIL;
        }

        if ("0".equals(t3901)) {
            return SUCCESS;
        }

        if ("-125".equals(t3901)) {
            return NEED_RETRY;
        }

        if ("-119".equals(t3901)) {
            if (t3904 == null) {
                return FAIL;
            }

            if (t3904.startsWith("62")) {
                return NEED_RETRY;
            }

            if (t3904.startsWith("66")) {
                return BROKEN;
            }
        }

        return FAIL;
    }

    public static String getMessage(String t3901, String t3904, boolean isPaying) {
        if (t3901 == null) {
            return "請點選返回，結束交易";
        }
        String resultCode = "(" + t3901 + ")";
        String errorCode = "(" + t3904 + ")\n";
        if (t3904 == null) {
            errorCode = "";
        }
        String errorMsg = "";

        if ("0".equals(t3901)) {
            return isPaying ? "付款成功，請稍候" : "感應成功，請稍候";
        }

        if ("-123".equals(t3901)) {
            errorMsg = "餘額不足";
            return resultCode + errorCode + errorMsg;
        }

        if ("-125".equals(t3901)) {
            return resultCode + "請確認相同卡片正確置放，重新進行感應";
        }

        if ("-132".equals(t3901)) {
            return resultCode + errorCode + "黑名單卡，API 鎖卡";
        }

        if ("-119".equals(t3901)) {
            if (t3904 == null) {
                errorMsg = "t3904錯誤，請點選返回，結束交易";
                return errorCode + errorMsg;
            }

            if (t3904.startsWith("66")) {
                errorMsg = "請暫停使用悠遊卡交易，並請進行報修換機流程";
                return errorCode + errorMsg;
            }

            switch (t3904) {
                case "6103":
                    errorMsg = "卡片已失效,請洽悠遊卡公司";
                    break;
                case "6108":
                    errorMsg = "卡片已過期";
                    break;
                case "6109":
                    errorMsg = "卡片已鎖卡,請洽悠遊卡公司";
                    break;
                case "6201":
                    errorMsg = "找不到卡片,請重新感應";
                    break;
                case "6202":
                    errorMsg = "讀卡失敗,請重新感應";
                    break;
                case "6204":
                    errorMsg = "一張卡以上重複感應";
                    break;
                case "6304":
                case "6305":
                    errorMsg = "設備重新Sing On中，請稍等";
                    break;
                case "6402":
                    errorMsg = "交易金額超過額度";
                    break;
                case "6403":
                    errorMsg = "餘額不足";
                    break;
                case "6404":
                    errorMsg = "卡號錯誤";
                    break;
                case "6406":
                    errorMsg = "拒絕交易,請洽悠遊卡公司";
                    break;
                case "640C":
                    errorMsg = "累計小額扣款(購貨)金額超出日限額";
                    break;
                case "640D":
                    errorMsg = "單次小額扣款(購貨)金額超出次限額";
                    break;
                case "640E":
                    errorMsg = "卡片餘額異常, 卡片餘額異常";
                    break;
                case "6410":
                    errorMsg = "票卡不適用(非普通卡)";
                    break;
                case "6418":
                    errorMsg = "票卡於此通路 限制使用";
                    break;
                case "6419":
                    errorMsg = "票卡狀態不適用此交易";
                    break;
                default:
                    errorMsg = "請點選返回，結束交易";
                    break;
            }

            return resultCode + errorCode + errorMsg;
        }

        return resultCode + "請點選返回，結束交易";
    }
}
