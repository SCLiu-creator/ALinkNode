package superlink.udpbind.farme;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
        import javafx.scene.web.WebView;
        import javafx.scene.web.WebEngine;
        import javafx.stage.Stage;
import netscape.javascript.JSObject;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;

public class jfx2 extends Application {

    public InetAddress address;
    public int port;
    public jfx2(InetAddress address,int port){
        this.address=address;
        this.port = port;
    }

    @Override
    public void start(Stage primaryStage) {
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
            // 确保JavaScript执行未被禁用
            webView.getEngine().setJavaScriptEnabled(true);

            // 启用JavaScript
            engine.setJavaScriptEnabled(true);
            webView.getEngine().setUserStyleSheetLocation(null);
            // 加载页面后重写console.log
            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    // 注入JavaScript代码，重写console.log
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("javaBridge", new jfx.JavaBridge());
                }else {
                    System.out.println("unSUCCEEDED");
                }
            });


            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    String currentUrl = engine.getLocation();
                    System.out.println("Current URL: " + currentUrl);
                    // 注入JavaScript代码，重写console.log
                    engine.executeScript(
                            "console.oldLog = console.log; " +
                                    "console.log = function(message) { " +
                                    "    console.oldLog(message); " +
                                    "    window.javaBridge.log(message); " +
                                    "};"
                    );
                    // 页面加载完成后执行JavaScript
                    webView.getEngine().executeScript("console.log('Page loaded1!');");
                    webView.getEngine().executeScript("console.log('Page loaded2!');");
                }
            });


            engine.setCreatePopupHandler(config -> {
                WebView popup = new WebView();
                popup.getEngine().load("about:blank");
                return popup.getEngine();
            });
            // 监听JavaScript控制台输出
            engine.setOnAlert(event -> {
                System.out.println("JS Alert: " + event.getData());
            });

            engine.setConfirmHandler(message -> {
                System.out.println("JS Confirm: " + message);
                return true;
            });
            // 在页面加载完成后执行调试代码
            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    // 打印所有全局变量
                    engine.executeScript("console.log(window);");
                    // 手动触发断点
                    engine.executeScript("debugger;");
                }
            });


            System.out.println("Loading URL: http://"+address.getHostAddress()+":"+port);
            webView.getEngine().load("http://"+address.getHostAddress()+":"+port);
            jfxPanel.setScene(new Scene(webView));
        });

        frame.add(jfxPanel, BorderLayout.CENTER);
        frame.setSize(1200, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
    public static void main(String[] args) {
        launch(args);
    }
}