package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import net.cinnamon.helper.StageHelper;

public class Menu implements IController {

    @FXML
    private Label lb_user;

    @Override
    public void initialize() {
        lb_user.setText(String.valueOf(Login.userEmail));
    }

    @FXML
    public void logout(MouseEvent event) {
        Login.userEmail = null;
        Login.userID = -1;
        StageHelper.openLogin();
        hideWindow();
    }

    @Override
    public void hideWindow() {
        lb_user.getScene().getWindow().hide();
    }
}
