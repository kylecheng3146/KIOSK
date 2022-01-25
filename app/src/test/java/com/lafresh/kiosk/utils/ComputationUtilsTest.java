package com.lafresh.kiosk.utils;

import org.junit.Assert;
import org.junit.Test;

public class ComputationUtilsTest {

    @Test
    public void isDouble() {
        Assert.assertTrue(ComputationUtils.isDouble("0.1"));
        Assert.assertTrue(ComputationUtils.isDouble("1"));
        Assert.assertFalse(ComputationUtils.isDouble("abc"));
        Assert.assertFalse(ComputationUtils.isDouble(""));
        Assert.assertFalse(ComputationUtils.isDouble(null));
    }

    @Test
    public void parseDouble() {
        Assert.assertEquals(0.1, ComputationUtils.parseDouble("0.1"), 0);
        Assert.assertEquals(0, ComputationUtils.parseDouble("abc"), 0);
    }

    @Test
    public void isInt() {
        Assert.assertTrue(ComputationUtils.isInt("1"));
        Assert.assertFalse(ComputationUtils.isInt("0.1"));
        Assert.assertFalse(ComputationUtils.isInt("abc"));
        Assert.assertFalse(ComputationUtils.isInt(""));
        Assert.assertFalse(ComputationUtils.isInt(null));
    }

    @Test
    public void parseInt() {
        Assert.assertEquals(1, ComputationUtils.parseInt("1"));
        Assert.assertEquals(0, ComputationUtils.parseInt(" "));
    }

    @Test
    public void parseSPtoPT() {
    }

    @Test
    public void parsePTtoSP() {
    }

    @Test
    public void isNotZero() {
        Assert.assertTrue(ComputationUtils.isNotZero("0.1"));
        Assert.assertTrue(ComputationUtils.isNotZero("1"));
        Assert.assertTrue(ComputationUtils.isNotZero("abc"));
        Assert.assertFalse(ComputationUtils.isNotZero("0.0"));
        Assert.assertFalse(ComputationUtils.isNotZero("0"));
    }
}