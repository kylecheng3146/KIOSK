package com.lafresh.kiosk.utils

import org.junit.Assert
import org.junit.Test

/**
 * Created by Kyle on 2021/2/5.
 */
class CommonUtilsTest {
    @Test
    fun removeDot() {
        val removeDotValue = CommonUtils.removeDot("1.0")
        Assert.assertEquals(removeDotValue, "1")
    }

    @Test
    fun isDayExpired() {
        val dateTime = "2021-02-04 00:00"
        val isDayExpired = CommonUtils.isDayExpired(dateTime)
        Assert.assertEquals(isDayExpired, true)
    }

    @Test
    fun spaceIt() {
        val spaceIt = "test"
        Assert.assertEquals(CommonUtils.spaceIt(spaceIt, 5), " test")
    }

    @Test
    fun isEmailValid() {
        val email = "test@test.com"
        Assert.assertEquals(CommonUtils.isEmailValid(email), true)
    }
}
