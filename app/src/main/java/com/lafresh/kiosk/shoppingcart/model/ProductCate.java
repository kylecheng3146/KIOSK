package com.lafresh.kiosk.shoppingcart.model;

import java.io.Serializable;

public class ProductCate implements Serializable{
    private String serno;
    private String subject;
    private String imgfile1;
    private String imgfile2;
    private String istag;
    private String ishome;

    public String getSerno() {
        return serno;
    }

    public void setSerno(String serno) {
        this.serno = serno;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getImgfile1() {
        return imgfile1;
    }

    public void setImgfile1(String imgfile1) {
        this.imgfile1 = imgfile1;
    }

    public String getImgfile2() {
        return imgfile2;
    }

    public void setImgfile2(String imgfile2) {
        this.imgfile2 = imgfile2;
    }

    public String getIstag() {
        return istag;
    }

    public void setIstag(String istag) {
        this.istag = istag;
    }

    public String getIshome() {
        return ishome;
    }

    public void setIshome(String ishome) {
        this.ishome = ishome;
    }
}
