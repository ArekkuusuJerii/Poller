package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.repository.PollImpl;

public class UseToken implements IController {

    @FXML TextField tf_token;
    private Window parent;

    @Override
    public void initialize() {
        tf_token.setOnAction((event -> handleOpenEvent(null)));
    }

    @FXML
    public void handleOpenEvent(MouseEvent event) {
        if (!tf_token.getText().isEmpty() && tf_token.getText().length() == 8) {
            if(PollImpl.getIsPollActive(tf_token.getText())) {
                if(PollImpl.getCanAnswerPoll(tf_token.getText())) {
                    StageHelper.openPoll(parent, tf_token.getText());
                    hideWindow();
                } else AlertHelper.showError("Ya no puedes contestar esta encuesta").showAndWait();
            } else AlertHelper.showError("Esta encuesta no se encuentra activa").showAndWait();
        } else AlertHelper.showError("Este token no es válido").showAndWait();
    }

    public void setParent(Window parent) {
        this.parent = parent;
    }

    @Override
    public void hideWindow() {
        tf_token.getScene().getWindow().hide();
    }
}
