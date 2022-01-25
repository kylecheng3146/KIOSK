package com.lafresh.kiosk.printer;

public class InitialPrinterAction implements PrinterAction {
    private PrinterConfig[] printerConfigs;

    public InitialPrinterAction(PrinterConfig[] configs) {
        printerConfigs = configs;
    }

    @Override
    public void doAction() {

    }

    @Override
    public KioskPrinter[] findAvailablePrinter(KioskPrinter[] printers) {
        return new KioskPrinter[0];
    }
}
