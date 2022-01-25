package com.lafresh.kiosk.httprequest;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class AddSiteLogApiRequest extends ApiRequest {
    private String authkey;
    private String acckey;
    private String log_type;
    private String log_code;
    private String status;
    private String msg;

    public AddSiteLogApiRequest(String authkey, String acckey, String msg) {
        this.authkey = authkey;
        this.acckey = acckey;
        this.msg = msg;
        log_code = "kiosk";
        log_type = log_code;
        status = log_code;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(BASIC_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "add_sitelog")
                .add("authkey", authkey)
                .add("acckey", acckey)
                .add("log_type", log_type)
                .add("log_code", log_code)
                .add("status", status)
                .add("msg", msg);
        return builder.build();
    }
}
