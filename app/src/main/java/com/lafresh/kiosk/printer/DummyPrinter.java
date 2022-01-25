package com.lafresh.kiosk.printer;

import android.content.Context;

public class DummyPrinter extends KioskPrinter {

    @Override
    public void releaseAll(Context context) {
        releasePrinter();
    }

    @Override
    public void requestPrinterPermission(Context context) {
        //do nothing
    }

    @Override
    public boolean disconnect() {
        return true;
    }

    @Override
    public boolean isConnect() {
        return true;
    }
}
