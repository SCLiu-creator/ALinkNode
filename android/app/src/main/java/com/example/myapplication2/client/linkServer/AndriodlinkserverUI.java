package com.example.myapplication2.client.linkServer;

import android.content.Context;

import com.example.myapplication2.MainActivity;
import com.example.myapplication2.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.linkServer.Links;
import superlink.linkServer.Mod;

@Links(name = "Linkserver")
@ReInfuse(name = "getUI",grade = "c")
public class AndriodlinkserverUI {

    @Mod(def = "getUiByte")
    public byte[] rci() throws IOException {
        Context context = MainActivity.context;
        InputStream inputStream=context.getResources().openRawResource(R.raw.webui);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;

        // 循环读取直到流结束
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // 3. 转换为 byte 数组
        byte[] data = byteBuffer.toByteArray();
        return data;
    }

}
