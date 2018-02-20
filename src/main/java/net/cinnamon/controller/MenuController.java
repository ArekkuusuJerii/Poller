package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.repository.PollImpl;

public class MenuController implements IController {

    private static String email;
    private static int id;

    @FXML
    private Label lb_user;
    @FXML
    private TextField tf_token;

    @Override
    public void initialize() {
        tf_token.setOnAction((event -> handleOpenEvent(null)));
    }

    @FXML
    public void logout(MouseEvent event) {
        StageHelper.openLogin("");
        hideWindow();
    }

    @FXML
    public void handleOpenEvent(MouseEvent event) {
        if (!tf_token.getText().isEmpty() && PollImpl.getIsPollActive(tf_token.getText())) {
            StageHelper.openPoll(tf_token.getText());
            hideWindow();
        } else AlertHelper.showError("Este token no es válido").showAndWait();
    }

    @FXML
    public void handleVisualizeEvent(MouseEvent event) {
        StageHelper.openStatistic(lb_user.getScene().getWindow());
    }

    @FXML
    public void handleCreateEvent(MouseEvent event) {
         StageHelper.openUpload(lb_user.getScene().getWindow());
    }

    public void setEmail(String email) {
        lb_user.setText(email);
        MenuController.email = email;
    }

    public static String getEmail() {
        return email;
    }

    protected static void setId(int id) {
        MenuController.id = id;
    }

    public static int getId() {
        return MenuController.id;
    }

    @Override
    public void hideWindow() {
        lb_user.getScene().getWindow().hide();
    }
}
