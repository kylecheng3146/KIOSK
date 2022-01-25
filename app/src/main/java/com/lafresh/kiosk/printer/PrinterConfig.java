package com.lafresh.kiosk.printer;

public class PrinterConfig {
    private String printerName;
    private String connectionType;
    private int printReceiptNumber;

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public int getPrintReceiptNumber() {
        return printReceiptNumber;
    }

    public void setPrintReceiptNumber(int printReceiptNumber) {
        this.printReceiptNumber = printReceiptNumber;
    }
}
