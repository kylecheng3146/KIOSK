package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Config;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class CheckKioskVerApiRequest extends ApiRequest {

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(BASIC_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "check_kiosk_ver")
                .add("authkey", Config.authKey);
        RequestBody requestBody = builder.build();
        return requestBody;
    }

    public static class Response {
        private int code;
        private String message;
        private String kiosk_ver;
        private String kiosk_apk;//fileName
        private String kiosk_apk_size;//mb || kb
        private String kiosk_test_apk;//fileName
        private String kiosk_test_apk_size;//mb || kb


        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getKiosk_ver() {
            return kiosk_ver;
        }

        public void setKiosk_ver(String kiosk_ver) {
            this.kiosk_ver = kiosk_ver;
        }

        public String getKiosk_apk() {
            return kiosk_apk;
        }

        public void setKiosk_apk(String kiosk_apk) {
            this.kiosk_apk = kiosk_apk;
        }

        public String getKiosk_apk_size() {
            return kiosk_apk_size;
        }

        public void setKiosk_apk_size(String kiosk_apk_size) {
            this.kiosk_apk_size = kiosk_apk_size;
        }

        public String getKiosk_test_apk() {
            return kiosk_test_apk;
        }

        public void setKiosk_test_apk(String kiosk_test_apk) {
            this.kiosk_test_apk = kiosk_test_apk;
        }

        public String getKiosk_test_apk_size() {
            return kiosk_test_apk_size;
        }

        public void setKiosk_test_apk_size(String kiosk_test_apk_size) {
            this.kiosk_test_apk_size = kiosk_test_apk_size;
        }
    }
}
