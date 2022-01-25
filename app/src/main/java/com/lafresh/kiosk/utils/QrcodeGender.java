package com.lafresh.kiosk.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrcodeGender {

    public static void generateQRCode_general(final String data, final ImageView img, final int width, final int height) {
        new AsyncTask<Void, Void, Void>() {
            Bitmap ImageBitmap;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    com.google.zxing.Writer writer = new QRCodeWriter();
                    String finaldata = data;

                    BitMatrix bm = writer.encode(finaldata, BarcodeFormat.QR_CODE, width, height);
                    ImageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (ImageBitmap != null) {
                    img.setImageBitmap(ImageBitmap);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void generateBarcode_general(final String data, final ImageView img, final int width, final int height) {
        new AsyncTask<Void, Void, Void>() {
            Bitmap ImageBitmap;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    com.google.zxing.Writer writer = new Code128Writer();
                    String finaldata = data;

                    BitMatrix bm = writer.encode(finaldata, BarcodeFormat.CODE_128, width, height);
                    Log.d("Barcode", bm.toString());
                    ImageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (ImageBitmap != null) {
                    img.setImageBitmap(ImageBitmap);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
