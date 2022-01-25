package com.lafresh.kiosk.printer;

public interface PrinterAction {

    void doAction();

    KioskPrinter[] findAvailablePrinter(KioskPrinter[] printers);
}
