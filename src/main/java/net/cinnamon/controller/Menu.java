package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.repository.PollImpl;

public class Menu implements IController {

    @FXML
    private Label lb_user;
    @FXML
    private TextField tf_token;

    @Override
    public void initialize() {
        lb_user.setText(String.valueOf(Login.userEmail));
        tf_token.setOnAction((event -> handleOpenEvent(null)));
    }

    @FXML
    public void logout(MouseEvent event) {
        Login.userEmail = null;
        Login.userID = -1;
        StageHelper.openLogin();
        hideWindow();
    }

    @FXML
    public void handleOpenEvent(MouseEvent event) {
        if (!tf_token.getText().isEmpty() && PollImpl.getIsPollActive(tf_token.getText())) {
            StageHelper.openPoll();
            hideWindow();
        } else AlertHelper.showError("Este token no es válido").showAndWait();
    }

    @FXML
    public void handleVisualizeEvent(MouseEvent event) {
        StageHelper.openVisualize(lb_user.getScene().getWindow());
    }

    @FXML
    public void handleCreateEvent(MouseEvent event) {
         StageHelper.openFile(lb_user.getScene().getWindow());
    }

    @Override
    public void hideWindow() {
        lb_user.getScene().getWindow().hide();
    }
}
