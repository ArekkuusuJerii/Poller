package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StringHelper;
import net.cinnamon.helper.StyleHelper;

import java.io.File;

public class Upload implements IController {

    @FXML
    private TextField tf_file;
    private File file;

    @Override
    public void initialize() {
        tf_file.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                boolean isValid = StringHelper.checkFilePath(tf_file.getText());
                StyleHelper.apply(tf_file, StyleHelper.TextColor(), isValid ? "white" : "red");
            }
        });
    }

    @FXML
    public void handleClickEvent(MouseEvent event) {
        if (event.getClickCount() == 2) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON", "*.json")
            );
            file = fileChooser.showOpenDialog(tf_file.getScene().getWindow());
            if(file != null) tf_file.setText(file.getPath());
        }
    }

    @FXML
    public void handleAcceptEvent(MouseEvent event) {
        if (file != null && file.exists() && file.isFile() && file.canRead()) {
            hideWindow();
        } else AlertHelper.showError("No se pudo abrir el archivo").showAndWait();
    }

    @Override
    public void hideWindow() {
        tf_file.getScene().getWindow().hide();
    }
}
