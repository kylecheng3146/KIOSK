package com.lafresh.kiosk;

import android.util.Base64;

import com.lafresh.kiosk.httprequest.model.Invoice;
import com.lafresh.kiosk.httprequest.model.WebOrder01;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class Tool {
    private final static int max_byte_length = 110;

    public static String encodeAESBase64(String key, String data) {
        String return_data = "";
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            return_data = encodeBase64(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return return_data;
    }

    static String encodeBase64(byte[] bytes) {
        return new String(Base64.encode(bytes, Base64.NO_WRAP), StandardCharsets.UTF_8);
    }

    public static String decodeAESBase64(String key, String data) {
        String return_data = "";
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return_data = new String(cipher.doFinal(decodeBase64Byte(data)));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return return_data;
    }

    static byte[] decodeBase64Byte(String message) {
        return Base64.decode(message, Base64.DEFAULT);
    }

    public static String getQRCode77() {

        //1050330

        //String date = getRocYearMonth(Tool.getNowTimeToDay("")) + Tool.getNowDay();
        String date = getYMD_TW();


        //double tax_rate = ComputationUtils.parseDouble(setting.taxrate);//稅率 台灣都是 0.05
        double tax_rate = 0.05d;//稅率 台灣都是 0.05

        //double tot_sales = ComputationUtils.parseDouble(weborder00.tot_sales);
        //double tot_sales = Bill.fromServer.getTotal();

        //未稅
        /*
        double un_tax_sales = tot_sales / (1 + tax_rate);
        String un_tax_string = Integer.toHexString(processDecimalInt(un_tax_sales)).toUpperCase();
        int size = 8 - un_tax_string.length();
        for (int i = 0; i < size; i++) {
            un_tax_string = "0" + un_tax_string;
        }
        */
        String un_tax_string = Bill.fromServer.getUnTax_Hex();

        /*
        String sales_string = Integer.toHexString(processDecimalInt(tot_sales)).toUpperCase();
        size = 8 - sales_string.length();
        for (int i = 0; i < size; i++) {
            sales_string = "0" + sales_string;
        }
        */
        String sales_string = Bill.fromServer.getTotalHex();

        String cust_code = Bill.fromServer.getCustCode();
        if (cust_code.length() == 0) {
            cust_code = "00000000";
        }

        Invoice invoice = Bill.fromServer.getOrderResponse().getInvoice();
        String companyCode = invoice.getGUI_number();
        String encode_data = Tool.encodeAESBase64(Config.invoice_aes_key, invoice.getInv_no_b() + invoice.getRandom_no());

        return invoice.getInv_no_b() + date + invoice.getRandom_no() + un_tax_string + sales_string + cust_code + companyCode + encode_data;
    }

    public static String getQRCodeData(List<WebOrder01> webOrder01List, String QRCode77) {
        String qr_code_data = "";
        String sub_information = "**********";
        int qr_code_max_byte = max_byte_length * 2 - 2;//扣掉第二個QR Code 起始 **
        int product_print_quantity = 0;
        int product_total_item = webOrder01List.size();
        int char_set = 1; // Big5=0 , UTF-8=1 , Base64=2

        String product_information = "";
        for (WebOrder01 webOrder01 : webOrder01List) {
            ++product_print_quantity;

            String not_product_information = QRCode77 + ":" + sub_information + ":" + product_print_quantity + ":" + product_total_item + ":" + char_set;
            int not_product_information_length = not_product_information.getBytes(StandardCharsets.UTF_8).length;

            product_information = product_information + ":" + webOrder01.getProd_name1() + ":" + webOrder01.getQty() + ":" + webOrder01.getSale_price();//品茗：數量：單價

            int product_length = product_information.getBytes(StandardCharsets.UTF_8).length;

            int qr_code_now_length = not_product_information_length + product_length;

            if (qr_code_now_length > qr_code_max_byte) {
                break;
            }

            qr_code_data = not_product_information + product_information;
        }
        return qr_code_data;
    }

    static String[] splitQRCodeData(String raw_data) {
        String[] return_data = new String[2];
        return_data[0] = "";
        return_data[1] = "**";
        int data_length = raw_data.length();
        for (int i = 0; i < data_length; i++) {
            String per_string = raw_data.substring(i, i + 1);
            int per_byte_length = per_string.getBytes(StandardCharsets.UTF_8).length;
            int left_byte_length = return_data[0].getBytes(StandardCharsets.UTF_8).length;

            int position;
            if (left_byte_length + per_byte_length <= max_byte_length) {
                position = 0;
            } else {
                position = 1;
            }

            return_data[position] = return_data[position] + per_string;
        }
        return_data[0] = return_data[0] + getQRCodePadding(return_data[0], max_byte_length);
        return_data[1] = return_data[1] + getQRCodePadding(return_data[1], max_byte_length);

        for (int i = 0; i < return_data.length; i++) {
            int byte_length = return_data[i].getBytes(StandardCharsets.UTF_8).length;
            if (byte_length < 135) {
                byte_length = 135 - byte_length;

                for (int j = 0; j < byte_length; j++) {
                    return_data[i] = return_data[i] + " ";
                }
            }
        }

        return return_data;
    }

    static private String getQRCodePadding(String data, int max_byte_length) {
        String padding = "";
        int data_byte_length = data.getBytes(StandardCharsets.UTF_8).length;
        if (data_byte_length < max_byte_length) {
            int padding_length = max_byte_length - data_byte_length;

            for (int i = 0; i < padding_length; i++) {
                padding = padding + " ";
            }
            return padding;
        }
        return padding;
    }

    public static String getYMD_TW() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String Y = year - 1911 + "";
        String M = month <= 9 ? "0" + month : month + "";
        String D = day <= 9 ? "0" + day : day + "";

        return Y + M + D;
    }
}
