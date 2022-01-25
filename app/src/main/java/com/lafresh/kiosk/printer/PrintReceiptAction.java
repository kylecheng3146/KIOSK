package com.lafresh.kiosk.printer;

public class PrintReceiptAction implements PrinterAction {

    public PrintReceiptAction() {
        //set Print Data
    }

    @Override
    public void doAction() {
        KioskPrinter[] availablePrinters = findAvailablePrinter(new KioskPrinter[0]);
        for(KioskPrinter printer: availablePrinters){
//            printer.printReceipt();
        }
    }

    @Override
    public KioskPrinter[] findAvailablePrinter(KioskPrinter[] printers) {
        for (KioskPrinter printer : printers){
            //get printer config
        }
        return new KioskPrinter[0];
    }
}
