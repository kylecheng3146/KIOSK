package com.lafresh.kiosk.httprequest.model;

import com.lafresh.kiosk.shoppingcart.model.Product;

import java.util.List;

public class GetProductRes {

    /**
     * code : 0
     * message : 商品資料
     * shop_id : X0058
     * prod_shop_id : M0017
     * datacnt : 14
     * imgpath : http://40.83.96.208:8080/kiosk/app/public/product00/
     * appcate_imgpath : http://40.83.96.208:8080/kiosk/app/public/prodcate00/
     * product : [{"prod_id":"PS00000001","prod_name1":"招牌鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"PCS","barcode":"","tax":".0500","tax_sign":"","price1":"5.000000","price2":"5.000000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":"4695-1-1546586572_s.jpg","prod_content":"<p>將特選豬後腿肉的嚼勁揉進高麗菜的香甜口感，包入新鮮製作的餃皮再煎至金黃酥脆，成就八方雲集自豪的招牌鍋貼。<\/p>","spe_price":null},{"prod_id":"10005","prod_name1":"田園蔬菜鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"顆","barcode":"","tax":".0500","tax_sign":"T","price1":"5.000000","price2":"5.000000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":"<p>將特選豬後腿肉的嚼勁揉進高麗菜的香甜口感，包入新鮮製作的餃皮再煎至金黃酥脆，成就八方雲集自豪的招牌鍋貼。<\/p>","spe_price":null},{"prod_id":"PS00000002","prod_name1":"韭菜鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"PCS","barcode":"","tax":".0500","tax_sign":"","price1":"5.500000","price2":"5.500000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":null,"spe_price":null},{"prod_id":"10001","prod_name1":"招牌鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"顆","barcode":"","tax":".0500","tax_sign":"T","price1":"22.000000","price2":"22.000000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":"","spe_price":"1.0000"},{"prod_id":"10002","prod_name1":"韭菜鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"顆","barcode":"","tax":".0500","tax_sign":"T","price1":"8.000000","price2":"8.000000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","spe_price":null,"packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":null},{"prod_id":"10003","prod_name1":"韓式辣味鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"顆","barcode":"","tax":".0500","tax_sign":"T","price1":"5.000000","price2":"5.000000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"0","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":null,"spe_price":null},{"prod_id":"10004","prod_name1":"咖哩鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"顆","barcode":"","tax":".0500","tax_sign":"T","price1":"5.000000","price2":"5.000000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":null,"spe_price":null},{"prod_id":"PS00000003","prod_name1":"韓式辣味鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"PCS","barcode":"","tax":".0500","tax_sign":"","price1":"5.500000","price2":"5.500000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":null,"spe_price":null},{"prod_id":"PS00000004","prod_name1":"咖哩鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"PCS","barcode":"","tax":".0500","tax_sign":"","price1":"5.500000","price2":"5.500000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":null,"spe_price":null},{"prod_id":"PS00000007","prod_name1":"歐式酸甘藍鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"PCS","barcode":"","tax":".0500","tax_sign":"","price1":"6.000000","price2":"6.000000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":null,"spe_price":null},{"prod_id":"PS00000013","prod_name1":"玉米鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"台北","dep_memo":null,"unit":"PCS","barcode":"","tax":".0500","tax_sign":"","price1":"5.500000","price2":"5.500000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":null,"spe_price":null},{"prod_id":"PS00000015","prod_name1":"韭黃魚肉鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"PCS","barcode":"","tax":".0500","tax_sign":"","price1":"6.500000","price2":"6.500000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":null,"spe_price":null},{"prod_id":"PS00000016","prod_name1":"韭黃魚肉鍋貼*","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"PCS","barcode":"","tax":".0500","tax_sign":"","price1":".000000","price2":".000000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":null,"spe_price":null},{"prod_id":"PS00000017","prod_name1":"蒲瓜鍋貼","prod_name2":"","dep_id":"10","app_cateid":["128"],"app_cate":[{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null}],"prod_memo":"","spec":"","dep_memo":null,"unit":"PCS","barcode":"","tax":".0500","tax_sign":"","price1":"5.000000","price2":"5.000000","enable":"1","iscomb":"0","ispack":"0","combined":"0","isfloat":"0","isusepos":"1","stop_sale":"0","nonservicecharge":"0","nonbonuspt":"0","isredeem":"0","redeem_point":"0","packprc_type":"1","size_type":"0","szfprod_id":"","sztaste_sno":"0","origin":"","element":"","caloric":"","imgfile1":null,"prod_content":null,"spe_price":null}]
     */

    private int code = -999;
    private String message;
    private String shop_id;
    private String prod_shop_id;
    private int datacnt;
    private String imgpath;
    private String appcate_imgpath;
    private List<Product> product;

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

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getProd_shop_id() {
        return prod_shop_id;
    }

    public void setProd_shop_id(String prod_shop_id) {
        this.prod_shop_id = prod_shop_id;
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

    public String getAppcate_imgpath() {
        return appcate_imgpath;
    }

    public void setAppcate_imgpath(String appcate_imgpath) {
        this.appcate_imgpath = appcate_imgpath;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }
}
