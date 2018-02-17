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

import java.util.Optional;

public class Login implements IController {

    public static String userEmail;
    public static int userID = -1;

    @FXML
    private TextField tf_email;
    @FXML
    private PasswordField pf_password;

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
        if(userEmail != null) tf_email.setText(userEmail);
    }

    @FXML
    public void handleLoginEvent(MouseEvent event) {
        Optional<Integer> optional = LoginImpl.login(tf_email.getText(), pf_password.getText());
        if (optional.isPresent()) {
            Login.userEmail = tf_email.getText();
            Login.userID = optional.get();
            StageHelper.openMenu();
            hideWindow();
        } else AlertHelper.showError("Correo o Contraseña incorrecta").showAndWait();
    }

    @FXML
    public void register(MouseEvent event) {
        StageHelper.openRegister();
        hideWindow();
    }

    @Override
    public void hideWindow() {
        tf_email.getScene().getWindow().hide();
    }
}