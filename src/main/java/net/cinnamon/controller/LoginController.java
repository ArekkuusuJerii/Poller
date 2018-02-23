package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.helper.StringHelper;
import net.cinnamon.helper.StyleHelper;
import net.cinnamon.repository.LoginImpl;

import java.util.Locale;
import java.util.Optional;

public class LoginController implements IController {

    @FXML TextField tf_email;
    @FXML PasswordField pf_password;

    public static void login(String email, String password, Runnable success) {
        Optional<Integer> optional = LoginImpl.login(email, password);
        if (optional.isPresent()) {
            MenuController.setId(optional.get());
            StageHelper.openMenu(email);
            success.run();
        } else {
            AlertHelper.showError("Correo o Contraseña incorrecta").showAndWait();
        }
    }

    @Override
    public void initialize() {
        tf_email.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                boolean isEmail = StringHelper.checkEmail(tf_email.getText());
                StyleHelper.apply(tf_email, StyleHelper.TextColor(), isEmail ? "white" : "red");
            }
        });
        tf_email.setOnAction((event) -> pf_password.requestFocus());
        pf_password.setOnAction(event -> handleLoginEvent(null));
    }

    @FXML
    public void handleLoginEvent(MouseEvent event) {
        LoginController.login(tf_email.getText().toLowerCase(Locale.ROOT), pf_password.getText(), () -> {
            tf_email.setText("");
            pf_password.setText("");
            hideWindow();
        });
    }

    @FXML
    public void register(MouseEvent event) {
        StageHelper.openRegister();
        hideWindow();
    }

    public void setEmail(String email) {
        tf_email.setText(email);
    }

    @Override
    public void hideWindow() {
        tf_email.getScene().getWindow().hide();
    }
}
