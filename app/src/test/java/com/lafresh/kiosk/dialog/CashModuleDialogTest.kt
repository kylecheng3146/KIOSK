package com.lafresh.kiosk.dialog

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * Created by Kyle on 2021/7/21.
 */
class CashModuleDialogTest {
    @Test
    fun setUI() {
        val activity = mockk<CashModuleDialog>()
        every { activity.setUI() } returns Unit
        activity.setUI()
        verify { activity.setUI() }
        confirmVerified(activity)
    }

    @Test
    fun setActions() {
        val activity = mockk<CashModuleDialog>()
        every { activity.setActions() } returns Unit
        activity.setActions()
        verify { activity.setActions() }
        confirmVerified(activity)
    }

    @Test
    fun vaildateMoneyCount() {
        val activity = mockk<CashModuleDialog>()
        every { activity.vaildateMoneyCount() } returns false
        activity.vaildateMoneyCount()
        verify { activity.vaildateMoneyCount() }
        confirmVerified(activity)
    }
}
