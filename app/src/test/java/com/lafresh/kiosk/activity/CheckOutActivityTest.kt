package com.lafresh.kiosk.activity

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * Created by Kyle on 2021/3/5.
 */
class CheckOutActivityTest {
    @Test
    fun getPaymentsApi() {
        val activity = mockk<CheckOutActivity>()
        every { activity.getPaymentsApi() } returns Unit
        activity.getPaymentsApi()
        verify { activity.getPaymentsApi() }
        confirmVerified(activity)
    }

    @Test
    fun showErrorDialog() {
        val activity = mockk<CheckOutActivity>()
        every { activity.showErrorDialog(any(), any()) } returns Unit
        activity.showErrorDialog(1, 1)
        verify { activity.showErrorDialog(1, 1) }
        confirmVerified(activity)
    }

    @Test
    fun checkEarnPointVisibility() {
        val activity = mockk<CheckOutActivity>()
        every { activity.checkEarnPointVisibility() } returns Unit
        activity.checkEarnPointVisibility()
        verify { activity.checkEarnPointVisibility() }
        confirmVerified(activity)
    }

    @Test
    fun setUI() {
        val activity = mockk<CheckOutActivity>()
        every { activity.setUI() } returns Unit
        activity.setUI()
        verify { activity.setUI() }
        confirmVerified(activity)
    }

    @Test
    fun setActions() {
        val activity = mockk<CheckOutActivity>()
        every { activity.setActions() } returns Unit
        activity.setActions()
        verify { activity.setActions() }
        confirmVerified(activity)
    }

    @Test
    fun toPaymentPage() {
        val activity = mockk<CheckOutActivity>()
        every { activity.toPaymentPage("test") } returns Unit
        activity.toPaymentPage("test")
        verify { activity.toPaymentPage("test") }
        confirmVerified(activity)
    }

    @Test
    fun addPaymentInfo() {
        val activity = mockk<CheckOutActivity>()
        every { activity.addPaymentInfo("test") } returns Unit
        activity.addPaymentInfo("test")
        verify { activity.addPaymentInfo("test") }
        confirmVerified(activity)
    }

    @Test
    fun countPayment() {
        val activity = mockk<CheckOutActivity>()
        every { activity.countPayment() } returns Unit
        activity.countPayment()
        verify { activity.countPayment() }
        confirmVerified(activity)
    }

    @Test
    fun setNumbers() {
        val activity = mockk<CheckOutActivity>()
        every { activity.setNumbers() } returns Unit
        activity.setNumbers()
        verify { activity.setNumbers() }
        confirmVerified(activity)
    }

    @Test
    fun checkEditTextHasText() {
        val activity = mockk<CheckOutActivity>()
        every { activity.checkEditTextHasText() } returns Unit
        activity.checkEditTextHasText()
        verify { activity.checkEditTextHasText() }
        confirmVerified(activity)
    }

    @Test
    fun disableEasyCardButton() {
        val activity = mockk<CheckOutActivity>()
        every { activity.disableEasyCardButton() } returns Unit
        activity.disableEasyCardButton()
        verify { activity.disableEasyCardButton() }
        confirmVerified(activity)
    }

    @Test
    fun disableNcccButton() {
        val activity = mockk<CheckOutActivity>()
        every { activity.disableNcccButton() } returns Unit
        activity.disableNcccButton()
        verify { activity.disableNcccButton() }
        confirmVerified(activity)
    }

    @Test
    fun validateEnabledDemoEnv() {
        val activity = mockk<CheckOutActivity>()
        every { activity.validateEnabledDemoEnv() } returns Unit
        activity.validateEnabledDemoEnv()
        verify { activity.validateEnabledDemoEnv() }
        confirmVerified(activity)
    }

    @Test
    fun validateEnabledCreditCard() {
        val activity = mockk<CheckOutActivity>()
        every { activity.validateEnabledCreditCard() } returns Unit
        activity.validateEnabledCreditCard()
        verify { activity.validateEnabledCreditCard() }
        confirmVerified(activity)
    }

    @Test
    fun validateIsNewApi() {
        val activity = mockk<CheckOutActivity>()
        every { activity.validateIsNewApi() } returns Unit
        activity.validateIsNewApi()
        verify { activity.validateIsNewApi() }
        confirmVerified(activity)
    }

    @Test
    fun validateEasyCardHasRetry() {
        val activity = mockk<CheckOutActivity>()
        every { activity.validateEasyCardHasRetry(1, "123") } returns Unit
        activity.validateEasyCardHasRetry(1, "123")
        verify { activity.validateEasyCardHasRetry(1, "123") }
        confirmVerified(activity)
    }

    @Test
    fun validateIsTfgFlow() {
        val activity = mockk<CheckOutActivity>()
        every { activity.validateIsTfgFlow() } returns Unit
        activity.validateIsTfgFlow()
        verify { activity.validateIsTfgFlow() }
        confirmVerified(activity)
    }
}
