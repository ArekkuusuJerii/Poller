package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import net.cinnamon.helper.StringHelper;
import net.cinnamon.helper.StyleHelper;
import net.cinnamon.repository.LoginImpl;

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
        pf_password.setOnAction(event -> login(null));
    }

    @FXML
    public void login(MouseEvent event) {
        if(LoginImpl.canLogin(tf_user.getText(), pf_password.getText())) {
            System.out.println("my ass");
        }
    }

    @FXML
    public void register(MouseEvent event) {

    }
}
