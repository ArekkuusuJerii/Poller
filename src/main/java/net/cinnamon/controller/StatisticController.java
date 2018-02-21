package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import net.cinnamon.entity.Poll;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.repository.PollImpl;

public class StatisticController implements IController {

    @FXML TextField tf_token;

    @Override
    public void initialize() {
        tf_token.setOnAction((event -> handleOpenEvent(null)));
    }

    @FXML
    public void handleOpenEvent(MouseEvent event) {
        if(!tf_token.getText().isEmpty() && tf_token.getText().length() == 8) {
            if(PollImpl.getIsPollOwner(tf_token.getText())) {
                open(PollImpl.readPoll(tf_token.getText()));
            } else AlertHelper.showError("Esta encuesta no te pertenece").showAndWait();
            hideWindow();
        } else AlertHelper.showError("Este token no es válido").showAndWait();
    }

    private void open(Poll poll) {

    }

    @Override
    public void hideWindow() {
        tf_token.getScene().getWindow().hide();
    }

    public static class Open {

    }
}
