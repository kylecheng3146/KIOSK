package com.lafresh.kiosk.utils;

import org.junit.Assert;
import org.junit.Test;

public class ArithTest {

    @Test
    public void add() {
        Assert.assertNotEquals(0.3, 0.1 + 0.2, 0);
        Assert.assertEquals(0.3, Arith.add(0.1, 0.2), 0);
    }

    @Test
    public void sub() {
        Assert.assertNotEquals(0.00001, 0.00003 - 0.00002, 0);
        Assert.assertEquals(0.00001, Arith.sub(0.00003, 0.00002), 0);
    }

    @Test
    public void testSub() {
        Assert.assertNotEquals(0.00002, 0.00005 - 0.00002 - 0.00001, 0);
        Assert.assertEquals(0.00002, Arith.sub(0.00005, 0.00002, 0.00001), 0);
    }

    @Test
    public void mul() {
        Assert.assertNotEquals(0.9, 0.3 * 3, 0);
        Assert.assertEquals(0.9, Arith.mul(0.3, 3), 0);
    }

    @Test
    public void testMul() {
        Assert.assertNotEquals(0.042, 0.2 * 0.3 * 0.7, 0);
        Assert.assertEquals(0.042, Arith.mul(0.2, 0.3, 0.7), 0);
    }

    @Test
    public void div() {
        Assert.assertNotEquals(4, 0.8 / 0.2);
        Assert.assertEquals(4, Arith.div(0.8, 0.2), 0);
    }

    @Test
    public void testDiv() {
        Assert.assertNotEquals(3.333333333333333, Arith.round(0.1 / 0.03, 15), 0);
        Assert.assertEquals(3.333333333333334, Arith.round(0.1 / 0.03, 15), 0);
        Assert.assertEquals(3.333333333333333, Arith.div(0.1, 0.03, 15), 0);
    }

    @Test
    public void round() {
        Assert.assertEquals(3.14, Arith.round(Math.PI, 2), 0);
        Assert.assertEquals(3.14159, Arith.round(Math.PI, 5), 0);
    }

    @Test
    public void abs() {
        Assert.assertEquals(0.00007, Arith.abs(-0.00007), 0);
    }

    @Test
    public void neg() {
        Assert.assertEquals(-0.008, Arith.neg(0.008), 0);
    }

    @Test
    public void pow() {
        Assert.assertNotEquals(0.008, Math.pow(0.2, 3), 0);
        Assert.assertEquals(0.008, Arith.pow(0.2, 3), 0);
    }

    @Test
    public void isEqual() {
        Assert.assertFalse(Arith.isEqual(0.3, 0.1 + 0.2));
        Assert.assertTrue(Arith.isEqual(0.3, Arith.add(0.1, 0.2)));
    }
}