package com.example.myapplication2.client.scan;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import superlink.udpbind.usedata.User;

public class DexWebScan {
    // Copy from galaxy sdk ${com.alibaba.android.galaxy.utils.ClassUtils}
    private static final String EXTRACTED_NAME_EXT = ".classes";
    private static final String EXTRACTED_SUFFIX = ".zip";
    private static final String SECONDARY_FOLDER_NAME = "code_cache" + File.separator + "secondary-dexes";
    private static final String PREFS_FILE = "multidex.version";
    private static final String KEY_DEX_NUMBER = "dex.number";
    private static final int VM_WITH_MULTIDEX_VERSION_MAJOR = 2;
    private static final int VM_WITH_MULTIDEX_VERSION_MINOR = 1;
    private static final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

    private static SharedPreferences getMultiDexPreferences(Context context) {
        return context.getSharedPreferences(PREFS_FILE, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                ? Context.MODE_PRIVATE : Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    }

    public void loadDexFile(Context context) {
        copyAssetsToFiles(context);
        String filePath = context.getFilesDir().getPath() + "/testDex.jar";

        //动态加载的也可以是APK文件，没有任何区别，APK 文件中的class.dex文件会被DexClassLoader加载。
        //但是，APK中的Activity类，由于是使用反射，无法取得Context，与普通的类毫无区别，没有生命周期。
        //String filePath = context.getFilesDir().getPath() + "/Test.apk";

        DexClassLoader classLoader = new DexClassLoader(filePath, context.getFilesDir().getPath(), null, context.getClassLoader());
        try {
            //方法一：反射调用
            Class myClass = classLoader.loadClass("com.test.mytest.Test");
            Constructor myConstructor = myClass.getConstructor(Context.class);
            Method method = myClass.getMethod("test", null);
            String data = (String) method.invoke(myConstructor.newInstance(this), null);
            //method.setAccessible(true);访问private函数
            System.out.println(data);

            //方法二：类型强转，前提是知道Test.jar中Class所实现的接口类<span style="font-family: Arial, Helvetica, sans-serif;">ITest</span>
            Class myClass2 = classLoader.loadClass("com.test.mytest.Test");
            Constructor myConstructor2 = myClass2.getConstructor(Context.class);
            User obj = (User) myConstructor2.newInstance(this);
//            String data2 = obj.test();
//            System.out.println(data2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Copy the APK "assets/" to "/data/data/package-name/file/"
     */
    public static void copyAssetsToFiles(Context context) {
        String fileDir = context.getFilesDir().getPath() + "/";
        File workingDir = new File(fileDir);
        if (!workingDir.exists()) {
            workingDir.mkdirs();
        }

        File outFile_bin = new File(workingDir, "test.jar");
        if (!outFile_bin.exists()) {
            copyFile(context, "test.jar", outFile_bin);
            outFile_bin.setExecutable(true, false);
        }
    }

    /**
     * Copy assets file to the data folder
     */
    private static void copyFile(Context context, String sourceFileName, File targetFile) {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = context.getAssets().open(sourceFileName);
            out = new FileOutputStream(targetFile);
            byte[] temp = new byte[1024];
            int count = 0;
            while ((count = in.read(temp)) > 0) {
                out.write(temp, 0, count);
            }

            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

        /**
         * 通过指定包名，扫描包下面包含的所有的ClassName
         *
         * @param context     U know
         * @param packageName 包名
         * @return 所有class的集合
         */
        public static List<Class<?>> getFileNameByPackageName(Context context, final String packageName) throws PackageManager.NameNotFoundException, IOException {
            List<String> paths = getSourcePaths(context);
            final CountDownLatch parserCtl = new CountDownLatch(paths.size());
            List<Class<?>> classList=new ArrayList<>();
            for (final String path : paths) {
                    DexFile dexfile = null;

                    try {
                        if (path.endsWith(EXTRACTED_SUFFIX)) {
                            //NOT use new DexFile(path), because it will throw "permission error in /data/dalvik-cache"
                            dexfile = DexFile.loadDex(path, path + ".tmp", 0);
                        } else {
                            dexfile = new DexFile(path);
                        }

                        Enumeration<String> dexEntries = dexfile.entries();
                        while (dexEntries.hasMoreElements()) {
                            String className = dexEntries.nextElement();

                            if (className.contains("superlink.httpserver.servelt" )){
                                Class<?> clazz = Class.forName(className);
                                classList.add(clazz);
                            }
                            if (className.contains("client.web" )){
                                Class<?> clazz = Class.forName(className);
                                classList.add(clazz);
                            }
                            if (className.contains("client.infu" )){
                                Class<?> clazz = Class.forName(className);
                                classList.add(clazz);
                            }
                        }
                    } catch (Throwable ignore) {
                        Log.e("ARouter", "Scan map file in dex files made error.", ignore);
                    } finally {
                        if (null != dexfile) {
                            try {
                                dexfile.close();
                            } catch (Throwable ignore) {
                            }
                        }
                        parserCtl.countDown();
                    }

                }

            //Log.d(Consts.TAG, "Filter " + classNames.size() + " classes by packageName <" + packageName + ">");
            return classList;
        }

        /**
         * get all the dex path
         *
         * @param context the application context
         * @return all the dex path
         * @throws PackageManager.NameNotFoundException
         * @throws IOException
         */
        public static List<String> getSourcePaths(Context context) throws PackageManager.NameNotFoundException, IOException {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            File sourceApk = new File(applicationInfo.sourceDir);

            List<String> sourcePaths = new ArrayList<>();
            sourcePaths.add(applicationInfo.sourceDir); //add the default apk path

            //the prefix of extracted file, ie: test.classes
            String extractedFilePrefix = sourceApk.getName() + EXTRACTED_NAME_EXT;

//        如果VM已经支持了MultiDex，就不要去Secondary Folder加载 Classesx.zip了，那里已经么有了
//        通过是否存在sp中的multidex.version是不准确的，因为从低版本升级上来的用户，是包含这个sp配置的
//        if (!isVMMultidexCapable()) {
            //the total dex numbers
            int totalDexNumber = getMultiDexPreferences(context).getInt(KEY_DEX_NUMBER, 1);
            File dexDir = new File(applicationInfo.dataDir, SECONDARY_FOLDER_NAME);
            for (int secondaryNumber = 2; secondaryNumber <= totalDexNumber; secondaryNumber++) {
                //for each dex file, ie: test.classes2.zip, test.classes3.zip...
                String fileName = extractedFilePrefix + secondaryNumber + EXTRACTED_SUFFIX;
                File extractedFile = new File(dexDir, fileName);
                if (extractedFile.isFile()) {
                    sourcePaths.add(extractedFile.getAbsolutePath());
                    //we ignore the verify zip part
                } else {
                    throw new IOException("Missing extracted secondary dex file '" + extractedFile.getPath() + "'");
                }
            }
//        }

//        if (ARouter.debuggable()) { // Search instant run support only debuggable
//            sourcePaths.addAll(tryLoadInstantRunDexFile(applicationInfo));
//        }
            return sourcePaths;
        }

        /**
         * Get instant run dex path, used to catch the branch usingApkSplits=false.
         */
        private static List<String> tryLoadInstantRunDexFile(ApplicationInfo applicationInfo) {
            List<String> instantRunSourcePaths = new ArrayList<>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && null != applicationInfo.splitSourceDirs) {
                // add the split apk, normally for InstantRun, and newest version.
                instantRunSourcePaths.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
//            Log.d(Consts.TAG, "Found InstantRun support");
            } else {
                try {
                    // This man is reflection from Google instant run sdk, he will tell me where the dex files go.
                    Class pathsByInstantRun = Class.forName("com.android.tools.fd.runtime.Paths");
                    Method getDexFileDirectory = pathsByInstantRun.getMethod("getDexFileDirectory", String.class);
                    String instantRunDexPath = (String) getDexFileDirectory.invoke(null, applicationInfo.packageName);

                    File instantRunFilePath = new File(instantRunDexPath);
                    if (instantRunFilePath.exists() && instantRunFilePath.isDirectory()) {
                        File[] dexFile = instantRunFilePath.listFiles();
                        for (File file : dexFile) {
                            if (null != file && file.exists() && file.isFile() && file.getName().endsWith(".dex")) {
                                instantRunSourcePaths.add(file.getAbsolutePath());
                            }
                        }
//                    Log.d(Consts.TAG, "Found InstantRun support");
                    }

                } catch (Exception e) {
//                Log.e(Consts.TAG, "InstantRun support error, " + e.getMessage());
                }
            }

            return instantRunSourcePaths;
        }

        /**
         * Identifies if the current VM has a native support for multidex, meaning there is no need for
         * additional installation by this library.
         *
         * @return true if the VM handles multidex
         */
        private static boolean isVMMultidexCapable() {
            boolean isMultidexCapable = false;
            String vmName = null;

            try {
                if (isYunOS()) {    // YunOS需要特殊判断
                    vmName = "'YunOS'";
                    isMultidexCapable = Integer.valueOf(System.getProperty("ro.build.version.sdk")) >= 21;
                } else {    // 非YunOS原生Android
                    vmName = "'Android'";
                    String versionString = System.getProperty("java.vm.version");
                    if (versionString != null) {
                        Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString);
                        if (matcher.matches()) {
                            try {
                                int major = Integer.parseInt(matcher.group(1));
                                int minor = Integer.parseInt(matcher.group(2));
                                isMultidexCapable = (major > VM_WITH_MULTIDEX_VERSION_MAJOR)
                                        || ((major == VM_WITH_MULTIDEX_VERSION_MAJOR)
                                        && (minor >= VM_WITH_MULTIDEX_VERSION_MINOR));
                            } catch (NumberFormatException ignore) {
                                // let isMultidexCapable be false
                            }
                        }
                    }
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            //Log.i(Consts.TAG, "VM with name " + vmName + (isMultidexCapable ? " has multidex support" : " does not have multidex support"));
            return isMultidexCapable;
        }

        /**
         * 判断系统是否为YunOS系统
         */
        private static boolean isYunOS() {
            try {
                String version = System.getProperty("ro.yunos.version");
                String vmName = System.getProperty("java.vm.name");
                return (vmName != null && vmName.toLowerCase().contains("lemur"))
                        || (version != null && version.trim().length() > 0);
            } catch (Exception ignore) {
                return false;
            }
        }

}
