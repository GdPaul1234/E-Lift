package main.controller.DAO;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import main.controller.MainController;
import main.view.LoginDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Connexion à la database (Singleton)
 */
public class DataAccess {
    private static DataAccess instance;
    private Connection conn;
    private String login;

    private DataAccess(String url, String login, String password) throws SQLException {
        this.login = login;
        /* Get the Connection object. */
        conn = DriverManager.getConnection(url, login, password);
    }

    public String getLogin() {
        return login;
    }

    /**
     * Connexion à la BDD rentcar
     *
     * @return instance de DataAccess
     */
    public static synchronized DataAccess getInstance() throws IllegalArgumentException {

        if (instance == null || instance.getConnection() == null) {
            System.out.println("Ask login");

            AtomicBoolean error = new AtomicBoolean(false);

            final Runnable showDialog = () -> {
                LoginDialog dialog = new LoginDialog();
                Optional<Pair<String, String>> result = dialog.showAndWait();

                result.ifPresent(usernamePassword -> {
                    String login = usernamePassword.getKey();
                    String password = usernamePassword.getValue();
                    try {
                        instance = new DataAccess("jdbc:mysql://localhost:3306/e-lift", login, password);
                    } catch (SQLException e) {
                        MainController.showError(e);
                        error.set(true);
                    }
                });

                if (result.isEmpty()) {
                    instance = null;
                }
            };

            // Lancer le dialogue dans le thread JavaFX
            // https://stackoverflow.com/a/35960176
            try {
                if (Platform.isFxApplicationThread()) {
                    showDialog.run();
                } else {
                    FutureTask<Void> showDialogTask = new FutureTask<>(showDialog, null);
                    Platform.runLater(showDialogTask);
                    showDialogTask.get();
                }

            } catch (InterruptedException | ExecutionException e) {
                MainController.showError(e);
            }

            if(error.get()) {
                System.out.println("Error");
                throw  new IllegalArgumentException("Mot de passe incorrect");
            }
        }

        if(instance != null) System.out.println("Success !");


        return instance;
    }

    public static synchronized DataAccess getInstance(String login, String password)
            throws IllegalArgumentException, SQLException {
        if (instance != null && instance.getConnection() != null) {
            instance.getConnection().close();
        }

        instance = new DataAccess("jdbc:mysql://localhost:3306/e-lift", login, password);

        if (instance == null)
            throw new IllegalArgumentException();
        return instance;
    }

    /**
     * Obtenir la connexion à la BDD e-lift
     *
     * @return connexion à la BDD
     */
    public Connection getConnection() {
        return conn;
    }

    public void close() throws SQLException {
        if (conn != null)
            conn.close();
        else
            System.err.println("Unexpected command");
    }

}
