package com.lafresh.kiosk.utils;

import org.junit.Assert;
import org.junit.Test;

public class VerifyUtilTest {

    @Test
    public void isPhoneCarrierNumber() {
        String carrierNumberString = "/QD.H43Q";
        Assert.assertTrue(VerifyUtil.isPhoneCarrierNumber(carrierNumberString));
    }

    @Test
    public void isPhoneCarrierNumberWhenNull() {
        Assert.assertFalse(VerifyUtil.isPhoneCarrierNumber(null));
    }

    @Test
    public void isPhoneCarrierNumberWhenInvalidLength() {
        String badCarrierNumber = "";
        Assert.assertFalse(VerifyUtil.isPhoneCarrierNumber(badCarrierNumber));
    }

    @Test
    public void isPhoneCarrierNumberWhenWrongCode() {
        String badCarrierNumber = "Q/D.H43Q";
        Assert.assertFalse(VerifyUtil.isPhoneCarrierNumber(badCarrierNumber));
    }

    @Test
    public void isLoveCode() {
        String loveCode = "919";//創世基金會
        Assert.assertTrue(VerifyUtil.isLoveCode(loveCode));
    }

    @Test
    public void isLoveCodeWhenTooShort() {
        String badLoveCode = "12";
        Assert.assertFalse(VerifyUtil.isLoveCode(badLoveCode));
    }

    @Test
    public void isLoveCodeWhenTooLong() {
        String badLoveCode = "12345678";
        Assert.assertFalse(VerifyUtil.isLoveCode(badLoveCode));
    }

    @Test
    public void isLoveCodeWhenBadCode() {
        String badLoveCode = "12abc";
        Assert.assertFalse(VerifyUtil.isLoveCode(badLoveCode));
    }

    @Test
    public void isLoveCodeWhenNull() {
        Assert.assertFalse(VerifyUtil.isLoveCode(null));
    }

    @Test
    public void isUnifiedBusinessNumber() {
        String unifiedBusinessNumber = "24436074";
        Assert.assertTrue(VerifyUtil.isUnifiedBusinessNumber(unifiedBusinessNumber));
    }

    @Test
    public void isUnifiedBusinessNumberCase2() {
        String unifiedBusinessNumberCase2 = "68461979";//第7碼=7
        Assert.assertTrue(VerifyUtil.isUnifiedBusinessNumber(unifiedBusinessNumberCase2));
    }

    @Test
    public void isUnifiedBusinessNumberWhenInvalidLength() {
        String badNumber = "123";
        Assert.assertFalse(VerifyUtil.isUnifiedBusinessNumber(badNumber));
    }

    @Test
    public void isUnifiedBusinessNumberWhenBadCode() {
        String badNumber = "23456789";
        Assert.assertFalse(VerifyUtil.isUnifiedBusinessNumber(badNumber));
    }

    @Test
    public void isUnifiedBusinessNumberWhenNotNumber() {
        String badNumber = "dsflkdk5";
        Assert.assertFalse(VerifyUtil.isUnifiedBusinessNumber(badNumber));
    }

    @Test
    public void isUnifiedBusinessNumberWhenNull() {
        Assert.assertFalse(VerifyUtil.isUnifiedBusinessNumber(null));
    }

    @Test
    public void isLegalBlcFileName() {
        String goodFileName = "BLC03331A_161221.BIG";
        Assert.assertTrue(VerifyUtil.isLegalBlcFileName(goodFileName));
    }

    @Test
    public void isLegalBlcFileNameWhenBadSuffix() {
        String badFileName = "BLC03331A_161221";
        Assert.assertFalse(VerifyUtil.isLegalBlcFileName(badFileName));
    }

    @Test
    public void isLegalBlcFileNameWhenBadPrefix() {
        String badFileName = "3331A_161221.BIG";
        Assert.assertFalse(VerifyUtil.isLegalBlcFileName(badFileName));
    }

    @Test
    public void isLegalBlcFileNameWhenEmpty() {
        Assert.assertFalse(VerifyUtil.isLegalBlcFileName(""));
    }

    @Test
    public void isLegalBlcFileNameWhenNull() {
        Assert.assertFalse(VerifyUtil.isLegalBlcFileName(null));
    }
}