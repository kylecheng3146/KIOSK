package com.lafresh.kiosk.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TimeUtil.class)
public class TimeUtilTest {
    long mockTimeMillis = 1600217215515L;
    Date date = new Date(mockTimeMillis);

    private void mockNewDateWithNoArguments() {
        try {
            PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mockSystemCurrentTimeMillis() {
        PowerMockito.spy(System.class);
        try {
            PowerMockito.when(System.currentTimeMillis()).thenReturn(mockTimeMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseTime() {
        String expectTime = "2020-09-16 08:46:55";
        Assert.assertEquals(expectTime, TimeUtil.parseTime(mockTimeMillis, TimeUtil.RECEIPT_PATTERN));
    }

    @Test
    public void getNowTimeWithPattern() {
        mockNewDateWithNoArguments();
        String expectTime = "2020-09-16T08:46:55.515Z";
        Assert.assertEquals(expectTime, TimeUtil.getNowTime(TimeUtil.ISO8601_PATTERN));
    }

    @Test
    public void getNowTime() {
        mockNewDateWithNoArguments();
        String expectTime = "2020-09-16 08:46:55";
        Assert.assertEquals(expectTime, TimeUtil.getNowTime());
    }

    @Test
    public void getNowTimeToMs() {
        mockNewDateWithNoArguments();
        String expectTime = "2020-09-16 08:46:55.515";
        Assert.assertEquals(expectTime, TimeUtil.getNowTimeToMs());
    }

    @Test
    public void get30MinLaterTime() {
        mockSystemCurrentTimeMillis();
        String expectTime = "2020-09-16 09:16:55";
        Assert.assertEquals(expectTime, TimeUtil.get30MinLaterTime(TimeUtil.RECEIPT_PATTERN));
    }

    @Test
    public void getYYYYMMdd() {
        mockNewDateWithNoArguments();
        String expectTime = "20200916";
        Assert.assertEquals(expectTime, TimeUtil.getYYYYMMdd());
    }

    @Test
    public void getMMdd() {
        mockNewDateWithNoArguments();
        String expectTime = "09-16";
        Assert.assertEquals(expectTime, TimeUtil.getMMdd());
    }

    @Test
    public void getHHmm() {
        mockNewDateWithNoArguments();
        String expectTime = "08:46";
        Assert.assertEquals(expectTime, TimeUtil.getHHmm());
    }

    @Test
    public void getYYYYMM() {
        mockNewDateWithNoArguments();
        String expectTime = "2020-09";
        Assert.assertEquals(expectTime, TimeUtil.getYYYYMM());
    }

    @Test
    public void getYYYYMMWithDate() {
        String expectTime = "2020-09";
        Assert.assertEquals(expectTime, TimeUtil.getYYYYMM(date));
    }

    @Test
    public void getNowTimeToMsForDate() {
        mockNewDateWithNoArguments();
        Assert.assertEquals(date.toString(), TimeUtil.getNowTimeToMsForDate().toString());
    }

    @Test
    public void parseDateToMs() {
        mockNewDateWithNoArguments();
        Assert.assertEquals(date.toString(), TimeUtil.parseDateToMs("2020-09-16 08:46:55.515").toString());
    }

    @Test
    public void parseDateToString() {
        String expectTimeString = "2020-09-16 08:46:55";
        Assert.assertEquals(expectTimeString, TimeUtil.parseDateToString(date));
    }

    @Test
    public void parseDate() {
        String mockTimeString = "2020-09-16 08:46:55";
        Assert.assertEquals(date.toString(), TimeUtil.parseDate(mockTimeString).toString());
    }
}