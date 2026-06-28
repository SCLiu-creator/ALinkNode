package com.example.myapplication2;

import static com.example.myapplication2.client.utils.IntentChooser.REQUEST_CODE_FILE_CHOOSER;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication2.client.AndriodInitClass;
import com.example.myapplication2.client.utils.IntentChooser;
import com.example.myapplication2.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import dalvik.system.DexClassLoader;
import superlink.httpserver.HttpServlet;
import superlink.udpbind.client.UDPclient;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    WebView webView;

//    public ImageView imageView;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

    public static Context context;
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意，可以做你要做的事情了。
                } else {
                    requestPermission();
                    // 权限被用户拒绝了，可以提示用户,关闭界面等等。
//                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
                }
                return;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / (1024 * 1024));
        int cacheSize = maxMemory / 4; // 例如，设置缓存大小为总内存的1/4
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        if (BuildConfig.DEBUG) {
            // 在开发阶段，使用较低的日志级别
//            Log.d("MainActivity", "Debug log message");
//        } else {
            // 在生产阶段，使用较高的日志级别
//            Log.i("MainActivity", "Debug log message");
//        }
//        if (BuildConfig.DEBUG) {
            Log.println(Log.DEBUG, "MainActivity", "Setting log level to DEBUG");
//        }
//        Log.d("MainActivity","Debug Message");
//        Log.i("MainActivity","Info Message");
//        Log.w("MainActivity","Waring Message");
        Log.e("MainActivity","Error Message");
        Logger logger=LogManager.getLogManager().getLogger("global");
                logger.setLevel(Level.INFO);


//        Log.v("MainActivity","Verbose message");
        //requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_APN_SETTINGS}, REQUEST_READ_EXTERNAL_STORAGE);
//Manifest.permission.MANAGE_EXTERNAL_STORAGE

        // 获取当前窗口
        Window window = getWindow();

        // 启用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // 如果您的应用目标API级别是Android 5.0 (Lollipop) 或以上，可以使用以下代码
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        int REQUEST_NOTIFICATION_PERMISSION =1001;
        //单纯请求通知权限
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
//                        REQUEST_NOTIFICATION_PERMISSION);
//            }
//        }
        // 初始化 ActivityResultLauncher
        //请求通知权限
        ActivityResultLauncher<String> notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), // 权限请求契约
                isGranted -> { // 回调 lambda
                    if (isGranted) {
                        // 权限被授予
                        Toast.makeText(this, "通知权限已授予", Toast.LENGTH_SHORT).show();
                    } else {
                        // 权限被拒绝
                        Toast.makeText(this, "通知权限被拒绝", Toast.LENGTH_SHORT).show();
                    }
                });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.POST_NOTIFICATIONS)) {
                    // 解释为什么需要权限
                    new AlertDialog.Builder(this)
                            .setTitle("需要通知权限")
                            .setMessage("应用需要发送通知以提醒您重要事件。")
                            .setPositiveButton("确定", (dialog, which) -> {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                            })
                            .show();
                } else {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
            }
        }

        int permissinon=ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        checkStorageManagerPermission();
        String packageName = getPackageName(); // 获取当前应用的包名
        int permissionResult = getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, packageName);
        permissionResult = getPackageManager().checkPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE, packageName);
        permissionResult = getPackageManager().checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, packageName);
        permissionResult = getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, packageName);
        permissinon=0;

//        File file=new File("/storage/emulated/0/DCIM/Camera/IMG_20240629_174139.HEIC");
//        try {
//            byte[] bytes=Files.readAllBytes(file.toPath());
//            File file1=new File("/storage/emulated/0/DCIM/Camera/IMG_20240629_1741392.HEIC");
//            Files.write(file1.toPath(),bytes);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        if (permissinon != PackageManager.PERMISSION_GRANTED) { // 权限尚未被授予，请求权限
        boolean permissinonSt =false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) 及以上：使用 MANAGE_EXTERNAL_STORAGE
            if (Environment.isExternalStorageManager()) {
                permissinonSt=true;
//                Toast.makeText(this, "已获得访问所有文件权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            // API 26 (Oreo, 8.0) ~ API 29 (Q, 10.0)：使用 READ/WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "已获得存储权限", Toast.LENGTH_SHORT).show();
                permissinonSt=true;
            }
        }
        if (permissinonSt) {
            Toast.makeText(this, "已获得访问所有文件权限", Toast.LENGTH_SHORT).show();

        } else { // 权限尚未被授予，请求权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                Intent appIntent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                appIntent.setData(Uri.parse("package:" + getPackageName()));
                //appIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                try {
                    this.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    ex.printStackTrace();
                    Intent allFileIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    this.startActivity(allFileIntent);
                }
            }else {
                AlertDialog dialog = null;
                if (dialog != null) {
                    dialog.dismiss();
                }
                    dialog = new AlertDialog.Builder(this)
                        .setTitle("提示")//设置标题
                        .setMessage("请开启文件访问权限，否则无法正常使用应用！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);


                            }
                        }).create();
                    dialog.show();
            }

//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    1);
//        }
//            checkStorageManagerPermission();
//            setSupportActionBar(binding.appBarMain.toolbar);
//            MainActivity mainActivity=this;
//            FloatingActionButton fab = findViewById(R.id.fab);
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Snackbar.make(view, "Replace with your own action     " + UDPclient.userlocal.address+" : " + UDPclient.userlocal.port, Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                    try {
//                        if (qr){
//                            ImageView imageView=mainActivity.findViewById(R.id.imageView2);
//                            imageView.setWillNotDraw(false);
//                            qr=false;
//                        }else {
//                            ImageView imageView=mainActivity.findViewById(R.id.imageView2);
//                            imageView.setWillNotDraw(true);
//                            String url="http://"+UDPclient.userlocal.inaddress.toString()+":"+UDPclient.userlocal.inport;
//                            QRCodeWriter qrCodeWriter = new QRCodeWriter();
//                            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 126, 126);
////                        Bitmap barcodeEncoder=new BarcodeEncoder().encode(url, BarcodeFormat.QR_CODE, 126, 126);
//                            Bitmap bmp= Utils.bitMatrixToBitmap(bitMatrix);
//                            imageView.setImageBitmap(bmp);
//                            qr=true;
//                        }
//
//                    } catch (Exception  e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            });
//
//
//            fab = findViewById(R.id.fab);
//            fab.setOnTouchListener(new View.OnTouchListener() {
//
//                private boolean isClicking = false;
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            isClicking = true;
//                            break;
//                        case MotionEvent.ACTION_UP:
//                            if (isClicking) {
//                                // 执行点击事件的代码
//                                Toast.makeText(MainActivity.this, "FloatingActionButton was clicked!", Toast.LENGTH_SHORT).show();
//                                isClicking = false;
//                            }
//                            break;
//                        case MotionEvent.ACTION_CANCEL:
//                            isClicking = false;
//                            break;
//                    }
//                    return true; // 返回true以拦截事件
//                }
//            });

//            DrawerLayout drawer = binding.drawerLayout;
//            NavigationView navigationView = binding.navView;
//            // Passing each menu ID as a set of Ids because each
//            // menu should be considered as top level destinations.
//            mAppBarConfiguration = new AppBarConfiguration.Builder(
//                    R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.web_view)
//                    .setOpenableLayout(drawer)
//                    .build();
//            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//            NavigationUI.setupWithNavController(navigationView, navController);
        }
//        WebView webView = R.menu.activity_main_drawer;
//        webView.loadUrl("https://www.example.com");

        //引导用户将应用加入电池优化白名单
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);

        // 检查权限并启动服务
        if (Settings.canDrawOverlays(this)) {
            startService(new Intent(this, FloatingWindowService.class));
        } else {
            Toast.makeText(this, "请开启悬浮窗权限", Toast.LENGTH_SHORT).show();
        }

//        final int REQUEST_CODE_OVERLAY_PERMISSION = 1001;
//        if (!Settings.canDrawOverlays(this)) {
//            Intent intentd = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intentd, REQUEST_CODE_OVERLAY_PERMISSION);
//            startService(new Intent(this, FloatingWindowService.class));
//        } else {
//            // 已有权限，直接启动服务
//            // 启动悬浮窗服务（需在 Manifest 中声明）
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(new Intent(this, FloatingWindowService.class));
//            } else {
//                startService(new Intent(this, FloatingWindowService.class));
//            }
//        }

        if (AndriodInitClass.initClass==null){
            AndriodInitClass initClass=new AndriodInitClass();
            new Thread(()->{
                try {
                    initClass.initStart(this);
                } catch (IOException | PackageManager.NameNotFoundException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        String s = "/storage/emulated/0/superlink/web/webui/index.html";
        if (savedInstanceState != null) {
            Bundle webViewState = savedInstanceState.getBundle("webViewState");
            if (webViewState != null) {
                try {
                    webView.restoreState(webViewState);
                }catch (Exception e){
                    e.printStackTrace();
                    webviewshow();
                }

            }
        } else {
            // 加载初始URL
            webviewshow();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "横屏模式", Toast.LENGTH_SHORT).show();
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "竖屏模式", Toast.LENGTH_SHORT).show();
        }
    }


    static boolean viewloaded=false;
    static String viewurl="";
    @SuppressLint("SetJavaScriptEnabled")
    public void webviewshow(){
//        setContentView(R.layout.activity_main);
        //获得控件
        WebView webView = findViewById(R.id.webview);
        this.webView=webView;
//        int t=0;
//        while (UDPclient.userlocal.inaddress==null || UDPclient.userlocal.inport==0 || HttpServlet.port==null){
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            t++;
//            if (t>=10){
//                break;
//            }
//        }
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        // 创建 Handler 用于主线程操作
        Handler handler = new Handler(Looper.getMainLooper());

        // 定义检查任务
        Runnable checkTask = new Runnable() {
            private int attempts = 0;

            @Override
            public void run() {
                // 检查条件是否满足
                boolean isReady = (UDPclient.userlocal.inaddress != null &&
                        UDPclient.userlocal.inport != 0 &&
                        HttpServlet.port != null);

                if (isReady || attempts >= 20) {
                    if (isReady) {
                        // 条件满足，加载 WebView
                        loadWebView();
                    } else {
                        // 超时处理
                        Toast.makeText(MainActivity.this, "初始化超时，请重试", Toast.LENGTH_SHORT).show();
                        handler.postDelayed(this, 500);
                    }
                } else {
                    attempts++;
                    // 每隔 500ms 检查一次
                    handler.postDelayed(this, 500);
                }
            }
        };

        // 启动检查任务
        handler.post(checkTask);
    }

    // 单独封装 WebView 加载逻辑
    private void loadWebView() {
        WebSettings webSettings=webView.getSettings();
//        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setAllowContentAccess(true);
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setSavePassword(true);
//        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        MainActivity mainActivity=this;
//        //访问网页
        if (viewloaded){
            webView.loadUrl(viewurl);
            webView.evaluateJavascript("javascript: var state = JSON.parse(window.localStorage.getItem('pageState')); if (state) { restorePageState(state); }", null);

        }else {
            webView.loadUrl("http://127.0.0.1:"+UDPclient.userlocal.inport);
        }

        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) webView.getLayoutParams();
//        params.setMargins(0, 50, 0, 0);
//        webView.setLayoutParams(params); // 第一个参数是左，第二个参数是上，第三个参数是右，第四个参数是下
        webView.setWebViewClient(new WebViewClient(){
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebSettings webSettings=view.getSettings();
//        webSettings.setAppCacheEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.supportMultipleWindows();
                webSettings.setAllowContentAccess(true);
                webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
                webSettings.setUseWideViewPort(true);
                webSettings.setLoadWithOverviewMode(true);
//                webSettings.setSavePassword(true);
//                webSettings.setSaveFormData(true);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webSettings.setLoadsImagesAutomatically(true);
                webSettings.setJavaScriptEnabled(true);
                webSettings.setAllowFileAccess(true);
                //使用WebView加载显示url
                view.loadUrl(url);
                viewloaded=true;
                return true;
            }

            //            webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //页面开始加载
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //页面加载完毕
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //加载出现失败
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

//            });
            // 重写需要的方法，例如onShowFileChooser
        });
//        webView.setWebChromeClient(new WebChromeClient() {
//            public void onProgressChanged(WebView view, int progress) {
//                //加载过程回调，progress是接受到的数据的百分比
//            }
//        });
        // 查找并设置按钮的点击监听器
        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            // 使用reload()方法刷新WebView
            webView.reload();
            // 或者，如果你想要确保从服务器加载（忽略缓存），可以这样做：
            // webView.clearCache(true);
            // webView.loadUrl(webView.getUrl());
        });

        webView.setWebChromeClient(new WebChromeClient() {
            // 用于 API 级别 30 (Android 11) 及以上
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                System.out.println("onShowFileChooser onShowFileChooser onShowFileChooser");
                // 创建一个Intent来打开文件选择器
                Intent intent = fileChooserParams.createIntent();
                // 如果需要，你可以进一步配置intent，例如设置可接受的MIME类型
                // intent.setType("image/*"); // 例如，只选择图片
                // 启动Activity以获取文件
                IntentChooser.openFileChooser(mainActivity, intent);
                // 存储回调以便在onActivityResult中使用
                mainActivity.mFilePathCallback = filePathCallback;
                mainActivity.mFileChooserParams = fileChooserParams;
                // 返回true表示你处理了文件选择请求
                return true;
            }


        });
        // 定义一个 OnLayoutChangeListener
        View.OnLayoutChangeListener layoutChangeListener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    // 键盘弹出，调整WebView的布局参数
                    ViewGroup.LayoutParams params = webView.getLayoutParams();
                    params.height = oldBottom - bottom;
                    webView.setLayoutParams(params);
                }
            }
        };

// 将 OnLayoutChangeListener 添加到视图
        webView.addOnLayoutChangeListener(layoutChangeListener);
// 当不再需要监听布局变化时，可以移除监听器
        webView.removeOnLayoutChangeListener(layoutChangeListener);
        // IntentChooser是一个工具类，用于处理Intent的选择逻辑，你可能需要自己实现它
        // 或者直接使用已有的库，如FilePicker库
        ViewTreeObserver observer = webView.getViewTreeObserver();

// 添加 OnGlobalLayoutListener
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 当布局变化时，这个方法会被调用
                // 你可以在这里编写代码来处理布局变化

                // 一旦不再需要监听器，移除它
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    webView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    webView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private static final int REQUEST_MANAGE_FILES_ACCESS = 2;
    //申请所有文件访问权限
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //判断是否有管理外部存储的权限
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent=new Intent(Settings.Ma)
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_MANAGE_FILES_ACCESS);
            } else {
                // TODO: 2023/11/22
                // 已有所有文件访问权限，可直接执行文件相关操作
            }
        } else {
            // TODO: 2023/11/22
            //非android11及以上版本，走正常申请权限流程
        }
    }

    public void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    protected void san(Bundle savedInstanceState) {
        String apkPath= getExternalCacheDir().getAbsolutePath() + "/bundle.apk";
        loadApk(apkPath);
    }
    private void loadApk(String apkPath) {
        File optFile= getDir("opt", MODE_PRIVATE);//通过DexClassLoader加载制定的APK文件

        DexClassLoader dexClassLoader = new DexClassLoader(apkPath,
                optFile.getAbsolutePath(), null, getClassLoader());try{//通过反射去使用对象

            Class clz = dexClassLoader.loadClass("com.loubinfeng.www.boundle.Printer");
            if (clz != null) {
                Object instance=clz.newInstance();
                Method method= clz.getMethod("print");
                method.invoke(instance);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void checkStorageManagerPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
            Toast.makeText(this, "已获得访问所有文件权限", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("本程序需要您同意允许访问所有文件权限")
                    .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            startActivity(intent);
                        }
                    });
            builder.show();
            }
    }

    // 回调变量，用于存储从onShowFileChooser传递的回调和参数
    public ValueCallback<Uri[]> mFilePathCallback;
    public WebChromeClient.FileChooserParams mFileChooserParams;

    // 在你的Activity中处理ActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("onActivityResult onActivityResult onActivityResult");
        if (requestCode == REQUEST_CODE_FILE_CHOOSER) {
            if (mFilePathCallback == null) {
                return;
            }
            Uri[] results = null;

            // 检查用户是否选择了文件
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    // 用户取消了选择
                } else {
                    // 用户选择了一个或多个文件
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        results = new Uri[count];
                        for (int i = 0; i < count; i++) {
                            results[i] = data.getClipData().getItemAt(i).getUri();
                        }
                    } else {
                        results = new Uri[]{data.getData()};
                    }
                }
            }

            // 将选择的文件路径返回给WebView
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
            mFileChooserParams = null;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if(powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle webViewState = new Bundle();
        webView.saveState(webViewState);
        outState.putBundle("webViewState", webViewState);
        viewurl = webView.getUrl();
        webView.evaluateJavascript("javascript: window.localStorage.setItem('pageState', JSON.stringify(getPageState()));", null);
        WebBackForwardList savedList = webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void requestIgnoreBatteryOptimizations() {
//        try{
//            Intent intent = newIntent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//            intent.setData(Uri.parse("package:"+ getPackageName()));
//            startActivity(intent);
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }

}