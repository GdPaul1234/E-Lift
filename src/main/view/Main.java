package main.view;

import javafx.application.Platform;
import javafx.util.Pair;
import main.controller.DAO.AscensoristeDAO;
import main.controller.DAO.DataAccess;
import javafx.application.Application;
import javafx.stage.Stage;
import main.model.Ascensoriste;
import main.model.Personne;

import java.sql.SQLException;
import java.util.concurrent.FutureTask;

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

                // TODO Delete this
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {

                        boolean reaskAdd = false;
                        do {
                            Pair<Personne,String> userInput = new PersonneEditDialog(null).showPersonDialog();
                            if(userInput != null) {
                                try {
                                    if(userInput.getKey().isValid() && !userInput.getValue().isEmpty()) {
                                        new AscensoristeDAO().addAscensoriste(new Ascensoriste(userInput.getKey()), userInput.getValue());
                                        reaskAdd = false;
                                    } else {
                                        reaskAdd = true;
                                    }
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                            } else {
                                reaskAdd = false;
                            }
                        } while (reaskAdd);


                    }
                });

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
       /* Parent root = FXMLLoader.load(getClass().getResource("loginDialog.fxml"));
        primaryStage.setTitle("E-Lift - Maintenance des ascenseurs");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();*/
    }

}
