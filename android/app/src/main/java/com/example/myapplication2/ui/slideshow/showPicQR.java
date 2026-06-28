package com.example.myapplication2.ui.slideshow;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.R;
import com.example.myapplication2.client.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.farme.ShowQr;

public class showPicQR extends ShowQr {

    public static boolean qr=false;

    public static View view;
    public static AppCompatActivity mainActivity;

    public byte[] show(String o2){
        mainActivity.runOnUiThread(()->{
            show();
        });
        return null;
    }


    public void show(){
//        Snackbar.make(view, "Replace with your own action     " + UDPclient.userlocal.address+" : " + UDPclient.userlocal.port, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
        try {
            if (qr) {
                ImageView imageView = mainActivity.findViewById(R.id.imageView2);
                imageView.setWillNotDraw(false);
                qr = false;
            } else {
                ImageView imageView = mainActivity.findViewById(R.id.imageView2);
                imageView.setWillNotDraw(true);
                String url = "http://" + UDPclient.userlocal.inaddress.toString() + ":" + UDPclient.userlocal.inport;
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 126, 126);
//                Bitmap barcodeEncoder=new BarcodeEncoder().encode(url, BarcodeFormat.QR_CODE, 126, 126);

                Bitmap bmp = Utils.bitMatrixToBitmap(bitMatrix);
                imageView.setImageBitmap(bmp);
                qr = true;
            }
        } catch (Exception  e) {
            throw new RuntimeException(e);
        }
    }
}
