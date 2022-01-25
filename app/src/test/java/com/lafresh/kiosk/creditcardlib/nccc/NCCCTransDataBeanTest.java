package com.lafresh.kiosk.creditcardlib.nccc;

import com.lafresh.kiosk.utils.TimeUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class NCCCTransDataBeanTest {
    private String dateString;
    private String timeString;
    private int amount;

    @Before
    public void setUp() {
        dateString = TimeUtil.getNowTime("yyMMdd");
        timeString = TimeUtil.getNowTime("HHmmss");
        amount = 1;
    }

    @Test
    public void getTransData() {
        String transDataString = "I       01N                               000000000100" +
                dateString + timeString +
                "                                                                                                                                                                                                                                                                                                                                              ";
        String testOutput = NCCCTransDataBean.getTransData(amount, dateString, timeString,"N");
        Assert.assertEquals(transDataString, testOutput);
        Assert.assertEquals(400, testOutput.length());
    }

    @Test
    public void getRefundData() {
        amount = 10000;
        String approvalNo = "737006";
        String refundString = "I       02N                               000001000000" +
                dateString + timeString + approvalNo +
                "                                                                                                                                                                                                                                                                                                                                        ";
        String testOutput = NCCCTransDataBean.getRefundData(amount, dateString, timeString, approvalNo);
        Assert.assertEquals(refundString, testOutput);
        Assert.assertEquals(400, testOutput.length());
    }

    @Test
    public void getSearchTransData() {
        String searchDataString = "I       62N                               000000000100" +
                dateString + timeString +
                "                                                                                                                                                          01                                                                                                                                                                                  ";
        String testOutput = NCCCTransDataBean.getSearchTransData(amount, dateString, timeString,"N");
        Assert.assertEquals(searchDataString, testOutput);
        Assert.assertEquals(400, testOutput.length());
    }

    @Test
    public void parseAmount() {
        String amountString = "000000000100";
        Assert.assertEquals(1, NCCCTransDataBean.parseAmount(amountString));
    }

    @Test
    public void getSettleData() {
        String settleDataString = "I       50N03                                         " +
                dateString + timeString +
                "                                                                                                                                                                                                                                                                                                                                              ";
        String testOutput = NCCCTransDataBean.getSettleData(dateString, timeString);
        Assert.assertEquals(settleDataString, testOutput);
        Assert.assertEquals(400, testOutput.length());
    }

    @Test
    public void generateRes() {
        String resString = "I110325 01N03000007474609******9784       000000000100200408110033679007   V00006601999999     13999999                                                                                                             02000679  M                                 474609iBuI1LqTlPCi020JH/Lq9VNiGWEsodfTp2bQsq6JLDY=                                                                                              ";
        NCCCTransDataBean bean = NCCCTransDataBean.generateRes(resString);
        Assert.assertEquals("0000", bean.getEcrResponseCode());
    }

    @Test
    public void getEmptyString() {
        String oneEmpty = " ";
        Assert.assertEquals(oneEmpty, NCCCTransDataBean.getEmptyString(1));
        String tenEmpty = "          ";
        Assert.assertEquals(tenEmpty, NCCCTransDataBean.getEmptyString(10));
    }

    @Test
    public void parseInt() {
        String intString = "000000000100";
        Assert.assertEquals(100, NCCCTransDataBean.parseInt(intString));
    }

    @Test
    public void parseIntWhenBadString() {
        String badString = "safsd564";
        Assert.assertEquals(0, NCCCTransDataBean.parseInt(badString));
    }
}
