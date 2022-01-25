package com.lafresh.kiosk.utils;

import org.junit.Assert;
import org.junit.Test;


public class CryptoUtilTest {

    @Test
    public void getSHA256StrJava() {
        String sampleString = "我是胖虎我是孩子王，我天下無敵";
        String resultString = "ab4001a2166e9789792c5745eccfd198ea8a485efebb8f1f1418e561001a8d3b";
        Assert.assertEquals(resultString,CryptoUtil.getSHA256StrJava(sampleString));
    }
}