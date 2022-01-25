package com.lafresh.kiosk.dialog

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * Created by Kyle on 2021/7/21.
 */
class CashEmptyDialogTest {
    @Test
    fun setUI() {
        val activity = mockk<CashEmptyDialog>()
        every { activity.setUI() } returns Unit
        activity.setUI()
        verify { activity.setUI() }
        confirmVerified(activity)
    }

    @Test
    fun setActions() {
        val activity = mockk<CashEmptyDialog>()
        every { activity.setActions() } returns Unit
        activity.setActions()
        verify { activity.setActions() }
        confirmVerified(activity)
    }

    @Test
    fun validateCashIsFull() {
        val activity = mockk<CashEmptyDialog>()
        every { activity.validateCashIsFull() } returns Unit
        activity.validateCashIsFull()
        verify { activity.validateCashIsFull() }
        confirmVerified(activity)
    }
}
