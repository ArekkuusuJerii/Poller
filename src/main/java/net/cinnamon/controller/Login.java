package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.cinnamon.helper.StringHelper;
import net.cinnamon.helper.StyleHelper;
import net.cinnamon.repository.LoginImpl;
import net.cinnamon.utils.StageLoader;

public class Login {

    private static String userEmail;
    private static int userID;

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
        LoginImpl.login(tf_user.getText(), pf_password.getText()).ifPresent(key -> {
            tf_user.getScene().getWindow().hide();
            Login.userEmail = tf_user.getText();
            Login.userID = key;
            //Open Menu
            Stage stage = StageLoader.load(getClass(), "view/menu.fxml");
            stage.setTitle("Menu");
            stage.centerOnScreen();
            stage.show();
        });
    }

    @FXML
    public void register(MouseEvent event) {

    }

    public static int getUserID() {
        return userID;
    }

    public static String getUserEmail() {
        return userEmail;
    }
}
