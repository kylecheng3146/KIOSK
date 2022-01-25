package com.lafresh.kiosk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class NetworkUtilsTest {

    @Test
    public void isOnline() {
        Context context = Mockito.mock(Context.class);
        ConnectivityManager cm = Mockito.mock(ConnectivityManager.class);
        Mockito.when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(cm);
        NetworkInfo netInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(cm.getActiveNetworkInfo()).thenReturn(netInfo);
        Mockito.when(netInfo.isConnectedOrConnecting()).thenReturn(true);
        Assert.assertTrue(NetworkUtils.isOnline(context));
    }

    @Test
    public void isOnlineWhenConnectivityManagerIsNull() {
        Context context = Mockito.mock(Context.class);
        Assert.assertFalse(NetworkUtils.isOnline(context));
    }

    @Test
    public void isOnlineWhenNetworkInfoIsNull() {
        Context context = Mockito.mock(Context.class);
        ConnectivityManager cm = Mockito.mock(ConnectivityManager.class);
        Mockito.when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(cm);
        Assert.assertFalse(NetworkUtils.isOnline(context));
    }

    @Test
    public void isOnlineWhenIsConnectedOrConnectingIsFalse() {
        Context context = Mockito.mock(Context.class);
        ConnectivityManager cm = Mockito.mock(ConnectivityManager.class);
        Mockito.when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(cm);
        NetworkInfo netInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(cm.getActiveNetworkInfo()).thenReturn(netInfo);
        Assert.assertFalse(NetworkUtils.isOnline(context));
    }
}