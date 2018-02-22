package net.cinnamon.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Window;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.repository.StatisticImpl;

public class ViewToken implements IController {

    @FXML ChoiceBox<String> cb_tokens;
    @FXML Label lb_error;
    private Window parent;

    @Override
    public void initialize() {
        cb_tokens.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && !newValue.isEmpty()) {
                hideWindow();
                StageHelper.openStatistic(parent, newValue);
            }
        });
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll(StatisticImpl.getTokens());
        cb_tokens.setItems(list);
        if(list.isEmpty()) lb_error.setText("No has creado ninguna encuesta");
        else lb_error.setText("");
    }

    public void setParent(Window parent) {
        this.parent = parent;
    }

    @Override
    public void hideWindow() {
        cb_tokens.getScene().getWindow().hide();
    }
}
