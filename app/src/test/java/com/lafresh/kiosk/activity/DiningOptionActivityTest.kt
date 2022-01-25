package com.lafresh.kiosk.activity

import com.lafresh.kiosk.httprequest.model.OrderResponse
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * Created by Kyle on 2021/3/5.
 */
class DiningOptionActivityTest {
    @Test
    fun checkTableNo() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.checkTableNo(any()) } returns Unit
        val orderResponse = OrderResponse()
        activity.checkTableNo("123")
        verify { activity.checkTableNo("123") }
        confirmVerified(activity)
    }

    @Test
    fun setUI() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.setUI() } returns Unit
        activity.setUI()
        verify { activity.setUI() }
        confirmVerified(activity)
    }

    @Test
    fun setActions() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.setActions() } returns Unit
        activity.setActions()
        verify { activity.setActions() }
        confirmVerified(activity)
    }

    @Test
    fun checkMealWay() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.checkMealWay() } returns Unit
        activity.checkMealWay()
        verify { activity.checkMealWay() }
        confirmVerified(activity)
    }

    @Test
    fun checkProject() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.checkProject() } returns Unit
        activity.checkProject()
        verify { activity.checkProject() }
        confirmVerified(activity)
    }

    @Test
    fun checkShopHasPoints() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.checkShopHasPoints() } returns Unit
        activity.checkShopHasPoints()
        verify { activity.checkShopHasPoints() }
        confirmVerified(activity)
    }

    @Test
    fun setSingleSaleMethod() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.setSingleSaleMethod() } returns Unit
        activity.setSingleSaleMethod()
        verify { activity.setSingleSaleMethod() }
        confirmVerified(activity)
    }

    @Test
    fun inputTableNo() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.inputTableNo() } returns Unit
        activity.inputTableNo()
        verify { activity.inputTableNo() }
        confirmVerified(activity)
    }

    @Test
    fun toScanPage() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.toScanPage("123") } returns Unit
        activity.toScanPage("123")
        verify { activity.toScanPage("123") }
        confirmVerified(activity)
    }

    @Test
    fun toShopPage() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.toShopPage() } returns Unit
        activity.toShopPage()
        verify { activity.toShopPage() }
        confirmVerified(activity)
    }

    @Test
    fun validateProjectFlow() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.validateProjectFlow() } returns Unit
        activity.validateProjectFlow()
        verify { activity.validateProjectFlow() }
        confirmVerified(activity)
    }

    @Test
    fun validateIsLogged() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.validateIsLogged() } returns Unit
        activity.validateIsLogged()
        verify { activity.validateIsLogged() }
        confirmVerified(activity)
    }

    @Test
    fun validateFlavorType() {
        val activity = mockk<DiningOptionActivity>()
        every { activity.validateFlavorType() } returns Unit
        activity.validateFlavorType()
        verify { activity.validateFlavorType() }
        confirmVerified(activity)
    }
}
