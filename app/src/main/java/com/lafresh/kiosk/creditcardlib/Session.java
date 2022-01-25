package com.lafresh.kiosk.creditcardlib;

import com.lafresh.kiosk.creditcardlib.model.Request;
import com.lafresh.kiosk.creditcardlib.model.Response;

public interface Session {
    public Response execute(Request request);
}
