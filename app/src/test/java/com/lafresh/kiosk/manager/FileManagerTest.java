package com.lafresh.kiosk.manager;

import android.content.SharedPreferences;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class FileManagerTest {
    private static SharedPreferences sharedPreferences;
    private static Map mockMap;

    @BeforeClass
    public static void setUp() {
        sharedPreferences = Mockito.mock(SharedPreferences.class);
        mockMap = Mockito.mock(Map.class);
        Mockito.when(sharedPreferences.getAll()).thenReturn(mockMap);
    }

    @Test
    public void isSharedPreferenceHasData() {
        Mockito.when(mockMap.size()).thenReturn(2);
        Assert.assertTrue(FileManager.isSharedPreferenceHasData(sharedPreferences));
    }

    @Test
    public void isSharedPreferenceHasDataWhenNoData() {
        Mockito.when(mockMap.size()).thenReturn(0);
        Assert.assertFalse(FileManager.isSharedPreferenceHasData(sharedPreferences));
    }

    @Test
    public void clearSpData() {
        SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(sharedPreferences.edit()).thenReturn(editor);
        Mockito.when(editor.clear()).thenReturn(editor);
        FileManager.clearSpData(sharedPreferences);
        Mockito.verify(editor).apply();
    }
}