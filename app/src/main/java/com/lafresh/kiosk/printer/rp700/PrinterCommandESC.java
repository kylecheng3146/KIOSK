package com.lafresh.kiosk.printer.rp700;

import android.util.Log;

import java.io.UnsupportedEncodingException;

class PrinterCommandESC extends PrinterCommand {
    private PrinterReceiver printerReceiver;

    PrinterCommandESC(PrinterReceiver printerReceiver) {
        super(printerReceiver);
        this.printerReceiver = printerReceiver;
    }

    @Override
    public void clearBuffer() {
        super.clearBuffer();
    }

    // 初始化整台機器,包括 clear buffer and reset
    @Override
    public void init() {
        byte[] prt_para = new byte[2];
        prt_para[0] = 27;
        prt_para[1] = 64;
        printerReceiver.action(prt_para);
    }

    @Override
    public void cutPaperEnd() {
        printFeedLine(240);
        printFeedLine(60);
        cutPaper(true);
    }

    @Override
    public void cutPaper(boolean flag) {
        byte[] prt_para = new byte[3];
        prt_para[0] = 0x1D;
        prt_para[1] = 0x56;
        prt_para[2] = (byte) 0x01;
        if (!flag) {
            prt_para[2] = (byte) 0x00;
        }
        printerReceiver.action(prt_para);
    }

    @Override
    public void printFeedLine(int lines) {
        byte[] prt_para = new byte[3];
        prt_para[0] = 27;
        prt_para[1] = 74;
        prt_para[2] = (byte) lines;
        printerReceiver.action(prt_para);
    }

    // 0=左,1=中,2=右
    @Override
    public void align(int para) {
        byte[] prt_para = new byte[3];
        prt_para[0] = 0x1B;
        prt_para[1] = 0x61;
        prt_para[2] = (byte) para;
        printerReceiver.action(prt_para);
    }

    @Override
    public void alignLeft() {
        align(0x00);
    }

    @Override
    public void alignCenter() {
        align(0x01);
    }

    @Override
    public void alignRight() {
        align(0x02);
    }

    // 0≤n≤7, 16≤n≤23, 32≤n≤39, 48≤n≤55, 64≤n≤ 71, 80≤n≤87, 96≤n≤103, 112≤n≤119；
    @Override
    public void setTextSize(int width_height) {
        byte[] prt_para = new byte[3];
        prt_para[0] = 29;
        prt_para[1] = 33;
        prt_para[2] = (byte) width_height;
        printerReceiver.action(prt_para);
    }

    @Override
    public void setTextType(int type) {
        byte[] prt_para = new byte[3];
        // 有些是28
        prt_para[0] = 27;
        prt_para[1] = 33;
        prt_para[2] = (byte) type;
        printerReceiver.action(prt_para);
    }

    @Override
    public void startPageMode() {
        byte[] prt_para = new byte[2];
        prt_para[0] = 0x1B;
        prt_para[1] = 0x4C;
        printerReceiver.action(prt_para);
    }

    // 0=左到右,1=下到上,2=右到左,3=上到下
    @Override
    public void setPageModeDirection(int para) {
        byte[] prt_para = new byte[3];
        prt_para[0] = 0x1B;
        prt_para[1] = 0x54;
        prt_para[2] = (byte) para;
        printerReceiver.action(prt_para);
    }

    @Override
    public void setPageModePrintArea(int destination_X_margin, int destination_X_tab, int destination_Y_margin, int destination_Y_tab) {
        byte[] prt_para = new byte[10];
        prt_para[0] = 27;
        prt_para[1] = 87;

        // ------------------------------------------------------

        // source x left margin(類似空白鍵)
        prt_para[2] = 0;

        // source x 類似Tab鍵,跳大範圍,1Tab = 256(0xff) left margin = 21 char
        prt_para[3] = 0;

        // source y top margin(類似空白鍵)
        prt_para[4] = 0;

        // source y 類似Tab鍵,跳大範圍,1Tab = 4行
        prt_para[5] = 0;

        // ------------------------------------------------------

        // destination x left margin
        prt_para[6] = (byte) destination_X_margin;

        // source x 類似Tab鍵,跳大範圍,1Tab = 256 left margin = 21 char
        prt_para[7] = (byte) destination_X_tab;

        // destination y top margin
        prt_para[8] = (byte) destination_Y_margin;

        // destination y 類似Tab鍵,跳大範圍,1Tab = 4行
        prt_para[9] = (byte) destination_Y_tab;

        printerReceiver.action(prt_para);
    }

    // left_margin 0x00,tab 0x00,1Tab = 255 left margin = 21 char
    @Override
    public void setX(int left_margin, int tab) {
        byte[] prt_para = new byte[4];
        prt_para[0] = 27;
        prt_para[1] = 36;
        prt_para[2] = (byte) left_margin;
        prt_para[3] = (byte) tab;
        printerReceiver.action(prt_para);
    }

    // top_margin 0~255,tab 0~255,1Tab = 4行
    @Override
    public void setY(int top_margin, int tab) {
        byte[] prt_para = new byte[4];
        prt_para[0] = 29;
        prt_para[1] = 36;
        prt_para[2] = (byte) top_margin;
        prt_para[3] = (byte) tab;
        printerReceiver.action(prt_para);
    }

    @Override
    public void endPageMode() {
        byte[] prt_para = new byte[1];
        prt_para[0] = 0x0C;
        printerReceiver.action(prt_para);
    }

    @Override
    public void initReceiptTitleFormat() {
        init();
        alignCenter();
        setTextType(0);
        setTextSize(17);
    }

    @Override
    public void initReceiptDetailsTitleFormat() {
        init();
        alignCenter();
        setTextSize(1);
    }

    @Override
    public void initReceiptLargeFormat() {
        init();
        alignLeft();
        setTextSize(17);
    }

    @Override
    public void initReceiptProductFormat() {
        init();
        alignLeft();
        setTextType(0);
        setTextSize(0);
    }

    @Override
    public void initReceiptDetailsFormat() {
        init();
        alignLeft();
        setTextType(0);
        setTextSize(0);
    }

    @Override
    public void printLogo() {
        byte[] prt_para = new byte[4];
        prt_para[0] = 0x1C;
        prt_para[1] = 0x70;
        prt_para[2] = 0x01;
        prt_para[3] = 0x30;
        printerReceiver.action(prt_para);
    }

    //之後一定要printFeedLine
    @Override
    public void printString(String data) {
        if (data == null) {
            data = "";
        }
        try {

            switch (getLanguageType()) {
                case PrinterCommand.TRADITIONAL_CHINESE:
                    printerReceiver.action(data.getBytes(PrinterCommand.TRADITIONAL_CHINESE_CHARSET));
                    break;
                case PrinterCommand.SIMPLE_CHINESE:
                    printerReceiver.action(data.getBytes(PrinterCommand.SIMPLE_CHINESE_CHARSET));
                    break;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void intoUserSettingMode() {
        byte[] prt_para = new byte[8];
        prt_para[0] = 0x1D;
        prt_para[1] = 0x28;
        prt_para[2] = 0x45;
        prt_para[3] = 0x03;
        prt_para[4] = 0x00;
        prt_para[5] = 0x01;
        prt_para[6] = 0x49;
        prt_para[7] = 0x4e;
        printerReceiver.action(prt_para);
    }

    // 設定發票紙(Receipt已付款)
    @Override
    public void setReceiptPaper() {
        byte[] prt_para = new byte[9];
        prt_para[0] = 0x1D;
        prt_para[1] = 0x28;
        prt_para[2] = 0x45;
        prt_para[3] = 0x04;
        prt_para[4] = 0x00;
        prt_para[5] = 0x05;
        prt_para[6] = 0x03;
        // 紙張大小 發票=3(58mm),商品收據=6(80mm)
        prt_para[7] = 0x03;
        prt_para[8] = 0x00;
        printerReceiver.action(prt_para);
    }

    // 設定收據紙(Invoice未付款)
    @Override
    public void setProductInvoicePaper() {
        byte[] prt_para = new byte[9];
        prt_para[0] = 0x1D;
        prt_para[1] = 0x28;
        prt_para[2] = 0x45;
        prt_para[3] = 0x04;
        prt_para[4] = 0x00;
        prt_para[5] = 0x05;
        prt_para[6] = 0x03;
        // 紙張大小 發票=3(58mm),商品收據=6(80mm)
        prt_para[7] = 0x06;
        prt_para[8] = 0x00;
        printerReceiver.action(prt_para);
    }

    @Override
    public void endUserSettingMode() {
        byte[] prt_para = new byte[9];
        prt_para[0] = 0x1D;
        prt_para[1] = 0x28;
        prt_para[2] = 0x45;
        prt_para[3] = 0x04;
        prt_para[4] = 0x00;
        prt_para[5] = 0x02;
        prt_para[6] = 0x4f;
        prt_para[7] = 0x55;
        prt_para[8] = 0x54;
        printerReceiver.action(prt_para);
    }

    /**
     * @param width 2~6,default 3
     */
    @Override
    public void setBarCodeWidth(int width) {
        byte[] prt_para = new byte[3];
        prt_para[0] = 29;
        prt_para[1] = 119;
        prt_para[2] = (byte) width;
        printerReceiver.action(prt_para);
    }

    /**
     * @param height 1~255,default 162
     */
    @Override
    public void setBarCodeHeight(int height) {
        byte[] prt_para = new byte[3];
        prt_para[0] = 29;
        prt_para[1] = 104;
        prt_para[2] = (byte) height;
        printerReceiver.action(prt_para);
    }

    // 0x00~0x03
    // 0 = Not printed
    // 1 = Above the bar code
    // 2 = Below the bar code
    // 3 = Both above and below the bar code
    @Override
    public void setBarCodeTextPosition(int position_type) {
        byte[] prt_para = new byte[3];
        prt_para[0] = 0x1D;
        prt_para[1] = 0x48;
        prt_para[2] = (byte) position_type;
        printerReceiver.action(prt_para);
    }

    @Override
    public void printBarCode39(String number) {
        setBarCodeHeight(46);
        setBarCodeWidth(1);
        setBarCodeTextPosition(0);

        printFeedLine(20);
        byte[] prt_para = new byte[4];
        prt_para[0] = 29;
        prt_para[1] = 107;
        prt_para[2] = 69;

        byte[] number_byte = null;
        try {
            number_byte = number.getBytes(PrinterCommand.BAR_CODE_CHARSET);
            prt_para[3] = (byte) number_byte.length;

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        byte[] merge = appendByteData(prt_para, number_byte);
        printerReceiver.action(merge);
    }

    /**
     * @param size 1 <= size <= 16, default=3
     */
    @Override
    public void setQRCodeSize(int size) {
        if (size < 1 || size > 16) {
            size = 3;
        }
        byte[] prt_para = new byte[8];
        prt_para[0] = 29;
        prt_para[1] = 40;
        prt_para[2] = 107;
        prt_para[3] = 3;
        prt_para[4] = 0;
        prt_para[5] = 49;
        prt_para[6] = 67;
        // dot size
        prt_para[7] = (byte) size;
        printerReceiver.action(prt_para);
    }

    /**
     * @param level 48=L, 49=M, 50=Q, 51=H
     */
    @Override
    public void setErrorCorrectionLevel(int level) {
        if (level < 48 || level > 51) {
            level = 48;
        }
        byte[] prt_para = new byte[8];
        prt_para[0] = 29;
        prt_para[1] = 40;
        prt_para[2] = 107;
        prt_para[3] = 3;
        prt_para[4] = 0;
        prt_para[5] = 49;
        prt_para[6] = 69;
        prt_para[7] = (byte) level;
        printerReceiver.action(prt_para);
    }

    @Override
    public void storeDataInQRGraph(String data) {
        byte[] prt_para = new byte[8];
        prt_para[0] = 29;
        prt_para[1] = 40;
        prt_para[2] = 107;
        prt_para[3] = 0;
        prt_para[4] = 0;
        prt_para[5] = 49;
        prt_para[6] = 80;
        prt_para[7] = 48;
        byte[] merge = null;
        try {
            byte[] data_byte = data.getBytes(PrinterCommand.QR_CODE_CHARSET);
            prt_para[3] = (byte) ((data_byte.length + 3) % 256);
            prt_para[4] = (byte) ((data_byte.length + 3) / 256);
            merge = appendByteData(prt_para, data_byte);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        printerReceiver.action(merge);
    }

    @Override
    public void putGraphToArea() {
        byte[] prt_para = new byte[8];
        prt_para[0] = 29;
        prt_para[1] = 40;
        prt_para[2] = 107;
        prt_para[3] = 3;
        prt_para[4] = 0;
        prt_para[5] = 49;
        prt_para[6] = 81;
        prt_para[7] = 48;
        printerReceiver.action(prt_para);
    }

    @Override
    public void printQRCode(String data, QrCodeSetting setting) {
        init();
        startPageMode();
        setPageModePrintArea(0, 2, 0, 2);
        setPageModeDirection(0x00);
        setQRCodeSize(setting.getSize());
        setErrorCorrectionLevel(setting.getErrorCorrectionLevel());

        setY(setting.getTopMargin(), setting.getTopTab());
        setX(setting.getLeftMargin(), setting.getLeftTab());
        storeDataInQRGraph(data);
        putGraphToArea();

        endPageMode();
    }

    @Override
    public void printDoubleQRCode(String left_data, String right_data) {
        try {

            int left_length = left_data.getBytes(PrinterCommand.QR_CODE_CHARSET).length;
            int right_length = right_data.getBytes(PrinterCommand.QR_CODE_CHARSET).length;
            if (left_length > right_length) {
                int padding_length = left_length - right_length;
                StringBuilder right_dataBuilder = new StringBuilder(right_data);
                for (int i = 0; i < padding_length; i++) {
                    right_dataBuilder.append(" ");
                }
                right_data = right_dataBuilder.toString();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        init();
        startPageMode();
        setPageModePrintArea(0, 2, 120, 1);
        setPageModeDirection(0x00);
        setQRCodeSize(3);
        setErrorCorrectionLevel(48);

        // left QR Code
        setY(20, 0);
        setX(0, 0);
        storeDataInQRGraph(left_data);
        putGraphToArea();

        // right QR Code
        setY(20, 0);
        setX(230, 0);
        storeDataInQRGraph(right_data);
        putGraphToArea();

        endPageMode();
    }

    @Override
    public void printDoubleQRCode(String data, int one_qr_code_max_byte_length) {
        try {
            String[] data_array = splitQRCodeData(data, one_qr_code_max_byte_length);
            init();

            startPageMode();
            setPageModePrintArea(0, 2, 50, 1);
            setPageModeDirection(0x00);
            setQRCodeSize(3);
            setErrorCorrectionLevel(48);

            // left QR Code
            setY(20, 0);
            setX(20, 0);
            Log.i("QRCode_1", "{" + data_array[0] + "}");
            Log.i("QRCode_1_length", "{" + data_array[0].getBytes(PrinterCommand.QR_CODE_CHARSET).length + "}");
            storeDataInQRGraph(data_array[0]);
            putGraphToArea();

            // right QR Code
            setY(20, 0);
            setX(210, 0);
            Log.i("QRCode_2", "{" + data_array[1] + "}");
            Log.i("QRCode_2_length", "{" + data_array[1].getBytes(PrinterCommand.QR_CODE_CHARSET).length + "}");
            storeDataInQRGraph(data_array[1]);
            putGraphToArea();

            endPageMode();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openCashDrawer() {
        byte[] prt_para = new byte[5];
        prt_para[0] = 27;
        prt_para[1] = 112;
        prt_para[2] = 0;
        prt_para[3] = 60;
        prt_para[4] = (byte) 255;
        printerReceiver.action(prt_para);
    }

    @Override
    public void setMargin(int margin) {
        super.setMargin(margin);
        byte[] prt_para = new byte[4];
        prt_para[0] = 29;
        prt_para[1] = 76;
        prt_para[2] = (byte) margin;
        prt_para[3] = 0;
        printerReceiver.action(prt_para);
    }
}
