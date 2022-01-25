package com.lafresh.kiosk.model;

import org.junit.Assert;
import org.junit.Test;

public class GrokLogTest {
    String sample = "2020-08-24 18:16:40.416|--|219.84.34.82|--|info|--|0|--|KIOSK|--" +
            "|device_id|--|service_version|--|type|--|company_id|--|shop_id|--|resourceId" +
            "|--|msg|--|msgData|--|msgDetail";

    @Test
    public void testToString() {
        GrokLog grokLog = new GrokLog();
        grokLog.setLogDatetime("2020-08-24 18:16:40.416");
        grokLog.setIp("219.84.34.82");
        grokLog.setDeviceId("device_id");
        grokLog.setServiceVersion("service_version");
        grokLog.setType("type");
        grokLog.setCompanyId("company_id");
        grokLog.setShopId("shop_id");
        grokLog.setResourceId("resourceId");
        grokLog.setMsg("msg");
        grokLog.setMsgData("msgData");
        grokLog.setMsgDetail("msgDetail");
        Assert.assertEquals(sample,grokLog.toString());
        System.out.println(grokLog.toString());
    }
}