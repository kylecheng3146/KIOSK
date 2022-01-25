package com.lafresh.kiosk.activity

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * Created by Kyle on 2021/3/17.
 */

class EasyCardActivityTest {

    @Test
    fun setUI() {
        val activity = mockk<EasyCardActivity>()
        every { activity.setUI() } returns Unit
        activity.setUI()
        verify { activity.setUI() }
        confirmVerified(activity)
    }

    @Test
    fun setActions() {
        val activity = mockk<EasyCardActivity>()
        every { activity.setActions() } returns Unit
        activity.setActions()
        verify { activity.setActions() }
        confirmVerified(activity)
    }

    @Test
    fun submitBill() {
        val activity = mockk<EasyCardActivity>()
        every { activity.submitBill() } returns Unit
        activity.submitBill()
        verify { activity.submitBill() }
        confirmVerified(activity)
    }
}
