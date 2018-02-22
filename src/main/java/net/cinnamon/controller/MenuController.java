package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import net.cinnamon.helper.StageHelper;

public class MenuController implements IController {

    private static String email;
    private static int id;

    @FXML Label lb_user;

    @Override
    public void initialize() {
        //NO - OP
    }

    @FXML
    public void logout(MouseEvent event) {
        StageHelper.openLogin("");
        hideWindow();
    }

    @FXML
    public void handleOpenEvent(MouseEvent event) {
        StageHelper.openUseToken(lb_user.getScene().getWindow());
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
