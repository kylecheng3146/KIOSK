package com.lafresh.kiosk.creditcardlib.model;

public class Request {
    private String path;
    private int baudRate;
    private boolean needSessionWait = true; //flag to control session wait
    private int sessionInterval;
    private String data;

    public Request(String path, int baudRate, String data) {
        this.path = path;
        this.baudRate = baudRate;
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public boolean isNeedSessionWait() {
        return needSessionWait;
    }

    public void setNeedSessionWait(boolean needSessionWait) {
        this.needSessionWait = needSessionWait;
    }

    public int getSessionInterval() {
        return sessionInterval;
    }

    public void setSessionInterval(int sessionInterval) {
        this.sessionInterval = sessionInterval;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
