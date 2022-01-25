package com.lafresh.kiosk.printer.rp700;

import java.io.UnsupportedEncodingException;

/**
 * 數值設定多以10進位
 * 參數設定(對齊)多以16進位
 */

abstract public class PrinterCommand {
    public static final String TRADITIONAL_CHINESE_CHARSET = "BIG5";
    public static final String SIMPLE_CHINESE_CHARSET = "GBK";
    public static final String BAR_CODE_CHARSET = "UTF-8";
    public static final String QR_CODE_CHARSET = "UTF-8";

    public static final int TRADITIONAL_CHINESE = 0;
    public static final int SIMPLE_CHINESE = 1;

    private PrinterReceiver printerReceiver;
    private int languageType = TRADITIONAL_CHINESE;

    public PrinterCommand(PrinterReceiver printerReceiver) {
        this.printerReceiver = printerReceiver;
    }

    public void setLanguageType(int languageType) {
        this.languageType = languageType;
    }

    public int getLanguageType() {
        return languageType;
    }

    public void sendSignal(byte[] bytes) {
        printerReceiver.action(bytes);
    }

    byte[] appendByteData(byte[] raw, byte[] append_data) {
        byte[] merge = new byte[raw.length + append_data.length];
        System.arraycopy(raw, 0, merge, 0, raw.length);
        System.arraycopy(append_data, 0, merge, raw.length, append_data.length);
        return merge;
    }

    protected String[] splitQRCodeData(String raw_data, int one_qr_code_max_byte_length) {

        String[] return_data = new String[2];
        return_data[0] = "";
        return_data[1] = "**";
        try {
            int data_length = raw_data.length();
            for (int i = 0; i < data_length; i++) {
                String per_string = raw_data.substring(i, i + 1);
                int per_byte_length = per_string.getBytes(PrinterCommand.QR_CODE_CHARSET).length;
                int left_byte_length = return_data[0].getBytes(PrinterCommand.QR_CODE_CHARSET).length;

                int position;
                if (left_byte_length + per_byte_length <= one_qr_code_max_byte_length) {
                    position = 0;
                } else {
                    position = 1;
                }

                return_data[position] = return_data[position] + per_string;
            }
            return_data[0] = return_data[0] + getQRCodePadding(return_data[0], one_qr_code_max_byte_length);
            return_data[1] = return_data[1] + getQRCodePadding(return_data[1], one_qr_code_max_byte_length);

            for (int i = 0; i < return_data.length; i++) {
                int byte_length = return_data[i].getBytes(PrinterCommand.QR_CODE_CHARSET).length;
                if (byte_length < 135) {
                    byte_length = 135 - byte_length;

                    for (int j = 0; j < byte_length; j++) {
                        return_data[i] = return_data[i] + " ";
                    }
                }

            }

            return return_data;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return return_data;
    }

    protected String getQRCodePadding(String data, int max_byte_length) {
        String padding = "";
        try {
            int data_byte_length = data.getBytes(PrinterCommand.QR_CODE_CHARSET).length;
            if (data_byte_length < max_byte_length) {
                int padding_length = max_byte_length - data_byte_length;

                for (int i = 0; i < padding_length; i++) {
                    padding = padding + " ";
                }
                return padding;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return padding;
    }

    //設定列印範圍,標籤機用由子類別覆寫留著空方法
    //width,height (mm)
    public void setAreaSize(String width, String height) {

    }

    public void clearBuffer() {

    }

    public void init() {

    }

    public void cutPaperEnd() {

    }

    public void cutPaperEnd(String value) {

    }

    public void cutPaper(boolean flag) {

    }

    public void printFeedLine(int lines) {

    }

    public void align(int para) {

    }

    public void alignLeft() {

    }

    public void alignCenter() {

    }

    public void alignRight() {

    }


    public void setTextSize(int width_height) {

    }

    public void setTextType(int type) {

    }

    public void startPageMode() {

    }


    public void setPageModeDirection(int para) {

    }

    public void setPageModePrintArea(int destination_X_margin, int destination_X_tab, int destination_Y_margin, int destination_Y_tab) {

    }

    public void setX(int left_margin, int tab) {

    }


    public void setY(int top_margin, int tab) {

    }

    public void endPageMode() {

    }

    public void initReceiptTitleFormat() {

    }

    public void initReceiptDetailsTitleFormat() {

    }

    public void initReceiptLargeFormat() {

    }

    public void initReceiptProductFormat() {

    }


    public void initReceiptDetailsFormat() {

    }

    public void printString(String data) {

    }

    public void printLogo() {
    }

    public void intoUserSettingMode() {

    }

    public void setReceiptPaper() {

    }

    public void setProductInvoicePaper() {

    }

    public void endUserSettingMode() {

    }

    public void setBarCodeWidth(int width) {

    }

    public void setBarCodeHeight(int height) {

    }

    public void setBarCodeTextPosition(int position_type) {

    }

    public void printBarCode39(String number) {

    }

    public void setQRCodeSize(int size) {

    }

    public void setErrorCorrectionLevel(int level) {

    }

    public void storeDataInQRGraph(String data) {

    }

    public void putGraphToArea() {

    }

    public void printQRCode(String data, QrCodeSetting setting) {

    }

    public void printDoubleQRCode(String left_data, String right_data) {


    }

    public void printDoubleQRCode(String data, int one_qr_code_max_byte_length) {

    }

    public void openCashDrawer() {

    }

    public void setSimpleChinese() {

    }

    public void setTranditionalChinese() {

    }

    public void setMargin(int margin) {

    }

}
