package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.repository.PollImpl;

public class Menu implements IController {

    @FXML
    private Label lb_user;
    @FXML
    private TextField tf_token;
    private Stage createWindow;

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
        if (PollImpl.getIsPollActive(tf_token.getText())) {
            StageHelper.openPoll();
            hideWindow();
        } else AlertHelper.showError("Este token no es válido").showAndWait();
    }

    @FXML
    public void handleVisualizeEvent(MouseEvent event) {

    }

    @FXML
    public void handleCreateEvent(MouseEvent event) {

        /*FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"),
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                new ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile != null) {
            mainStage.display(selectedFile);
        }*/
    }

    @Override
    public void hideWindow() {
        lb_user.getScene().getWindow().hide();
        if(createWindow != null) createWindow.hide();
    }
}
