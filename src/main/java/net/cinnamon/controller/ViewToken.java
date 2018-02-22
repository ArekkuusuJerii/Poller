package net.cinnamon.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.repository.StatisticImpl;

public class ViewToken implements IController {

    @FXML ChoiceBox<String> cb_tokens;
    @FXML TextField tf_token;
    @FXML Button btn_open;
    private Window parent;

    @Override
    public void initialize() {
        cb_tokens.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && !newValue.isEmpty()) {
                btn_open.setDisable(false);
            }
            tf_token.setText(newValue);
        });
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll(StatisticImpl.getTokens());
        cb_tokens.setItems(list);
    }

    @FXML
    public void handleOpenEvent(MouseEvent event) {
        if(!tf_token.getText().isEmpty()) {
            hideWindow();
            StageHelper.openStatistic(parent, tf_token.getText());
        }
    }

    public void setParent(Window parent) {
        this.parent = parent;
    }

    @Override
    public void hideWindow() {
        cb_tokens.getScene().getWindow().hide();
    }
}
