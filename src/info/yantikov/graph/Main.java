package info.yantikov.graph;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxml/sample.fxml"));
        primaryStage.setTitle("Нахождение кратчайшего пути");
        primaryStage.setScene(new Scene(root, 1000, 680));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(400);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> Platform.exit());
    }


    public static void main(String[] args) {
        launch(args);
    }
}
