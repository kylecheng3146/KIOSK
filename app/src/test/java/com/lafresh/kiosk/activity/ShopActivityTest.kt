package com.lafresh.kiosk.activity

import com.lafresh.kiosk.model.ProductBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * Created by Kyle on 2021/4/21.
 */
class ShopActivityTest {
    @Test
    fun setUI() {
        val activity = mockk<ShopActivity>()
        every { activity.setUI() } returns Unit
        activity.setUI()
        verify { activity.setUI() }
        confirmVerified(activity)
    }

    @Test
    fun setActions() {
        val activity = mockk<ShopActivity>()
        every { activity.setActions() } returns Unit
        activity.setActions()
        verify { activity.setActions() }
        confirmVerified(activity)
    }

    @Test
    fun setPointStatus() {
        val activity = mockk<ShopActivity>()
        every { activity.setPointStatus(123, "123") } returns Unit
        activity.setPointStatus(123, "123")
        verify { activity.setPointStatus(123, "123") }
        confirmVerified(activity)
    }

    @Test
    fun showRecommendationFragment() {
        val activity = mockk<ShopActivity>()
        val products: List<ProductBean> = mutableListOf()
        every { activity.showRecommendationFragment(products) } returns Unit
        activity.showRecommendationFragment(products)
        verify {
            activity.showRecommendationFragment(products)
        }
        confirmVerified(activity)
    }

    @Test
    fun enableCheckoutButton() {
        val activity = mockk<ShopActivity>()
        every { activity.enableCheckoutButton(true) } returns Unit
        activity.enableCheckoutButton(true)
        verify { activity.enableCheckoutButton(true) }
        confirmVerified(activity)
    }

    @Test
    fun getProductCategory() {
        val activity = mockk<ShopActivity>()
        every { activity.getProductCategory() } returns Unit
        activity.getProductCategory()
        verify { activity.getProductCategory() }
        confirmVerified(activity)
    }

    @Test
    fun showErrorDialog() {
        val activity = mockk<ShopActivity>()
        every { activity.showErrorDialog() } returns Unit
        activity.showErrorDialog()
        verify { activity.showErrorDialog() }
        confirmVerified(activity)
    }

    @Test
    fun validateShowRecommendPConfig() {
        val activity = mockk<ShopActivity>()
        every { activity.validateShowRecommendPConfig() } returns Unit
        activity.validateShowRecommendPConfig()
        verify { activity.validateShowRecommendPConfig() }
        confirmVerified(activity)
    }

    @Test
    fun validateCartIsOverLimit() {
        val activity = mockk<ShopActivity>()
        every { activity.validateCartIsOverLimit(false) } returns Unit
        activity.validateCartIsOverLimit(false)
        verify { activity.validateCartIsOverLimit(false) }
        confirmVerified(activity)
    }

    @Test
    fun validateIsLogged() {
        val activity = mockk<ShopActivity>()
        every { activity.validateIsLogged() } returns Unit
        activity.validateIsLogged()
        verify { activity.validateIsLogged() }
        confirmVerified(activity)
    }
}
