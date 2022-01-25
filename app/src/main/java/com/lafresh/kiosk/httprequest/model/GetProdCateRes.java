package com.lafresh.kiosk.httprequest.model;

import com.lafresh.kiosk.shoppingcart.model.ProductCate;

import java.util.List;

public class GetProdCateRes {

    /**
     * code : 0
     * message : App商品類型資料
     * datacnt : 59
     * prod_model :
     * imgpath : http://40.83.96.208:8080/kiosk/app/public/prodcate00/
     * data : [{"serno":"196","subject":"商品A","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"135","subject":"湯餃類","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"128","subject":"鍋貼","imgfile1":"128-1-1542700359.jpg","imgfile2":null,"istag":"0","ishome":"0"},{"serno":"129","subject":"水餃","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"130","subject":"湯品八方","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"132","subject":"魚丸","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"133","subject":"組合","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"134","subject":"套餐","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"136","subject":"乾麵","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"137","subject":"湯麵","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"138","subject":"湯品蘿蔔","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"139","subject":"蒸籠","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"140","subject":"抄手","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"141","subject":"小菜","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"142","subject":"飲品蘿蔔","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"143","subject":"生鮮水餃","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"144","subject":"生鮮抄手","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"145","subject":"袋裝飲料八方","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"146","subject":"袋裝飲料蘿蔔","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"152","subject":"商品(S05)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"153","subject":"商品(W00)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"154","subject":"商品(W00)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"155","subject":"商品(W00)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"156","subject":"商品(W02)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"157","subject":"商品(W00)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"158","subject":"商品(W00)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"161","subject":"原料(W02)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"162","subject":"原料(W00)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"165","subject":"固定資產","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"166","subject":"營業用品","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"167","subject":"衛生用品","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"168","subject":"文具用品","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"169","subject":"零件耗材","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"170","subject":"耗材","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"171","subject":"雜項","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"172","subject":"列管","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"173","subject":"包材(W00)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"174","subject":"包材(W02)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"175","subject":"製成品(W00)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"176","subject":"製成品(W02)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"177","subject":"高雄商品(W00)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"178","subject":"高雄商品(W02)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"179","subject":"POS鍋貼類","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"180","subject":"POS水餃類","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"181","subject":"POS麵類","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"182","subject":"POS抄手類","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"183","subject":"POS湯品類","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"184","subject":"POS小菜類","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"185","subject":"POS飲品","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"186","subject":"POS生鮮類","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"187","subject":"POS調味品","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"188","subject":"POS時蔬類","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"189","subject":"POS套餐類","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"190","subject":"POS包材類","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"191","subject":"半成品(W00)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"192","subject":"半成品(W02)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"193","subject":"半成品(W02)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"194","subject":"商品(T00)","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"},{"serno":"195","subject":"APP禮券","imgfile1":null,"imgfile2":null,"istag":"0","ishome":"0"}]
     */

    private int code = -999;
    private String message;
    private int datacnt;
    private String prod_model;
    private String imgpath;
    private List<ProductCate> data;

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

    public String getProd_model() {
        return prod_model;
    }

    public void setProd_model(String prod_model) {
        this.prod_model = prod_model;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public List<ProductCate> getData() {
        return data;
    }

    public void setData(List<ProductCate> data) {
        this.data = data;
    }
}
