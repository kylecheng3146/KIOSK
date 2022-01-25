package com.lafresh.kiosk.utils;

/**
 * 防止快速點點
 */

public class ClickUtil {
    private final static int CLICK_INTERVAL = 600;
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < CLICK_INTERVAL) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
