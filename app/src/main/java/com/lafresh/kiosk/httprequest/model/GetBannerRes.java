package com.lafresh.kiosk.httprequest.model;

import com.lafresh.kiosk.cache.DataCache;
import com.lafresh.kiosk.model.Banner;

import java.util.List;

public class GetBannerRes extends DataCache.CacheClass {

    /**
     * code : 0
     * message : banner資料
     * datacnt : 5
     * imgpath : http://40.83.96.208:8080/kiosk/app/public/banners/
     * data : [{"bannerid":"51","subject":"廣告圖12","url":"","imgfile1":null,"imgfile2":"51-2-1559035923.jpg"},{"bannerid":"57","subject":"華味香3","url":"","imgfile1":"57-1-1559018147.jpg","imgfile2":"57-2-1559035879.jpg"},{"bannerid":"56","subject":"華味香2","url":"","imgfile1":"56-1-1559018157.jpg","imgfile2":"56-2-1559035895.jpg"},{"bannerid":"55","subject":"華味香","url":"","imgfile1":"55-1-1559021239.jpg","imgfile2":"55-2-1559035908.jpg"},{"bannerid":"50","subject":"廣告圖11","url":"","imgfile1":null,"imgfile2":"50-2-1559035941.jpg"}]
     */

    private int code = -999;
    private String message;
    private int datacnt;
    private String imgpath;
    private List<Banner> data;

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

    public int getDatacnt() {
        return datacnt;
    }

    public void setDatacnt(int datacnt) {
        this.datacnt = datacnt;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public List<Banner> getData() {
        return data;
    }

    public void setData(List<Banner> data) {
        this.data = data;
    }
}
