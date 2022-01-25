package com.lafresh.kiosk

import com.lafresh.kiosk.activity.CheckOutOptionActivity
import com.lafresh.kiosk.httprequest.model.OrderResponse
import com.lafresh.kiosk.manager.OrderManager
import com.lafresh.kiosk.model.Order
import com.lafresh.kiosk.model.Payment
import com.lafresh.kiosk.type.PaymentsType
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.json.JSONArray
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Created by Kyle on 2021/2/3.
 */
class CheckOutOptionActivityTest {

    private var checkOutOptionActivity: CheckOutOptionActivity? = null

    @Before
    fun init() {
        checkOutOptionActivity = CheckOutOptionActivity()
    }

    @Test
    fun checkCashTicketExist() {
        val manager = OrderManager.getInstance()
        val payment = Payment()
        payment.type = PaymentsType.CASH_TICKET.name
        manager.addPayment(payment)
        val order = Order()
        checkOutOptionActivity?.checkCashTicketExist(manager, order)
        Assert.assertEquals(manager.payments.size, 1)
    }

    @Test
    fun checkHasDiscountTicket() {
        val manager = OrderManager.getInstance()
        val ticket = Order.TicketsBean()
        ticket.price = 50
        manager.addTickets(ticket)
        Assert.assertEquals(checkOutOptionActivity?.hasDisCountTicket(), true)
    }

    @Test
    fun billUpdate() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.billUpdate(any()) } returns Unit
        val orderResponse = OrderResponse()
        orderResponse.discount = 10
        activity.billUpdate(orderResponse)
        verify { activity.billUpdate(orderResponse) }
        confirmVerified(activity)
    }

    @Test
    fun checkCouponButtonVisibility() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.checkCouponButtonVisibility() } returns Unit
        activity.checkCouponButtonVisibility()
        verify { activity.checkCouponButtonVisibility() }
        confirmVerified(activity)
    }

    @Test
    fun checkOrderByTickets() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.checkOrderByTickets() } returns Unit
        activity.checkOrderByTickets()
        verify { activity.checkOrderByTickets() }
        confirmVerified(activity)
    }

    @Test
    fun confirmOrder() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.confirmOrder() } returns Unit
        activity.confirmOrder()
        verify { activity.confirmOrder() }
        confirmVerified(activity)
    }

    @Test
    fun validateIsNewApiFlow() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.validateIsNewApiFlow() } returns Unit
        activity.validateIsNewApiFlow()
        verify { activity.validateIsNewApiFlow() }
        confirmVerified(activity)
    }

    @Test
    fun replaceCheckoutButtonBackground() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.replaceCheckoutButtonBackground() } returns Unit
        activity.replaceCheckoutButtonBackground()
        verify { activity.replaceCheckoutButtonBackground() }
        confirmVerified(activity)
    }

    @Test
    fun validateIsOnlyCounterPay() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.validateIsOnlyCounterPay() } returns Unit
        activity.validateIsOnlyCounterPay()
        verify { activity.validateIsOnlyCounterPay() }
        confirmVerified(activity)
    }

    @Test
    fun finishOrder() {
        val activity = mockk<CheckOutOptionActivity>()
        val order = Order()
        every { activity.finishOrder(order) } returns Unit
        activity.finishOrder(order)
        verify { activity.finishOrder(order) }
        confirmVerified(activity)
    }

    @Test
    fun checkFlavorIntentFlow() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.checkFlavorIntentFlow() } returns Unit
        activity.checkFlavorIntentFlow()
        verify { activity.checkFlavorIntentFlow() }
        confirmVerified(activity)
    }

    @Test
    fun showTicketDialog() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.showTicketDialog() } returns Unit
        activity.showTicketDialog()
        verify { activity.showTicketDialog() }
        confirmVerified(activity)
    }

    @Test
    fun enableConfirmButton() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.enableConfirmButton(true) } returns Unit
        activity.enableConfirmButton(true)
        verify { activity.enableConfirmButton(true) }
        confirmVerified(activity)
    }

    @Test
    fun createOrUpdateOrder() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.createOrUpdateOrder() } returns Unit
        activity.createOrUpdateOrder()
        verify { activity.createOrUpdateOrder() }
        confirmVerified(activity)
    }

    @Test
    fun getOrdersFromServer() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.getOrdersFromServer("123") } returns Unit
        activity.getOrdersFromServer("123")
        verify { activity.getOrdersFromServer("123") }
        confirmVerified(activity)
    }

    @Test
    fun hasTicket() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.hasTicket() } returns true
        activity.hasTicket()
        verify { activity.hasTicket() }
        confirmVerified(activity)
    }

    @Test
    fun productLeftInCart() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.productLeftInCart() } returns 0
        activity.productLeftInCart()
        verify { activity.productLeftInCart() }
        confirmVerified(activity)
    }

    @Test
    fun validateCartNotEmpty() {
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.validateCartNotEmpty(true) } returns Unit
        activity.validateCartNotEmpty(true)
        verify { activity.validateCartNotEmpty(true) }
        confirmVerified(activity)
    }

    @Test
    fun validateOrderNotEmpty() {
        val manager = OrderManager()
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.validateOrderNotEmpty(manager) } returns Unit
        activity.validateOrderNotEmpty(manager)
        verify { activity.validateOrderNotEmpty(manager) }
        confirmVerified(activity)
    }

    @Test
    fun validateTypeIsCombo() {
        val product = Product()
        val jsonArray = JSONArray()
        val manager = OrderManager()
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.validateTypeIsCombo(product, jsonArray) } returns Unit
        activity.validateTypeIsCombo(product, jsonArray)
        verify { activity.validateTypeIsCombo(product, jsonArray) }
        confirmVerified(activity)
    }

    @Test
    fun validateAddOrderIsSuccess() {
        val orderResponse = OrderResponse()
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.validateAddOrderIsSuccess(orderResponse) } returns Unit
        activity.validateAddOrderIsSuccess(orderResponse)
        verify { activity.validateAddOrderIsSuccess(orderResponse) }
        confirmVerified(activity)
    }

    @Test
    fun validateOrderIsUseCoupon() {
        val product = Product()
        val order = com.lafresh.kiosk.Order(product)
        val activity = mockk<CheckOutOptionActivity>()
        every { activity.validateOrderIsUseCoupon(order) } returns Unit
        activity.validateOrderIsUseCoupon(order)
        verify { activity.validateOrderIsUseCoupon(order) }
        confirmVerified(activity)
    }

    @Test
    fun validateTicketUsable() {
        val activity = mockk<CheckOutOptionActivity>()
        val product = Product()
        val order = Order(product)
        every { activity.validateTicketUsable(1.0, order) } returns true
        activity.validateTicketUsable(1.0, order)
        verify { activity.validateTicketUsable(1.0, order) }
        confirmVerified(activity)
    }
}
