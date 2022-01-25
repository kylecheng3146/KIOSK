package com.lafresh.kiosk.creditcardlib;

import com.lafresh.kiosk.creditcardlib.model.Device;
import com.lafresh.kiosk.creditcardlib.nccc.NCCCSession;


public class SessionManager {
    public static Session createSession(int deviceCode) {
        if (deviceCode == Device.NCCC) {
            return new NCCCSession();
        }

        //default
        return new NCCCSession();
    }
}
