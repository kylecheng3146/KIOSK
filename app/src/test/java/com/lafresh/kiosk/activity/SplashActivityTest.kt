package com.lafresh.kiosk.activity

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * Created by Kyle on 2021/8/17.
 */
class SplashActivityTest {
    @Test
    fun validateIsNewVersion() {
        val activity = mockk<SplashActivity>()
        every { activity.validateIsNewVersion() } returns Unit
        activity.validateIsNewVersion()
        verify { activity.validateIsNewVersion() }
        confirmVerified(activity)
    }

    @Test
    fun setUI() {
        val activity = mockk<SplashActivity>()
        every { activity.setUI() } returns Unit
        activity.setUI()
        verify { activity.setUI() }
        confirmVerified(activity)
    }

    @Test
    fun setActions() {
        val activity = mockk<SplashActivity>()
        every { activity.setActions() } returns Unit
        activity.setActions()
        verify { activity.setActions() }
        confirmVerified(activity)
    }

    @Test
    fun validateConnectBT() {
        val activity = mockk<SplashActivity>()
        every { activity.validateConnectBT() } returns Unit
        activity.validateConnectBT()
        verify { activity.validateConnectBT() }
        confirmVerified(activity)
    }

    @Test
    fun startLogUploadTimer() {
        val activity = mockk<SplashActivity>()
        every { activity.startLogUploadTimer() } returns Unit
        activity.startLogUploadTimer()
        verify { activity.startLogUploadTimer() }
        confirmVerified(activity)
    }

    @Test
    fun onOk() {
        val activity = mockk<SplashActivity>()
        every { activity.onOk() } returns Unit
        activity.onOk()
        verify { activity.onOk() }
        confirmVerified(activity)
    }

    @Test
    fun onBack() {
        val activity = mockk<SplashActivity>()
        every { activity.onBack() } returns Unit
        activity.onBack()
        verify { activity.onBack() }
        confirmVerified(activity)
    }

    @Test
    fun showErrorDialog() {
        val activity = mockk<SplashActivity>()
        every { activity.showErrorDialog() } returns Unit
        activity.showErrorDialog()
        verify { activity.showErrorDialog() }
        confirmVerified(activity)
    }

    @Test
    fun waitSetting() {
        val activity = mockk<SplashActivity>()
        every { activity.waitSetting() } returns Unit
        activity.waitSetting()
        verify { activity.waitSetting() }
        confirmVerified(activity)
    }

    @Test
    fun checkAndUpdateKiosk() {
        val activity = mockk<SplashActivity>()
        every { activity.checkAndUpdateKiosk() } returns Unit
        activity.checkAndUpdateKiosk()
        verify { activity.checkAndUpdateKiosk() }
        confirmVerified(activity)
    }

    @Test
    fun changeLoadingMessage() {
        val activity = mockk<SplashActivity>()
        every { activity.changeLoadingMessage("test") } returns Unit
        activity.changeLoadingMessage("test")
        verify { activity.changeLoadingMessage("test") }
        confirmVerified(activity)
    }

    @Test
    fun detectPerformance() {
        val activity = mockk<SplashActivity>()
        every { activity.detectPerformance() } returns Unit
        activity.detectPerformance()
        verify { activity.detectPerformance() }
        confirmVerified(activity)
    }

    @Test
    fun showWifiConnectionErrorDialog() {
        val activity = mockk<SplashActivity>()
        every { activity.showWifiConnectionErrorDialog() } returns Unit
        activity.showWifiConnectionErrorDialog()
        verify { activity.showWifiConnectionErrorDialog() }
        confirmVerified(activity)
    }

    @Test
    fun validateToothIsConnected() {
        val activity = mockk<SplashActivity>()
        every { activity.validateToothIsConnected() } returns true
        activity.validateToothIsConnected()
        verify { activity.validateToothIsConnected() }
        confirmVerified(activity)
    }
}
