package main.controller.DAO;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.util.Pair;
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

    private DataAccess(String[] args) throws SQLException {
        String url = args[0];
        String login = args[1];
        String password = args[2];

        /* Get the Connection object. */
        conn = DriverManager.getConnection(url, login, password);
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
                    String[] args = {"jdbc:mysql://localhost:3306/e-lift", usernamePassword.getKey(), usernamePassword.getValue()};
                    try {
                        instance = new DataAccess(args);
                    } catch (SQLException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
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
                FutureTask<Void> showDialogTask = new FutureTask<>(showDialog, null);
                Platform.runLater(showDialogTask);
                showDialogTask.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
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

        String[] args = { "jdbc:mysql://localhost:3306/e-lift", login, password };
        instance = new DataAccess(args);

        if (instance == null)
            throw new IllegalArgumentException();
        return instance;
    }

    /**
     * Obtenir la connexion à la BDD rentcar
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
