package com.lafresh.kiosk.httprequest.model;

public class KioskImg {

    /**
     * code  : 0
     * message :  kiosk廣告海報資料
     * datacnt : 1
     * imgpath : http://40.83.96.208:8080/kiosk/app/public/company/
     * img_kiosk : 8WAY_kiosk.jpg
     */

    private int code = -999;
    private String message;
    private String datacnt;
    private String imgpath;
    private String img_kiosk;
    private String img_kiosk_logo_1;
    private String img_kiosk_logo_2;

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

    public String getDatacnt() {
        return datacnt;
    }

    public void setDatacnt(String datacnt) {
        this.datacnt = datacnt;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getImg_kiosk() {
        return img_kiosk;
    }

    public void setImg_kiosk(String img_kiosk) {
        this.img_kiosk = img_kiosk;
    }

    public String getImg_kiosk_logo_1() {
        return img_kiosk_logo_1;
    }

    public void setImg_kiosk_logo_1(String img_kiosk_logo_1) {
        this.img_kiosk_logo_1 = img_kiosk_logo_1;
    }

    public String getImg_kiosk_logo_2() {
        return img_kiosk_logo_2;
    }

    public void setImg_kiosk_logo_2(String img_kiosk_logo_2) {
        this.img_kiosk_logo_2 = img_kiosk_logo_2;
    }
}
