package com.lafresh.kiosk.printer.rp700;

class PrinterReceiverRawUsb implements PrinterReceiver {

    private HardwareConnectionHelper connectionHelper;

    PrinterReceiverRawUsb(HardwareConnectionHelper connectionHelper) {
        this.connectionHelper = connectionHelper;
    }

    @Override
    public boolean action(byte[] data) {
        return connectionHelper.writeDataInUsb(data, 3000);
    }

}
