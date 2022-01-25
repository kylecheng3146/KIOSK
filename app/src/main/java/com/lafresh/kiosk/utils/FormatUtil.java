package com.lafresh.kiosk.utils;

import java.text.DecimalFormat;

public class FormatUtil {

    public static String addLeftSpaceTillLength(String originString, int length) {
        StringBuilder newStringBuilder = new StringBuilder(originString);
        while (newStringBuilder.length() < length) {
            newStringBuilder.insert(0, " ");
        }
        return newStringBuilder.toString();
    }

    public static String removeDot(String s) {
        return removeDot(Double.parseDouble(s));
    }

    public static String removeDot(double d) {
        DecimalFormat format = new DecimalFormat("0");
        return format.format(d);
    }

    /**
     * <p>Left pad a String with spaces.</p>
     *
     * <p>The String is padded to the size of <code>n</code>.</p>
     *
     * @param str  String to pad out
     * @param size size to pad to
     * @return left padded String
     * @throws NullPointerException if str or delim is <code>null</code>
     */
    public static String leftPad(String str, int size) {
        return leftPad(str, size, " ");
    }

    /**
     * Left pad a String with a specified string. Pad to a size of n.
     *
     * @param str       String to pad out
     * @param size      size to pad to
     * @param delimiter String to pad with
     * @return left padded String
     * @throws NullPointerException if str or delim is null
     * @throws ArithmeticException  if delim is the empty string
     */
    public static String leftPad(String str, int size, String delimiter) {
        size = (size - str.length()) / delimiter.length();
        if (size > 0) {
            str = repeatString(delimiter, size) + str;
        }
        return str;
    }

    /**
     * <p>Right pad a String with spaces.</p>
     *
     * <p>The String is padded to the size of <code>n</code>.</p>
     *
     * @param str  String to repeat
     * @param size number of times to repeat str
     * @return right padded String
     * @throws NullPointerException if str is <code>null</code>
     */
    public static String rightPad(String str, int size) {
        return rightPad(str, size, " ");
    }

    /**
     * <p>Right pad a String with a specified string.</p>
     *
     * <p>The String is padded to the size of <code>n</code>.</p>
     *
     * @param str       String to pad out
     * @param size      size to pad to
     * @param delimiter String to pad with
     * @return right padded String
     * @throws NullPointerException if str or delim is <code>null</code>
     * @throws ArithmeticException  if delim is the empty String
     */
    public static String rightPad(String str, int size, String delimiter) {
        size = (size - str.length()) / delimiter.length();
        if (size > 0) {
            str += repeatString(delimiter, size);
        }
        return str;
    }

    /**
     * <p>Repeat a String <code>n</code> times to form a
     * new string.</p>
     *
     * @param str    String to repeat
     * @param repeat number of times to repeat str
     * @return String with repeated String
     * @throws NegativeArraySizeException if <code>repeat < 0</code>
     * @throws NullPointerException       if str is <code>null</code>
     */
    public static String repeatString(String str, int repeat) {
        StringBuilder builder = new StringBuilder(repeat * str.length());
        for (int i = 0; i < repeat; i++) {
            builder.append(str);
        }
        return builder.toString();
    }

    public static String toEightDigitHexString(int number) {
        StringBuilder hexString = new StringBuilder(Integer.toHexString(number));
        while (hexString.length() < 8) {
            hexString.insert(0, "0");
        }
        return hexString.toString();
    }


    public static String parseRocYear(String year) {
        int rocYear = -1911;
        try {
            int yearInt = Integer.parseInt(year);
            rocYear += yearInt;
        } catch (NumberFormatException nfe) {
            return "非民國年";
        }
        return rocYear + "";
    }
}








