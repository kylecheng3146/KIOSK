package com.lafresh.kiosk.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

import java.math.BigDecimal;

public class ComputationUtils {

    static boolean isDouble(String str) {
        if (str == null || str.length() <= 0) {
            return false;
        }

        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static double parseDouble(String value) {
        if (isDouble(value)) {
            return Double.parseDouble(value);
        }
        return 0;
    }

    static boolean isInt(String str) {
        if (str == null || str.length() <= 0) {
            return false;
        }

        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static int parseInt(String value) {
        if (isInt(value)) {
            return Integer.parseInt(value);
        }
        return 0;
    }


    public static int parsePXtoDP(Activity activity, int px) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float scale = dm.density;
        float dp = px / scale;
        BigDecimal dec = new BigDecimal(dp);
        dec = dec.setScale(0, BigDecimal.ROUND_HALF_UP);
        return dec.intValue();
    }

    public static int parseDPtoPX(Activity activity, int dp) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float scale = dm.density;
        float px = dp * scale;
        BigDecimal dec = new BigDecimal(px);
        dec = dec.setScale(0, BigDecimal.ROUND_HALF_UP);
        return dec.intValue();
    }

    public static int parseSPtoPT(float sp) {
        float px = sp / (160f / 72);
        BigDecimal dec = new BigDecimal(px);
        dec = dec.setScale(0, BigDecimal.ROUND_HALF_UP);
        return dec.intValue();
    }

    public static int parsePTtoSP(float pt) {
        float px = pt * (160f / 72);
        BigDecimal dec = new BigDecimal(px);
        dec = dec.setScale(0, BigDecimal.ROUND_HALF_UP);
        return dec.intValue();
    }

    static boolean isNotZero(String value) {
        if (isDouble(value)) {
            double value_double = parseDouble(value);
            return value_double != 0;
        } else {
            return true;
        }
    }
}
