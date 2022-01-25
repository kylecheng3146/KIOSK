package com.lafresh.kiosk.utils

import android.content.Context

/**
 * Created by Kyle on 2021/5/3.
 */
object SharePrefsUtils {
    private const val FILE_NAME = "cashmodule"
    fun putTenDollarInventory(context: Context, tenDollar: Int) { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.取得寫入至 xml 物件
        val edit = pref.edit()
        // 3.寫入資料
        edit.putInt("tenDollar", tenDollar)
        // 4.確認寫入
        edit.apply()
    }

    fun getTenDollarInventory(context: Context): Int { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.讀取資料
        return pref.getInt("tenDollar", 0)
    }

    fun putFiveDollarInventory(context: Context, fiveDollar: Int) { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.取得寫入至 xml 物件
        val edit = pref.edit()
        // 3.寫入資料
        edit.putInt("fiveDollar", fiveDollar)
        // 4.確認寫入
        edit.apply()
    }

    fun getFiveDollarInventory(context: Context): Int { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.讀取資料
        return pref.getInt("fiveDollar", 0)
    }

    fun putFiftyDollarInventory(context: Context, fiftyDollar: Int) { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.取得寫入至 xml 物件
        val edit = pref.edit()
        // 3.寫入資料
        edit.putInt("fiftyDollar", fiftyDollar)
        // 4.確認寫入
        edit.apply()
    }

    fun getFiftyDollarInventory(context: Context): Int { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.讀取資料
        return pref.getInt("fiftyDollar", 0)
    }

    fun putOneDollarInventory(context: Context, oneDollar: Int) { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.取得寫入至 xml 物件
        val edit = pref.edit()
        // 3.寫入資料
        edit.putInt("oneDollar", oneDollar)
        // 4.確認寫入
        edit.apply()
    }

    fun getOneDollarInventory(context: Context): Int { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.讀取資料
        return pref.getInt("oneDollar", 0)
    }

    fun putHundredInventory(context: Context, hundred: Int) { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.取得寫入至 xml 物件
        val edit = pref.edit()
        // 3.寫入資料
        edit.putInt("hundred", hundred)
        // 4.確認寫入
        edit.apply()
    }

    fun getHundredInventory(context: Context): Int { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.讀取資料
        return pref.getInt("hundred", 0)
    }

    fun putPortSettingDone(context: Context, isDone: Boolean) { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.取得寫入至 xml 物件
        val edit = pref.edit()
        // 3.寫入資料
        edit.putBoolean("isDone", isDone)
        // 4.確認寫入
        edit.apply()
    }

    fun getPortSettingDone(context: Context): Boolean { // 1.產生SharedPreference
        val pref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        // 2.讀取資料
        return pref.getBoolean("isDone", false)
    }
}
