package com.lafresh.kiosk.utils;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class FormatUtilTest {

    @Test
    public void addLeftSpaceToLength() {
        String originString = "ABC";
        String expectString = "   ABC";
        Assert.assertEquals(expectString, FormatUtil.addLeftSpaceTillLength(originString, 6));
        Assert.assertEquals(originString, FormatUtil.addLeftSpaceTillLength(originString, 3));
    }

    @Test
    public void removeDot() {
        String testString = "123.000";
        String expectString = "123";
        Assert.assertEquals(expectString, FormatUtil.removeDot(testString));
    }

    @Test
    public void testRemoveDot() {
        double testNumber = 123.0;
        String expectString = "123";
        Assert.assertEquals(expectString, FormatUtil.removeDot(testNumber));
    }

    @Test
    public void leftPad() {
        String testString = "123";
        String expectString = "     123";
        Assert.assertThat(FormatUtil.leftPad(testString, 8), is(expectString));
    }

    @Test
    public void testLeftPad() {
        String testString = "123";
        String expectString = "@@@@@123";
        Assert.assertThat(FormatUtil.leftPad(testString, 8, "@"), is(expectString));
    }

    @Test
    public void rightPad() {
        String testString = "123";
        String expectString = "123     ";
        Assert.assertThat(FormatUtil.rightPad(testString, 8), is(expectString));
    }

    @Test
    public void testRightPad() {
        String testString = "123";
        String expectString = "123!!!!!";
        Assert.assertThat(FormatUtil.rightPad(testString, 8, "!"), is(expectString));
    }

    @Test
    public void repeatString() {
        String expectString = "!!!!!";
        Assert.assertThat(FormatUtil.repeatString("!", 5), is(expectString));
    }

    @Test
    public void toEightDigitHexString() {
        int number = 12345678;
        String expectString = "00bc614e";
        Assert.assertThat(FormatUtil.toEightDigitHexString(number), is(expectString));
    }

    @Test
    public void parseRocYear() {
        String year = "2020";
        String rocYear = "109";
        Assert.assertThat(FormatUtil.parseRocYear(year), is(rocYear));
    }
}