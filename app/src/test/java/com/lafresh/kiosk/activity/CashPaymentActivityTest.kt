package com.lafresh.kiosk.activity

import com.lafresh.kiosk.Order
import com.lafresh.kiosk.fragment.AlertDialogFragment
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * Created by Kyle on 2021/5/26.
 */
class CashPaymentActivityTest {
    @Test
    fun setUI() {
        val activity = mockk<CashPaymentActivity>()
        every { activity.setUI() } returns Unit
        activity.setUI()
        verify { activity.setUI() }
        confirmVerified(activity)
    }

    @Test
    fun setActions() {
        val activity = mockk<CashPaymentActivity>()
        every { activity.setActions() } returns Unit
        activity.setActions()
        verify { activity.setActions() }
        confirmVerified(activity)
    }

    @Test
    fun confirmOrder() {
        val activity = mockk<CashPaymentActivity>()
        every { activity.confirmOrder() } returns Unit
        activity.confirmOrder()
        verify { activity.confirmOrder() }
        confirmVerified(activity)
    }

    @Test
    fun finishOrder() {
        val activity = mockk<CashPaymentActivity>()
        val order = com.lafresh.kiosk.model.Order()
        every { activity.finishOrder(order) } returns Unit
        activity.finishOrder(order)
        verify { activity.finishOrder(order) }
    }

    @Test
    fun interruptAllThread() {
        val activity = mockk<CashPaymentActivity>()
        every { activity.interruptAllThread() } returns Unit
        activity.interruptAllThread()
        verify { activity.interruptAllThread() }
        confirmVerified(activity)
    }

    @Test
    fun showDialog() {
        val activity = mockk<CashPaymentActivity>()
        every { activity.showDialog(123, 123, false) } returns AlertDialogFragment()
        activity.showDialog(123, 123, false)
        verify { activity.showDialog(123, 123, false) }
        confirmVerified(activity)
    }

    @Test
    fun addPayment() {
        val activity = mockk<CashPaymentActivity>()
        every { activity.addPayment() } returns Unit
        activity.addPayment()
        verify { activity.addPayment() }
        confirmVerified(activity)
    }

    @Test
    fun validateNewApiFlow() {
        val activity = mockk<CashPaymentActivity>()
        every { activity.validateNewApiFlow() } returns Unit
        activity.validateNewApiFlow()
        verify { activity.validateNewApiFlow() }
        confirmVerified(activity)
    }
}
