package com.example.myapplication2.client.scan;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class linuxscan extends AppCompatActivity {
    protected void san(Bundle savedInstanceState) {
        String apkPath= getExternalCacheDir().getAbsolutePath() + "/bundle.apk";
        loadApk(apkPath);
    }
    private void loadApk(String apkPath) {

        File optFile= getDir("opt", MODE_PRIVATE);//通过DexClassLoader加载制定的APK文件

        DexClassLoader dexClassLoader = new DexClassLoader(apkPath,
                optFile.getAbsolutePath(), null, getClassLoader());try{//通过反射去使用对象

            Class clz = dexClassLoader.loadClass("com.loubinfeng.www.boundle.Printer");if (clz != null) {

                Object instance=clz.newInstance();

                Method method= clz.getMethod("print");
                method.invoke(instance);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
