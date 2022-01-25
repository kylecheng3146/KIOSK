package com.lafresh.kiosk

import com.lafresh.kiosk.activity.ScanActivity
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test
import org.mockito.Mock

/**
 * Created by Kyle on 2021/2/20.
 */
class ScanActivityTest {

    @Mock
    val scanActivity = ScanActivity()

    @Test
    fun getDataFromUrl() {
        val test = scanActivity.getDataFromUrl("test", "http://www.google.com/?test=123")
        Assert.assertEquals(test, "123")
    }

    @Test
    fun showFailMessage() {
        val activity = mockk<ScanActivity>()
        every { activity.showFailMessage(any()) } returns Unit
        activity.showFailMessage("123") // returns OK
        verify { activity.showFailMessage("123") }
        confirmVerified(activity)
    }

    @Test
    fun showErrorDialog() {
        val activity = mockk<ScanActivity>()
        every { activity.showErrorDialog(any(), any()) } returns Unit
        activity.showErrorDialog(123, 123) // returns OK
        verify { activity.showErrorDialog(123, 123) }
        confirmVerified(activity)
    }

    @Test
    fun login() {
        val activity = mockk<ScanActivity>()
        every { activity.login(any()) } returns Unit
        activity.login("123")
        verify { activity.login("123") }
        confirmVerified(activity)
    }

    @Test
    fun checkTableNoCode() {
        val activity = mockk<ScanActivity>()
        every { activity.checkTableNoCode(any()) } returns Unit
        activity.checkTableNoCode("123")
        verify { activity.checkTableNoCode("123") }
        confirmVerified(activity)
    }

    @Test
    fun linePayOldVer() {
        val activity = mockk<ScanActivity>()
        every { activity.linePayOldVer(any()) } returns Unit
        activity.linePayOldVer("123")
        verify { activity.linePayOldVer("123") }
        confirmVerified(activity)
    }

    @Test
    fun setType() {
        val activity = mockk<ScanActivity>()
        every { activity.setType() } returns Unit
        activity.setType()
        verify { activity.setType() }
        confirmVerified(activity)
    }

    @Test
    fun setUI() {
        val activity = mockk<ScanActivity>()
        every { activity.setUI() } returns Unit
        activity.setUI()
        verify { activity.setUI() }
        confirmVerified(activity)
    }

    @Test
    fun setActions() {
        val activity = mockk<ScanActivity>()
        every { activity.setActions() } returns Unit
        activity.setActions()
        verify { activity.setActions() }
        confirmVerified(activity)
    }
}
