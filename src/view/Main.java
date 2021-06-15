package view;

import controller.DataAccess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    DataAccess dataAccess;

    @Override
    public void init() throws Exception {
        boolean reaskLogin = true;

        do {

        try {
            DataAccess dataAccess = DataAccess.getInstance();
            reaskLogin = false;

            if (dataAccess != null) {
                // TODO Launch MainView

            }

        } catch (Exception e) {
            System.out.println("reask login");
            e.printStackTrace();
            reaskLogin = true;
        }

        } while (reaskLogin);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
       /* Parent root = FXMLLoader.load(getClass().getResource("loginDialog.fxml"));
        primaryStage.setTitle("E-Lift - Maintenance des ascenseurs");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();*/
    }

}
