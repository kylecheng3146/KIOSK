package com.lafresh.kiosk.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClickUtilTest {

    @Test
    public void isFastDoubleClick() {
        Assert.assertFalse(ClickUtil.isFastDoubleClick());
        interval(1);
        Assert.assertTrue(ClickUtil.isFastDoubleClick());
    }

    @Test
    public void isFastDoubleClickWhenNotFast() {
        ClickUtil.isFastDoubleClick();
        interval(1001);
        Assert.assertFalse(ClickUtil.isFastDoubleClick());
    }

    private void interval(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}