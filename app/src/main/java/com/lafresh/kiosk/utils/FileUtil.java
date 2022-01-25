package com.lafresh.kiosk.utils;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    private static final String TAG = "FileUtil";
    private String ncccKey = "";

    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); //如果資料夾不存在 則建立新資料夾
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子資料夾
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "複製整個資料夾內容操作出錯");
            e.printStackTrace();
        }
    }

    public static void deleteFile(File path) {
        if (!path.exists()) {
            return;
        }

        if (path.isFile()) {
            path.delete();
            return;
        }

        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteFile(files[i]);
        }

        path.delete();
    }

    public static void storeFile(InputStream inputStream, String filePath, String fileName,
                                 int fileSize, UpdateCallback updateCallback) {
        File file = new File(filePath, fileName);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            double pg = 0;

            if (fileSize > 0) {
                pg = ((5.0 * 1024 / fileSize) * 100);
            }

            byte[] buffer = new byte[5 * 1024];
            int len;
            double temp = 0.0;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);

                if (updateCallback != null) {
                    temp += pg;
                    if (temp > 1) {
                        temp = 0.0;
                        updateCallback.onUpdate();
                    }
                }
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        } catch (IOException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public static int checkDirSize(String filePath) {
        File dir = new File(filePath);
        if (!dir.exists()) {
            return -1;
        }

        if (dir.isFile()) {
            return -1;
        }

        if (dir.isDirectory()) {
            return dir.listFiles().length;
        }

        return -1;
    }

    public static File getOldestFileInDir(String dirPath) {
        if (dirPath == null || dirPath.length() == 0) {
            return null;
        }

        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File oldest = null;
        for (File file : files) {
            if (oldest == null) {
                oldest = file;
                continue;
            }

            long lastModifiedTime = file.lastModified();
            long oldestTime = oldest.lastModified();
            if (lastModifiedTime < oldestTime) {
                oldest = file;
            }
        }

        return oldest;
    }

    public static void rename(String filePath, String renamePath) {
        File file = new File(filePath);
        file.renameTo(new File(renamePath));
    }

    public interface UpdateCallback {
        void onUpdate();
    }

    /**
     * 新增logfile到sd card
     * */
    public static void appendLog(String fileName,String text) {
        File logFile = new File("sdcard/"+fileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+":"+text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
