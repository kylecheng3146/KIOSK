package com.lafresh.app.kioska;


import android.content.Context;
import android.content.SharedPreferences;

import com.lafresh.kiosk.easycard.EasyCard;
import com.lafresh.kiosk.easycard.model.EasyCardData;
import com.lafresh.kiosk.utils.TimeUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;

public class TestEasyCard {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String notToday = "2000-01-01";
    private String today = TimeUtil.getToday();

    @Before
    public void setUp() {
        context = Mockito.mock(Context.class);
        sharedPreferences = Mockito.mock(SharedPreferences.class);
        editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
    }

    @Test
    public void testIsCheckoutToday() {
        EasyCardData easyCardData = new EasyCardData();
        Assert.assertFalse(EasyCard.isCheckoutToday(easyCardData));
        easyCardData.checkoutDate = notToday;
        Assert.assertFalse(EasyCard.isCheckoutToday(easyCardData));
        easyCardData.checkoutDate = today;
        Assert.assertTrue(EasyCard.isCheckoutToday(easyCardData));
    }

    @Test
    public void testUpdateCheckoutDate() {
        EasyCardData easyCardData = new EasyCardData();
        easyCardData.tradeNo = "0";
        easyCardData.batchNo = notToday.substring(2).replace("-", "") + "01";
        EasyCard.updateCheckoutDate(easyCardData);
        Assert.assertEquals(today, easyCardData.checkoutDate);
        Assert.assertEquals(today.substring(2).replace("-", "") + "01", easyCardData.batchNo);
        Assert.assertEquals("000001", easyCardData.tradeNo);
    }

    @Test
    public void testIsTodayBlackList() {
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn("");
        Assert.assertFalse(EasyCard.isTodayBlackList(context));
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn(notToday);
        Assert.assertFalse(EasyCard.isTodayBlackList(context));
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn(today);
        Assert.assertTrue(EasyCard.isTodayBlackList(context));
    }

    @Test
    public void testUpdateBlackListDate() {
        Mockito.when(sharedPreferences.edit()).thenReturn(editor);
        EasyCard.updateBlackListDate(context);
        Mockito.verify(editor).apply();
    }
}
