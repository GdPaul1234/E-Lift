package main.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.controller.DAO.DataAccess;

public class Main extends Application {
    private DataAccess dataAccess;
    boolean loginSucess = false;

    @Override
    public void init() {
        boolean reaskLogin = true;

        do {

            try {
                DataAccess dataAccess = DataAccess.getInstance();
                reaskLogin = false;

                if (dataAccess != null) {
                    loginSucess = true;
                }

            } catch (IllegalArgumentException e) {
                System.out.println("reask login");
                e.printStackTrace();
                reaskLogin = true;
            }

        } while (reaskLogin);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if(loginSucess) {
            Parent root = FXMLLoader.load(getClass().getResource("/main/view/fxml/MainPage.fxml"));
            primaryStage.setTitle("E-Lift - Maintenance des ascenseurs");
            primaryStage.setScene(new Scene(root, 850, 480));
            primaryStage.show();
        }

    }

}
