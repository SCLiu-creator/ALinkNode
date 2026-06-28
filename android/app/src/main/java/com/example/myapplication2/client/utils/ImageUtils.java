package com.example.myapplication2.client.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import superlink.filemanage.xmltool.XmlCreate;
import superlink.util.ImageTool.imageProider;
import superlink.util.SHAutils;
import superlink.util.Tool;

public class ImageUtils extends imageProider {

    public void generateFixedSizeImage(String filename, String newFileName){
        Bitmap bitmap = createThumbnail(filename,180); // 假设你已经加载了图片
        File file = new File(newFileName); // 选择保存位置和文件名，这里假设保存为PNG
        if (!file.exists()){
            try {
                file.createNewFile();
            }catch (Error | Exception e){
                e.printStackTrace();

            }
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            // 注意：这里假设我们想要保存为PNG格式
            // 对于JPEG，你可以使用 Bitmap.CompressFormat.JPEG 和适当的压缩质量
            String pre= Tool.getPrex(newFileName);
            switch (pre){
                case ".png":{
                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, fos);
                    break;
                }
                case ".jpg":
                case ".jpeg": {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateFixedSizeImage(String filename){
        Bitmap bitmap = createThumbnail(filename,180,180); // 假设你已经加载了图片
        File file = new File(XmlCreate.userCloudecache+ SHAutils.getSHA1(filename,false)); // 选择保存位置和文件名，这里假设保存为PNG
        if (!file.exists()){
            try {
                file.createNewFile();
            }catch (Error | Exception e){
                e.printStackTrace();

            }
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            // 注意：这里假设我们想要保存为PNG格式
            // 对于JPEG，你可以使用 Bitmap.CompressFormat.JPEG 和适当的压缩质量
            String pre= Tool.getPrex(file.getName());
            switch (pre){
                case ".png":{
                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, fos);
                    bitmap.recycle();
                    break;
                }
                case ".jpg":
                case ".jpeg": {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                    bitmap.recycle();
                    break;
                }
                case ".heic":
                    bitmap=generateThumbnail(filename,180);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                    bitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap generateThumbnail(String inputFilePath, int width) throws IOException {
        File heicFile = new File(inputFilePath);
        FileInputStream fis = new FileInputStream(heicFile);

        // 使用 HeifDecoder 读取 HEIC 文件
//        HeifDecoder decoder = new HeifDecoder(fis);
//        Bitmap bitmap = decoder.decodeBitmap();

//        // 计算缩放比例
//        float scaleFactor = (float) width / bitmap.getWidth();
//        int height = Math.round(bitmap.getHeight() * scaleFactor);
//
//        // 创建缩略图
//        Bitmap thumbnail = Bitmap.createScaledBitmap(bitmap, width, height, false);

        // 关闭输入流
//        fis.close();
//
//        return thumbnail;
        return null;
    }
    public Bitmap createThumbnail(String filePath, int targetWidth, int targetHeight) {
        // 使用BitmapFactory解码图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 先获取图片大小，再决定是否载入原图
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // 计算缩放比例
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > targetHeight || width > targetWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= targetHeight &&
                    (halfWidth / inSampleSize) >= targetWidth) {
                inSampleSize *= 2;
            }
        }

        // Decode bitmap with inSampleSize set
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        // 根据需要调整尺寸
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }

    public Bitmap createThumbnail(String filePath, int longLen) {
        // 使用BitmapFactory解码图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 先获取图片大小，再决定是否载入原图
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // 计算缩放比例
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        int h;
        int w;
        if (height>width){
            h=height/longLen;
            w=width/h;
            h=longLen;
        }else {
            w=width/longLen;
            h=height/w;
            w=longLen;
        }


        int halfHeight ;
        int halfWidth ;
        if (height > longLen || width > longLen) {
            halfHeight = height / 2;
            halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= longLen && (halfWidth / inSampleSize) >= longLen) {
                inSampleSize *= 2;
            }
        }
        // Decode bitmap with inSampleSize set
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        // 根据需要调整尺寸
        return Bitmap.createScaledBitmap(bitmap, w, h, true);
    }


    //视频获取关键帧
    private Bitmap createThumbnailAtTime(String filePath, int timeInSeconds){
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(filePath);
        //api time unit is microseconds
        return mMMR.getFrameAtTime(timeInSeconds*1000000, MediaMetadataRetriever.OPTION_CLOSEST);
    }
}