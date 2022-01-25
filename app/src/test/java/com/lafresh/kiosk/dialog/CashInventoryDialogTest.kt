package com.lafresh.kiosk.dialog

import com.lafresh.kiosk.model.ServerReceivedModel
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.json.JSONObject
import org.junit.Test

/**
 * Created by Kyle on 2021/7/6.
 */
class CashInventoryDialogTest {
    @Test
    fun validateIsDoorOpen() {
        val model = ServerReceivedModel("", "", "", 1, "", "")
        val activity = mockk<CashInventoryDialog>()
        every { activity.validateIsDoorOpen(model) } returns Unit
        activity.validateIsDoorOpen(model)
        verify { activity.validateIsDoorOpen(model) }
        confirmVerified(activity)
    }

    @Test
    fun validateIsDoorClose() {
        val model = ServerReceivedModel("", "", "", 1, "", "")
        val activity = mockk<CashInventoryDialog>()
        every { activity.validateIsDoorClose(model) } returns Unit
        activity.validateIsDoorClose(model)
        verify { activity.validateIsDoorClose(model) }
        confirmVerified(activity)
    }

    @Test
    fun validateResponseIsFail() {
        val activity = mockk<CashInventoryDialog>()
        every { activity.validateResponseIsFail("test") } returns false
        activity.validateResponseIsFail("test")
        verify { activity.validateResponseIsFail("test") }
        confirmVerified(activity)
    }

    @Test
    fun validateIsResetAll() {
        val activity = mockk<CashInventoryDialog>()
        every { activity.validateIsResetAll("test") } returns false
        activity.validateIsResetAll("test")
        verify { activity.validateIsResetAll("test") }
        confirmVerified(activity)
    }

    @Test
    fun validateIsAddMoney() {
        val activity = mockk<CashInventoryDialog>()
        every { activity.validateIsAddMoney("test") } returns false
        activity.validateIsAddMoney("test")
        verify { activity.validateIsAddMoney("test") }
        confirmVerified(activity)
    }

    @Test
    fun validateIsGetMoney() {
        val activity = mockk<CashInventoryDialog>()
        every { activity.validateIsGetMoney("test") } returns Unit
        activity.validateIsGetMoney("test")
        verify { activity.validateIsGetMoney("test") }
        confirmVerified(activity)
    }

    @Test
    fun addMoney() {
        val activity = mockk<CashInventoryDialog>()
        every { activity.addMoney("test", 1) } returns JSONObject()
        activity.addMoney("test", 1)
        verify { activity.addMoney("test", 1) }
        confirmVerified(activity)
    }

    @Test
    fun resetTextValue() {
        val activity = mockk<CashInventoryDialog>()
        every { activity.resetTextValue() } returns Unit
        activity.resetTextValue()
        verify { activity.resetTextValue() }
        confirmVerified(activity)
    }

    @Test
    fun validateMoneyCount() {
        val activity = mockk<CashInventoryDialog>()
        every { activity.validateMoneyCount() } returns false
        activity.validateMoneyCount()
        verify { activity.validateMoneyCount() }
        confirmVerified(activity)
    }

    @Test
    fun validateMoneyNotZero() {
        val activity = mockk<CashInventoryDialog>()
        every { activity.validateMoneyNotZero() } returns false
        activity.validateMoneyNotZero()
        verify { activity.validateMoneyNotZero() }
        confirmVerified(activity)
    }
}
