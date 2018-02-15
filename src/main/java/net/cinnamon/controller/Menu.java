package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class Menu {

    @FXML
    private TextField tf_user;

    public void initialize() {
        tf_user.setText(String.valueOf(Login.getUserEmail()));
    }
}
