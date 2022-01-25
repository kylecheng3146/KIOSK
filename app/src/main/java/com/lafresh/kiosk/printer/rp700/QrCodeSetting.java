package com.lafresh.kiosk.printer.rp700;

public class QrCodeSetting {
    public static final int QR_CODE_ERROR_CORRECTION_LEVEL_L_7PERCENT = 48;
    public static final int QR_CODE_ERROR_CORRECTION_LEVEL_M_15PERCENT = 49;
    public static final int QR_CODE_ERROR_CORRECTION_LEVEL_Q_25PERCENT = 50;
    public static final int QR_CODE_ERROR_CORRECTION_LEVEL_H_30PERCENT = 51;
    private int errorCorrectionLevel;
    private int size;
    private int printAreaXMargin;
    private int printAreaXTab;
    private int printAreaYMargin;
    private int printAreaYTab;
    private int leftMargin;
    private int leftTab;
    private int topMargin;
    private int topTab;

    public static QrCodeSetting getDefaultSetting(){
        QrCodeSetting setting = new QrCodeSetting();
        setting.setErrorCorrectionLevel(QR_CODE_ERROR_CORRECTION_LEVEL_L_7PERCENT);
        setting.setSize(4);
        setting.setLeftMargin(100);
        setting.setTopMargin(20);
        return setting;
    }

    public int getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    public void setErrorCorrectionLevel(int errorCorrectionLevel) {
        this.errorCorrectionLevel = errorCorrectionLevel;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPrintAreaXMargin() {
        return printAreaXMargin;
    }

    public void setPrintAreaXMargin(int printAreaXMargin) {
        this.printAreaXMargin = printAreaXMargin;
    }

    public int getPrintAreaXTab() {
        return printAreaXTab;
    }

    public void setPrintAreaXTab(int printAreaXTab) {
        this.printAreaXTab = printAreaXTab;
    }

    public int getPrintAreaYMargin() {
        return printAreaYMargin;
    }

    public void setPrintAreaYMargin(int printAreaYMargin) {
        this.printAreaYMargin = printAreaYMargin;
    }

    public int getPrintAreaYTab() {
        return printAreaYTab;
    }

    public void setPrintAreaYTab(int printAreaYTab) {
        this.printAreaYTab = printAreaYTab;
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public int getLeftTab() {
        return leftTab;
    }

    public void setLeftTab(int leftTab) {
        this.leftTab = leftTab;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    public int getTopTab() {
        return topTab;
    }

    public void setTopTab(int topTab) {
        this.topTab = topTab;
    }
}
