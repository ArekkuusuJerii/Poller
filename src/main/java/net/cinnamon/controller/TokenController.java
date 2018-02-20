package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class TokenController {

    @FXML TextField tf_token;

    public void setToken(String token) {
        tf_token.setText(token);
    }
}
