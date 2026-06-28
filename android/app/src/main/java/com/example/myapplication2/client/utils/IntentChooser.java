package com.example.myapplication2.client.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.example.myapplication2.MainActivity;

public class IntentChooser extends MainActivity {

    // 常量，用于在onActivityResult中识别请求
    public static final int REQUEST_CODE_FILE_CHOOSER = 1001;

    // 启动文件选择器Intent并处理结果
    public static void openFileChooser(Activity activity, Intent intent) {
        // 启动Intent
        activity.startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER);
    }

    // 在你的Activity中处理onActivityResult
    // 你需要根据你的Activity或Fragment来调用这个逻辑
    // 注意：这不是IntentChooser类的一部分，而是你的Activity或Fragment中的代码
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentChooser.REQUEST_CODE_FILE_CHOOSER) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    // 用户取消了选择
                } else {
                    // 用户选择了一个或多个文件
                    Uri selectedUri = null;
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        Uri[] uris = new Uri[count];
                        for (int i = 0; i < count; i++) {
                            uris[i] = data.getClipData().getItemAt(i).getUri();
                        }
                        // 你可以处理多个Uri，这里只是取第一个作为示例
                        if (count > 0) {
                            selectedUri = uris[0];
                        }
                    } else {
                        selectedUri = data.getData();
                    }

                    // 处理selectedUri
                    // ...
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // 在你的WebChromeClient中
//    @Override
//    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
//                                     WebChromeClient.FileChooserParams fileChooserParams) {
//        // 创建Intent
//        Intent intent = fileChooserParams.createIntent();
//
//        // 启动文件选择器
//        IntentChooser.openFileChooser(YourActivity.this, intent);
//
//        // 存储回调以便在onActivityResult中使用
//        // ...
//
//        return true;
//    }
}