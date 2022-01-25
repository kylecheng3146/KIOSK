package com.lafresh.kiosk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyUtil {

    /**
     * 驗證手機載具號碼
     *
     * @param number 由Code39組成，總長度為8碼字元
     *               第一碼必為『/』
     *               其餘七碼則由數字【0-9】、大寫英文【A-Z】與特殊符號【.】【-】【+】組成
     * @return 是否符合規則
     */
    public static boolean isPhoneCarrierNumber(String number) {
        if (number == null || number.length() != 8) {
            return false;
        }

        String regularExpression = "^/[0-9A-Z-+.]{7}$";
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(number);

        LogUtils.INSTANCE.info("matcher.matches__"+matcher.matches());
        LogUtils.INSTANCE.info("number:__"+number);
        return matcher.matches();
    }

    /**
     * 驗證捐贈碼
     *
     * @param code 總長度為3至7碼字元
     *             全部由數字【0-9】組成
     * @return 是否符合規則
     */
    public static boolean isLoveCode(String code) {
        if (code == null || code.length() < 3 || code.length() > 7) {
            return false;
        }

        String regularExpression = "^[0-9]{3,7}$";
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(code);

        return matcher.matches();
    }

    /**
     * 驗證統一編號
     *
     * @param number 總長度為8碼
     *               各數字分別乘以 1,2,1,2,1,2,4,1
     *               並將十位數與個位數全部結果值加總
     *               第一種:加總值取10的餘數為0
     *               第二種:統編的第7碼為7，加總值+1 再取10的餘數為0
     * @return 是否符合規則
     */
    public static boolean isUnifiedBusinessNumber(String number) {
        if (number == null || number.length() != 8) {
            return false;
        }

        int numberSum;
        try {
            numberSum = calculateSumByBusinessNumberRule(number);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (numberSum % 10 == 0) {
            return true;
        }

        boolean isSpecialCase = number.charAt(6) == 55;//第7碼為7
        if (isSpecialCase) {
            return (numberSum + 1) % 10 == 0;
        }

        return false;
    }

    private static int calculateSumByBusinessNumberRule(String number) throws Exception {
        int[] seed = {1, 2, 1, 2, 1, 2, 4, 1};
        int numberSum = 0;
        String[] numberArray = number.split("(?!^)");
        for (int i = 0; i < 8; i++) {
            if (isNumber(number.charAt(i))) {
                int n = Integer.parseInt(numberArray[i]);
                n = n * seed[i];
                numberSum += ((n / 10) + (n % 10));
            } else {
                throw new Exception("UnifiedBusinessNumber Contain Not Number Digit");
            }
        }
        return numberSum;
    }

    public static boolean isNumber(char c) {
        return c >= 48 && c <= 57;
    }

    public static boolean isLegalBlcFileName(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return false;
        }

        return fileName.startsWith("BLC") && fileName.endsWith(".BIG");
    }
}
