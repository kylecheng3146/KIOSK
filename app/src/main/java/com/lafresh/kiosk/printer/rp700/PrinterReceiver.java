package com.lafresh.kiosk.printer.rp700;

/**
 * 規格請看AllNotice
 */

interface PrinterReceiver {
    boolean action(byte[] data);
}
