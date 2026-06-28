package superlink.udpbind.farme;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.InetAddress;

public final class jfxt extends Application {
    public InetAddress address;
    public int port;
    public jfxt(InetAddress address,int port){
        this.address=address;
        this.port = port;
    }
    @Override
    public void start(Stage primaryStage) {
        WebView view = new WebView();
        WebEngine engine = view.getEngine();
        BorderPane root = new BorderPane(view);
        TextField addressBar = new TextField("https://google.com");
        addressBar.setOnAction(event ->
                engine.load(addressBar.getText()));
        root.setTop(addressBar);
        // Update address bar with URL of the loaded web page.
        engine.locationProperty().addListener((observable, oldValue, newValue) ->
                addressBar.setText(newValue));
        primaryStage.setTitle("JavaFX WebView");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
        engine.load(addressBar.getText());
    }
}