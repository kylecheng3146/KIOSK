package com.lafresh.kiosk.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Base64Util {
    public static String encodeAESBase64(String key, String data) {
        String return_data = "";
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            return_data = encodeBase64(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return return_data;
    }

    public static String encodeBase64(byte[] bytes) {
        return new String(Base64.encode(bytes, Base64.NO_WRAP), StandardCharsets.UTF_8);
    }

    public static String decodeAESBase64(String key, String data) {
        String return_data = "";
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return_data = new String(cipher.doFinal(decodeBase64Byte(data)));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return return_data;
    }

    public static byte[] decodeBase64Byte(String message) {
        return Base64.decode(message, Base64.DEFAULT);
    }
}
