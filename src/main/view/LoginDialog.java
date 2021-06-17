package main.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Objects;


// Custom Login Dialog
// https://code.makery.ch/blog/javafx-dialogs-official/
// https://stackoverflow.com/a/64967696
public class LoginDialog extends Dialog<Pair<String, String>> {
    @FXML private TextField loginTextField;
    @FXML private PasswordField passwordTextField;

    public LoginDialog() {
        // Import login components
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("loginDialog.fxml"));
            loader.setController(this);

            DialogPane dialogPane = loader.load();

            initModality(Modality.APPLICATION_MODAL);
            setTitle("Connexion E-Lift");
            setDialogPane(dialogPane);

            // Convert the result to a username-password-pair when the login button is clicked.
            setResultConverter(buttonType  -> {
                if (Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                    return new Pair<>(loginTextField.getText(), passwordTextField.getText());
                }
                return null;
            });

            // Request focus on the username field by default.
            setOnShowing(dialogEvent -> Platform.runLater(() -> loginTextField.requestFocus()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
