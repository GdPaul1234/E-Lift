package main.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ImmeubleOverview extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main/view/fxml/ImmeubleOverview.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}
