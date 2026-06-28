package superlink.udpbind.farme;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;

public class jfx {
    public jfx(InetAddress inetAddress, int port) {
        System.setProperty("javafx.web.debug", "true");
        System.setProperty("javax.net.ssl.trustStore", "none");
        System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
        // 警告：这会极大降低安全性！
        System.setProperty("jdk.tls.disabledAlgorithms", ""); // 禁用TLS限制
        System.setProperty("jsse.enableSNIExtension", "false"); // 禁用SNI

        JFrame frame = new JFrame("JavaFX WebView in Swing");
        frame.setEnabled(true);
        frame.setVisible(true);
        JFXPanel jfxPanel = new JFXPanel();

        Platform.runLater(() -> {
            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();

            // 启用DevTools
            engine.executeScript("debugger;"); // 强制在DevTools中暂停
            engine.setJavaScriptEnabled(true);

            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    // 1. 注入桥接对象
                    JavaBridge bridge = new JavaBridge();
                    engine.executeScript("window.javaBridge = " + bridge + ";");

                    // 2. 安全的 console.log 重写
                    engine.executeScript(
                            "if (typeof console !== 'undefined') {" +
                                    "   console.oldLog = console.log;" +
                                    "   console.log = function() {" +
                                    "       var args = Array.prototype.slice.call(arguments);" +
                                    "       window.javaBridge.log(args.join(' '));" +
                                    "       console.oldLog.apply(console, args);" +
                                    "   };" +
                                    "}"
                    );

                    // 3. 诊断脚本加载状态
                    engine.executeScript(
                            "Array.from(document.scripts).forEach(script => {" +
                                    "   console.log('Script:', script.src, script.loaded);" +
                                    "});"
                    );
                } else if (newState == Worker.State.FAILED) {
                    Throwable ex = engine.getLoadWorker().getException();
                    ex.printStackTrace();
                }
            });

            engine.setCreatePopupHandler(config -> {
                WebView popup = new WebView();
                popup.getEngine().load("about:blank");
                return popup.getEngine();
            });

            engine.setOnAlert(event -> {
                System.out.println("JS Alert: " + event.getData());
            });

            engine.setConfirmHandler(message -> {
                System.out.println("JS Confirm: " + message);
                return true;
            });

            webView.getEngine().getLoadWorker().exceptionProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    newValue.printStackTrace();
                }
            });

            System.out.println("Loading URL: http://"+inetAddress.getHostAddress()+":"+port);
            webView.getEngine().load("http://"+inetAddress.getHostAddress()+":"+port);
            jfxPanel.setScene(new Scene(webView));
        });

        frame.add(jfxPanel, BorderLayout.CENTER);
        frame.setSize(1200, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Java桥接类，用于接收JavaScript的日志
    public static class JavaBridge {
        public void log(String message) {
            System.out.println("JS Log: " + message);
        }

        @Override
        public String toString() {
            // 返回一个JavaScript对象表示
            return "{" +
                    "log: function(message) { " +
                    "   java.lang.System.out.println('JS Log: ' + message); " +
                    "}" +
                    "}";
        }
    }
}

//package superlink.udpbind.farme;
//
//import javafx.application.Platform;
//import javafx.concurrent.Worker;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Scene;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebView;
//import netscape.javascript.JSObject;
//
//import javax.swing.*;
//import java.awt.*;
//import java.net.InetAddress;
//
//
//public class jfx {
//    public jfx(InetAddress inetAddress, int port) {
//        System.setProperty("javafx.web.debug", "true");
//        System.setProperty("javax.net.ssl.trustStore", "none");
//        System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
//        // 警告：这会极大降低安全性！
//        System.setProperty("jdk.tls.disabledAlgorithms", ""); // 禁用TLS限制
//        System.setProperty("jsse.enableSNIExtension", "false"); // 禁用SNI
//
//
//        JFrame frame = new JFrame("JavaFX WebView in Swing");
//        frame.setEnabled(true);
//        frame.setVisible(true);
//        JFXPanel jfxPanel = new JFXPanel();
//
//        Platform.runLater(() -> {
//            WebView webView = new WebView();
//            WebEngine engine = webView.getEngine();
//            // 启用DevTools
//            engine.executeScript("debugger;"); // 强制在DevTools中暂停
//            // 确保JavaScript执行未被禁用
//            webView.getEngine().setJavaScriptEnabled(true);
//
//            // 启用JavaScript
//            engine.setJavaScriptEnabled(true);
////            webView.getEngine().setUserStyleSheetLocation(null);
//
//            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
//                if (newState == Worker.State.SUCCEEDED) {
//                    // 1. 注入桥接对象
//                    JSObject window = (JSObject) engine.executeScript("window");
//                    window.setMember("javaBridge", new JavaBridge());
//
//                    // 2. 安全的 console.log 重写
//                    engine.executeScript(
//                            "if (typeof console !== 'undefined') {" +
//                                    "   console.oldLog = console.log;" +
//                                    "   console.log = function() {" +  // 不使用 ...args
//                                    "       var args = Array.prototype.slice.call(arguments);" + // 将 arguments 转为数组
//                                    "       window.javaBridge.log(args.join(' '));" +
//                                    "       console.oldLog.apply(console, args);" +
//                                    "   };" +
//                                    "}"
//                    );
//
//                    // 3. 诊断脚本加载状态
//                    engine.executeScript(
//                            "Array.from(document.scripts).forEach(script => {" +
//                                    "   console.log('Script:', script.src, script.loaded);" +
//                                    "});"
//                    );
//                } else if (newState == Worker.State.FAILED) {
//                    Throwable ex = engine.getLoadWorker().getException();
//                    ex.printStackTrace(); // 打印加载错误
//                }
//            });
////
////            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
////                if (newState == Worker.State.SUCCEEDED) {
////                    String currentUrl = engine.getLocation();
////                    System.out.println("Current URL: " + currentUrl);
////                    // 注入JavaScript代码，重写console.log
////                    engine.executeScript(
////                                    "console.oldLog = console.log; " +
////                                    "console.log = function(message) { " +
////                                    "    console.oldLog(message); " +
////                                    "    window.javaBridge.log(message); " +
////                                    "};"
////                    );
////                    // 页面加载完成后执行JavaScript
////                    webView.getEngine().executeScript("console.log('Page loaded1!');");
////                    webView.getEngine().executeScript("console.log('Page loaded2!');");
////                }
////            });
//
//
//            engine.setCreatePopupHandler(config -> {
//                WebView popup = new WebView();
//                popup.getEngine().load("about:blank");
//                return popup.getEngine();
//            });
//            // 监听JavaScript控制台输出
//            engine.setOnAlert(event -> {
//                System.out.println("JS Alert: " + event.getData());
//            });
//
//            engine.setConfirmHandler(message -> {
//                System.out.println("JS Confirm: " + message);
//                return true;
//            });
//            // 监控WebView中的JavaScript执行，防止内存泄漏
//            webView.getEngine().getLoadWorker().exceptionProperty().addListener((obs, oldValue, newValue) -> {
//                if (newValue != null) {
//                    newValue.printStackTrace();
//                    // 处理JavaScript异常，防止影响整个WebView的性能
//                }
//            });
////            // 在页面加载完成后执行调试代码
////            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
////                if (newState == Worker.State.SUCCEEDED) {
////                    // 打印所有全局变量
////                    engine.executeScript("console.log(window);");
////                    // 手动触发断点
////                    engine.executeScript("debugger;");
////                }
////            });
//
//
//            System.out.println("Loading URL: http://"+inetAddress.getHostAddress()+":"+port);
//            webView.getEngine().load("http://"+inetAddress.getHostAddress()+":"+port);
//            jfxPanel.setScene(new Scene(webView));
//        });
//
//        frame.add(jfxPanel, BorderLayout.CENTER);
//        frame.setSize(1200, 1000);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//
////        DevToolsDebuggerServer.startDebugServer(webView.getEngine().impl_getDebugger(), 51742);
//
//    }
//    // Java桥接类，用于接收JavaScript的日志
//    public static class JavaBridge {
//        public void log(String message) {
//            System.out.println("JS Log: " + message);
//        }
//    }
//}