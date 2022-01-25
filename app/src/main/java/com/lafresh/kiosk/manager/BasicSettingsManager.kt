package com.lafresh.kiosk.manager

import com.lafresh.kiosk.model.BasicSettings

/**
 * Created by Kyle on 2020/11/26.
 */

class BasicSettingsManager {

    private var basicSettings: BasicSettings? = null

    companion object {
        @JvmStatic
        val instance = BasicSettingsManager()
    }

    fun getBasicSetting(): BasicSettings? {
        return basicSettings

    }

    fun setBasicSetting(basicSettings: BasicSettings?) {
        this.basicSettings = basicSettings
    }
}
