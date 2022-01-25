package com.lafresh.kiosk.shoppingcart.model;

import java.util.List;

public class Product {
    private String prod_id;
    private String prod_name1;
    private String prod_name2;
    private String dep_id;
    private String prod_memo;
    private String spec;
    private String dep_memo;
    private String unit;
    private String barcode;
    private String tax;
    private String tax_sign;
    private String price1;
    private String price2;
    private String enable;
    private String iscomb;
    private String ispack;
    private String combined;
    private String isfloat;
    private String isusepos;
    private String stop_sale;
    private String nonservicecharge;
    private String nonbonuspt;
    private String isredeem;
    private String redeem_point;
    private String packprc_type;
    private String size_type;
    private String szfprod_id;
    private String sztaste_sno;
    private String origin;
    private String element;
    private String caloric;
    private String imgfile1;
    private String prod_content;
    private String spe_price;
    private boolean isUsePoint = false;
    private boolean stop_sell;//停售
    private List<String> app_cateid;
    private List<AppCateBean> app_cate;

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_name1() {
        return prod_name1;
    }

    public void setProd_name1(String prod_name1) {
        this.prod_name1 = prod_name1;
    }

    public String getProd_name2() {
        return prod_name2;
    }

    public void setProd_name2(String prod_name2) {
        this.prod_name2 = prod_name2;
    }

    public String getDep_id() {
        return dep_id;
    }

    public void setDep_id(String dep_id) {
        this.dep_id = dep_id;
    }

    public String getProd_memo() {
        return prod_memo;
    }

    public void setProd_memo(String prod_memo) {
        this.prod_memo = prod_memo;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getDep_memo() {
        return dep_memo;
    }

    public void setDep_memo(String dep_memo) {
        this.dep_memo = dep_memo;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTax_sign() {
        return tax_sign;
    }

    public void setTax_sign(String tax_sign) {
        this.tax_sign = tax_sign;
    }

    public String getPrice1() {
        return price1;
    }

    public void setPrice1(String price1) {
        this.price1 = price1;
    }

    public String getPrice2() {
        return price2;
    }

    public void setPrice2(String price2) {
        this.price2 = price2;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getIscomb() {
        return iscomb;
    }

    public void setIscomb(String iscomb) {
        this.iscomb = iscomb;
    }

    public String getIspack() {
        return ispack;
    }

    public void setIspack(String ispack) {
        this.ispack = ispack;
    }

    public String getCombined() {
        return combined;
    }

    public void setCombined(String combined) {
        this.combined = combined;
    }

    public String getIsfloat() {
        return isfloat;
    }

    public void setIsfloat(String isfloat) {
        this.isfloat = isfloat;
    }

    public String getIsusepos() {
        return isusepos;
    }

    public void setIsusepos(String isusepos) {
        this.isusepos = isusepos;
    }

    public String getStop_sale() {
        return stop_sale;
    }

    public void setStop_sale(String stop_sale) {
        this.stop_sale = stop_sale;
    }

    public String getNonservicecharge() {
        return nonservicecharge;
    }

    public void setNonservicecharge(String nonservicecharge) {
        this.nonservicecharge = nonservicecharge;
    }

    public String getNonbonuspt() {
        return nonbonuspt;
    }

    public void setNonbonuspt(String nonbonuspt) {
        this.nonbonuspt = nonbonuspt;
    }

    public String getIsredeem() {
        return isredeem;
    }

    public void setIsredeem(String isredeem) {
        this.isredeem = isredeem;
    }

    public String getRedeem_point() {
        return redeem_point;
    }

    public void setRedeem_point(String redeem_point) {
        this.redeem_point = redeem_point;
    }

    public String getPackprc_type() {
        return packprc_type;
    }

    public void setPackprc_type(String packprc_type) {
        this.packprc_type = packprc_type;
    }

    public String getSize_type() {
        return size_type;
    }

    public void setSize_type(String size_type) {
        this.size_type = size_type;
    }

    public String getSzfprod_id() {
        return szfprod_id;
    }

    public void setSzfprod_id(String szfprod_id) {
        this.szfprod_id = szfprod_id;
    }

    public String getSztaste_sno() {
        return sztaste_sno;
    }

    public void setSztaste_sno(String sztaste_sno) {
        this.sztaste_sno = sztaste_sno;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getCaloric() {
        return caloric;
    }

    public void setCaloric(String caloric) {
        this.caloric = caloric;
    }

    public String getImgfile1() {
        return imgfile1;
    }

    public void setImgfile1(String imgfile1) {
        this.imgfile1 = imgfile1;
    }

    public String getProd_content() {
        return prod_content;
    }

    public void setProd_content(String prod_content) {
        this.prod_content = prod_content;
    }

    public String getSpe_price() {
        return spe_price;
    }

    public void setSpe_price(String spe_price) {
        this.spe_price = spe_price;
    }

    public boolean isStop_sell() {
        return stop_sell;
    }

    public void setStop_sell(boolean stop_sell) {
        this.stop_sell = stop_sell;
    }
    public boolean getUsePoint() {
        return isUsePoint;
    }

    public void setUsePoint(boolean isUsePoint) {
        this.isUsePoint = isUsePoint;
    }

    public List<String> getApp_cateid() {
        return app_cateid;
    }

    public void setApp_cateid(List<String> app_cateid) {
        this.app_cateid = app_cateid;
    }

    public List<AppCateBean> getApp_cate() {
        return app_cate;
    }

    public void setApp_cate(List<AppCateBean> app_cate) {
        this.app_cate = app_cate;
    }

    public static class AppCateBean {
        /**
         * serno : 128
         * subject : 鍋貼
         * imgfile1 : 128-1-1542700359.jpg
         * imgfile2 : null
         */

        private String serno;
        private String subject;
        private String imgfile1;
        private Object imgfile2;

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

        public Object getImgfile2() {
            return imgfile2;
        }

        public void setImgfile2(Object imgfile2) {
            this.imgfile2 = imgfile2;
        }
    }
}
