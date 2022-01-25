package com.lafresh.kiosk.utils

import com.lafresh.kiosk.BuildConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**
 * Created by Kyle on 2020/12/31.
 */
object LogUtils {
    private val LOG = BuildConfig.DEBUG
    fun info(string: String?) {
        if (LOG) {
            Logger.addLogAdapter(AndroidLogAdapter())
            try {
                Logger.i(string!!)
            } catch (e: Exception) {
                Logger.i(e.message.toString())
            }
            Logger.clearLogAdapters()
        }
    }

    fun error(string: String?) {
        if (LOG) {
            Logger.addLogAdapter(AndroidLogAdapter())
            try {
                Logger.e(string!!)
            } catch (e: Exception) {
            }
            Logger.clearLogAdapters()
        }
    }

    fun debug(string: String?) {
        if (LOG) {
            Logger.addLogAdapter(AndroidLogAdapter())
            try {
                Logger.d(string)
            } catch (e: Exception) {
            }
            Logger.clearLogAdapters()
        }
    }

    fun verbose(string: String?) {
        if (LOG) {
            Logger.addLogAdapter(AndroidLogAdapter())
            Logger.v(string!!)
            Logger.clearLogAdapters()
        }
    }

    fun warn(string: String?) {
        if (LOG) {
            Logger.addLogAdapter(AndroidLogAdapter())
            Logger.w(string!!)
            Logger.clearLogAdapters()
        }
    }

    fun json(string: String?) {
        if (LOG) {
            Logger.addLogAdapter(AndroidLogAdapter())
            try {
                Logger.json(string)
            } catch (e: Exception) {
            }
            Logger.clearLogAdapters()
        }
    }
}
