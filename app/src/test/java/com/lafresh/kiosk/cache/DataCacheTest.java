package com.lafresh.kiosk.cache;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class DataCacheTest {
    private Context context;
    private SharedPreferences sharedPreferences;
    private DataCache<FakeObject> dataCache;

    @Before
    public void setup() {
        context = Mockito.mock(Context.class);
        sharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        dataCache = new DataCache<>();
    }

    @Test
    public void set() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(sharedPreferences.edit()).thenReturn(editor);
        Mockito.when(editor.putString(anyString(), anyString())).thenReturn(editor);
        dataCache.set(context, FakeObject.class, "test");
        Mockito.verify(editor).apply();
    }

    @Test
    public void get() {
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn("{\"i\":1}");
        FakeObject fakeObject = dataCache.get(context, FakeObject.class);
        Assert.assertNotNull(fakeObject);
    }

    @Test
    public void getReturnNull() {
        Mockito.when(sharedPreferences.getString(anyString(), anyString())).thenReturn("");
        FakeObject fakeObject = dataCache.get(context, FakeObject.class);
        Assert.assertNull(fakeObject);
    }

    private static class FakeObject extends DataCache.CacheClass {
        int i;
    }
}