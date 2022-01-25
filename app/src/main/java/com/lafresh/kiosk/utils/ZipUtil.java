package com.lafresh.kiosk.utils;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    private static final int BUFFER = 2048;

    public static void zip(String fromPath) throws IOException {
        File file = new File(fromPath);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file.getPath() + ".zip"));
        zos.putNextEntry(new ZipEntry(file.getName()));
        BufferedOutputStream bos = new BufferedOutputStream(zos);

        int b;
        try {
            while ((b = bis.read()) != -1) {
                bos.write(b);
            }
        } finally {
            bis.close();
            bos.close();
        }
    }

    public static boolean isGoodZipFile(File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().log(LogUtil.parseExceptionToString(e));
            return false;
        }
    }

    public static void unzip(File file) throws IOException {
        BufferedOutputStream dest;
        BufferedInputStream is;

        ZipFile zipfile = new ZipFile(file);
        Enumeration e = zipfile.entries();
        ZipEntry entry;
        while (e.hasMoreElements()) {
            entry = (ZipEntry) e.nextElement();
            is = new BufferedInputStream(zipfile.getInputStream(entry));

            int count;
            byte data[] = new byte[BUFFER];

            String toPath = file.getParentFile().getPath() + "/" + entry.getName();
            dest = new BufferedOutputStream(new FileOutputStream(toPath), BUFFER);

            while ((count = is.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
            is.close();
        }

        zipfile.close();
    }
}
