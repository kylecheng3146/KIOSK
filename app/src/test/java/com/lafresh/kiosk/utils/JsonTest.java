package com.lafresh.kiosk.utils;

import com.lafresh.kiosk.model.ConfigFileData;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class JsonTest {
    private static String jsonString;
    private static ConfigFileData configFileData;

    @BeforeClass
    public static void setup() {
        jsonString = "{" +
                "\"ApiRoot\":\"http://kiosk.lafresh.com.tw:8080/kiosk/app\"," +
                "\"cacheUrl\":\"http://13.70.19.141:80\"," +
                "\"linePayUrl\":\"http://kiosk.lafresh.com.tw:8080/kiosk/app/webservice/line/pay.php\"," +
                "\"authKey\":\"v32lgfy6hdq7y69pr95a62new6zsz3bc\"," +
                "\"accKey\":\"67pcbag9awwfdxe19gdj1rgfb2xvny9c\"," +
                "\"shop_id\":\"000030\"," +
                "\"acckeyDefault\":\"67pcbag9awwfdxe19gdj1rgfb2xvny9c\"," +
                "\"kiosk_id\":\"k1\"," +
                "\"creditCardComportPath\":\"/dev/ttyS3\"," +
                "\"creditCardBaudRate\":9600," +
                "\"useNcccByShop\":false," +
                "\"useNcccByKiosk\":false," +
                "\"usePiPay\":false," +
                "\"enableCounter\":false" +
                "}";
    }

    @Test
    public void fromJson() {
        configFileData = Json.fromJson(jsonString, ConfigFileData.class);
        Assert.assertEquals("000030", configFileData.getShop_id());
        Assert.assertEquals(9600, configFileData.getCreditCardBaudRate());
        Assert.assertFalse(configFileData.isEnableCounter());
    }

    @Test
    public void toJson() {
        Assert.assertEquals(jsonString, Json.toJson(configFileData));
    }

    @Test
    public void parseStringInJson() {
        String s = "\"{\\\"s\\\":\\\"ABC\\\"}\"";
        Assert.assertEquals("\"{\"s\":\"ABC\"}\"", Json.parseStringInJson(s));
    }
}