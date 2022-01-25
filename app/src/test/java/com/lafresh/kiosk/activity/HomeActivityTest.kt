package com.lafresh.kiosk.activity

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * Created by Kyle on 2021/3/9.
 */
class HomeActivityTest {
    @Test
    fun updateEasycardDailyCheckoutApi() {
        val activity = mockk<HomeActivity>()
        every { activity.updateEasyCardDailyCheckoutApi() } returns Unit
        activity.updateEasyCardDailyCheckoutApi()
        verify { activity.updateEasyCardDailyCheckoutApi() }
        confirmVerified(activity)
    }

    @Test
    fun closeAllActivities() {
        val activity = mockk<HomeActivity>()
        every { activity.closeAllActivities() } returns Unit
        activity.closeAllActivities()
        verify { activity.closeAllActivities() }
        confirmVerified(activity)
    }

    @Test
    fun idleProof() {
        val activity = mockk<HomeActivity>()
        every { activity.idleProof() } returns Unit
        activity.idleProof()
        verify { activity.idleProof() }
        confirmVerified(activity)
    }

    @Test
    fun stopIdleProof() {
        val activity = mockk<HomeActivity>()
        every { activity.stopIdleProof() } returns Unit
        activity.stopIdleProof()
        verify { activity.stopIdleProof() }
        confirmVerified(activity)
    }

    @Test
    fun setUI() {
        val activity = mockk<HomeActivity>()
        every { activity.setUI() } returns Unit
        activity.setUI()
        verify { activity.setUI() }
        confirmVerified(activity)
    }

    @Test
    fun resetDefaultConfig() {
        val activity = mockk<HomeActivity>()
        every { activity.resetDefaultConfig() } returns Unit
        activity.resetDefaultConfig()
        verify { activity.resetDefaultConfig() }
        confirmVerified(activity)
    }

    @Test
    fun checkAndGo() {
        val activity = mockk<HomeActivity>()
        every { activity.checkAndGo() } returns Unit
        activity.checkAndGo()
        verify { activity.checkAndGo() }
        confirmVerified(activity)
    }

    @Test
    fun checkPrinter() {
        val activity = mockk<HomeActivity>()
        every { activity.checkPrinter() } returns Unit
        activity.checkPrinter()
        verify { activity.checkPrinter() }
        confirmVerified(activity)
    }

    @Test
    fun showEcCheckoutDialog() {
        val activity = mockk<HomeActivity>()
        every { activity.showEcCheckoutDialog() } returns Unit
        activity.showEcCheckoutDialog()
        verify { activity.showEcCheckoutDialog() }
        confirmVerified(activity)
    }

    @Test
    fun initBannerDisplay() {
        val activity = mockk<HomeActivity>()
        every { activity.initBannerDisplay() } returns Unit
        activity.initBannerDisplay()
        verify { activity.initBannerDisplay() }
        confirmVerified(activity)
    }

    @Test
    fun playBanners() {
        val activity = mockk<HomeActivity>()
        every { activity.playBanners() } returns Unit
        activity.playBanners()
        verify { activity.playBanners() }
        confirmVerified(activity)
    }

    @Test
    fun changeBannerItem() {
        val activity = mockk<HomeActivity>()
        every { activity.changeBannerItem(1) } returns Unit
        activity.changeBannerItem(1)
        verify { activity.changeBannerItem(1) }
        confirmVerified(activity)
    }

    @Test
    fun setLogo() {
        val activity = mockk<HomeActivity>()
        every { activity.setLogo() } returns Unit
        activity.setLogo()
        verify { activity.setLogo() }
        confirmVerified(activity)
    }

    @Test
    fun toNextPage() {
        val activity = mockk<HomeActivity>()
        every { activity.toNextPage() } returns Unit
        activity.toNextPage()
        verify { activity.toNextPage() }
        confirmVerified(activity)
    }

    @Test
    fun validateHasBanner() {
        val activity = mockk<HomeActivity>()
        every { activity.validateHasBanner() } returns Unit
        activity.validateHasBanner()
        verify { activity.validateHasBanner() }
        confirmVerified(activity)
    }

    @Test
    fun validatePrinterEnabled() {
        val activity = mockk<HomeActivity>()
        every { activity.validatePrinterEnabled() } returns Unit
        activity.validatePrinterEnabled()
        verify { activity.validatePrinterEnabled() }
        confirmVerified(activity)
    }

    @Test
    fun validateHardwareEnabled() {
        val activity = mockk<HomeActivity>()
        every { activity.validateHardwareEnabled() } returns Unit
        activity.validateHardwareEnabled()
        verify { activity.validateHardwareEnabled() }
        confirmVerified(activity)
    }

    @Test
    fun validateBannerTimerEnabled() {
        val activity = mockk<HomeActivity>()
        every { activity.validateBannerTimerEnabled() } returns Unit
        activity.validateBannerTimerEnabled()
        verify { activity.validateBannerTimerEnabled() }
        confirmVerified(activity)
    }

    @Test
    fun validateCashModuleRes() {
        val activity = mockk<HomeActivity>()
        every { activity.validateCashModuleRes("123") } returns Unit
        activity.validateCashModuleRes("123")
        verify { activity.validateCashModuleRes("123") }
        confirmVerified(activity)
    }
}
