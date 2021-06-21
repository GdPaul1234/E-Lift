package main.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.controller.PersonneOverviewController;
import main.model.Ascensoriste;

public class AscensoristeOverview extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Start ascensoriste editing...");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("PersonneOverview.fxml"));
        loader.setController(new PersonneOverviewController<>(new Ascensoriste()));

        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

}
