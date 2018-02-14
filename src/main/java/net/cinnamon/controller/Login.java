package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.cinnamon.helper.StringHelper;
import net.cinnamon.helper.StyleHelper;

public class Login {

    @FXML
    private TextField tf_user;
    @FXML
    private PasswordField pf_password;

    public void initialize() {
        tf_user.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                boolean isEmail = StringHelper.checkEmail(tf_user.getText());
                StyleHelper.apply(tf_user, StyleHelper.TextColor(), isEmail ? "white" : "red");
            }
        });
        tf_user.setOnAction((event) ->
                pf_password.requestFocus()
        );
    }
}
