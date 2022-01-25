package com.lafresh.kiosk.utils;


import com.lafresh.kiosk.printer.model.Product;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class EReceiptUtil {
    private final static int max_byte_length = 110;

    /**
     * 1. 發票字軌號碼 (10)：記錄發票完整 10 碼字軌號碼。
     * 2. 發票開立日期 (7)：記錄發票 3 碼民國年份 2 碼月份 2 碼日期共 7 碼。
     * 3. 隨機碼 (4)：記錄發票 4 碼隨機碼。
     * 4. 銷售額 (8)：記錄發票未稅總金額總計 8 碼，將金額轉換以十六進位方式記載。
     * 若營業人銷售系統無法順利將稅項分離計算，則以 00000000 記載，不足 8 碼左 補 0。
     * 5. 總計額 (8)：記錄發票含稅總金額總計 8 碼，將金額轉換以十六進位方式記載， 不足 8 碼左補 0。
     * 6. 買方統一編號 (8)：記錄發票買受人統一編號， 若買受人為一般消費者則以 00000000 記載。
     * 7. 賣方統一編號 (8)：記錄發票賣方統一編號。
     * 8. 加密驗證資訊 (24)：將發票字軌號碼 10 碼及隨機碼 4 碼以字串方式合併後使用 AES 加密
     * 並採用 Base64 編碼轉換， AES 所採用之金鑰產生方式請參考第叁、 肆章及「加解密 API 使用說明書」。
     * 以上欄位總計 77 碼。
     */
    public static String getQRCode77(String aesKey, String receiptNumber, String receiptDate,
                                     String randomNumber, String unTaxString, String totalString,
                                     String buyerGuiNumber, String sellerGuiNumber) {

        if (buyerGuiNumber.length() == 0) {
            buyerGuiNumber = "00000000";
        }

        String unTaxHexString = to8DigitsHexString(unTaxString);
        String totalHexString = to8DigitsHexString(totalString);
        String cryptoString = Base64Util.encodeAESBase64((aesKey == null || "".equals(aesKey)) ? "123" : aesKey, receiptNumber + randomNumber);
        return receiptNumber + receiptDate + randomNumber + unTaxHexString + totalHexString
                + buyerGuiNumber + sellerGuiNumber + cryptoString;
    }

    public static String getQrCodeData(String qrCode77, List<Product> productList) {
        int charSet = 1; // Big5=0 , UTF-8=1 , Base64=2
        int productCount = productList.size();
        String SEPARATOR = ":";
        String tenStar = "**********";
        StringBuilder data = new StringBuilder();
        data.append(qrCode77).append(SEPARATOR)
                .append(tenStar).append(SEPARATOR)
                .append(productCount).append(SEPARATOR)
                .append(productCount).append(SEPARATOR)
                .append(charSet).append(SEPARATOR);

        for (Product product : productList) {
            data.append(SEPARATOR).append(product.getName())
                    .append(SEPARATOR).append(product.getQuantity())
                    .append(SEPARATOR).append(product.getUnitPrice());
        }
        return data.toString();
    }

    public static String[] splitQRCodeData(String raw_data) {
        String[] return_data = new String[2];
        return_data[0] = "";
        return_data[1] = "**";
        int data_length = raw_data.length();
        for (int i = 0; i < data_length; i++) {
            String per_string = raw_data.substring(i, i + 1);
            int per_byte_length = per_string.getBytes(StandardCharsets.UTF_8).length;
            int left_byte_length = return_data[0].getBytes(StandardCharsets.UTF_8).length;
            int right_byte_length = return_data[1].getBytes(StandardCharsets.UTF_8).length;

            int position;
            if (left_byte_length + per_byte_length <= max_byte_length) {
                position = 0;
            } else if (right_byte_length + per_byte_length <= max_byte_length) {
                position = 1;
            } else {
                break;
            }

            return_data[position] = return_data[position] + per_string;
        }

        return_data[0] = return_data[0] + getQRCodePadding(return_data[0]);
        return_data[1] = return_data[1] + getQRCodePadding(return_data[1]);

        return return_data;
    }

    private static String getQRCodePadding(String data) {
        StringBuilder padding = new StringBuilder();
        int data_byte_length = data.getBytes(StandardCharsets.UTF_8).length;
        if (data_byte_length < 135) {
            int padding_length = 135 - data_byte_length;

            for (int i = 0; i < padding_length; i++) {
                padding.append(" ");
            }
            return padding.toString();
        }
        return padding.toString();
    }

    public static String to8DigitsHexString(String numberString) {
        int number = ComputationUtils.parseInt(numberString);
        String hexString = Integer.toHexString(number);
        return FormatUtil.leftPad(hexString, 8, "0");
    }

}
