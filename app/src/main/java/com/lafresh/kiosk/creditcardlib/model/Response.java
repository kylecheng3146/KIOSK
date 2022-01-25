package com.lafresh.kiosk.creditcardlib.model;

public class Response {

    private int code = -1;
    private String message;
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static Response errResponse(int errCode) {
        return wrapResponse(errCode, "");
    }

    public static Response wrapResponse(int code, String data) {
        Response response = new Response();
        response.code = code;
        response.message = Message.getMessage(code);
        response.data = data;
        return response;
    }
}
