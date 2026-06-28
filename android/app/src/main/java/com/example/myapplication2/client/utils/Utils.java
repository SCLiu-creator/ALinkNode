package com.example.myapplication2.client.utils;

import android.graphics.Bitmap;

import com.google.zxing.common.BitMatrix;

public class Utils {
    public static Bitmap bitMatrixToBitmap(BitMatrix bitMatrix) {
        //   设置容错率 L>M>Q>H  等级越高扫描时间越长,准确率越高
//        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
//        //设置字符集
//        map.put(EncodeHintType.CHARACTER_SET,"utf-8");
//        //设置外边距
//        map.put(EncodeHintType.MARGIN,1);

        final int width = bitMatrix.getWidth();
        final int height = bitMatrix.getHeight();

        final int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }
}
