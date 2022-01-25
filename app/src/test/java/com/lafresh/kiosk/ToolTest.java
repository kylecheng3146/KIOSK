package com.lafresh.kiosk;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ToolTest {

    @Test
    public void encodeBase64() {
        String testString = "1234567 7654321";
        String resultString = "MTIzNDU2NyA3NjU0MzIx";
        Assert.assertEquals(resultString, Tool.encodeBase64(testString.getBytes()));
    }

    @Test
    public void decodeBase64() {
        String testString = "MTIzNDU2NyA3NjU0MzIx";
        String resultString = "1234567 7654321";
        Assert.assertArrayEquals(resultString.getBytes(), Tool.decodeBase64Byte(testString));
    }

    @Test
    public void encodeAESBase64() {
        String aesKey = "DE92AE7B79AE53D56B32F89D8B57C587";
        String testString = "XF784550319364";
        String resultString = "wW4GU3+B8nNYFis4tDWOmw==";
        Assert.assertEquals(resultString, Tool.encodeAESBase64(aesKey, testString));
    }

    @Test
    public void decodeAESBase64() {
        String aesKey = "DE92AE7B79AE53D56B32F89D8B57C587";
        String testString = "wW4GU3+B8nNYFis4tDWOmw==";
        String resultString = "XF784550319364";
        Assert.assertEquals(resultString, Tool.decodeAESBase64(aesKey, testString));
    }
}