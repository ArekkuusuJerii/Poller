package net.cinnamon.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import net.cinnamon.entity.Poll;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.repository.StatisticImpl;
import scala.Tuple3;

public class ViewToken implements IController {

    @FXML ChoiceBox<String> cb_tokens;
    @FXML TextField tf_token;
    @FXML Button btn_open;
    @FXML TextField tf_title;
    @FXML CheckBox cb_active;
    private Window parent;
    private Poll poll = null;

    @Override
    public void initialize() {
        cb_active.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(poll != null && poll.active != newValue) {
                if (!newValue) {
                    poll.active = newValue;
                    poll.overwrite();
                } else {
                    AlertHelper.showTextInput("Introduce periodo", "Periodo", poll.term).ifPresent(newTerm -> {
                        poll.term = newTerm;
                        poll.active = true;
                        poll.overwrite();
                    });
                    cb_active.setSelected(poll.active);
                }
            }
        });
        tf_title.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(poll != null && !newValue) {
                poll.title = tf_title.getText();
                poll.overwrite();
            }
        });
        tf_title.setOnAction(event -> {
            if(poll != null) {
                poll.title = tf_title.getText();
                poll.overwrite();
            }
        });
        cb_tokens.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && !newValue.isEmpty()) {
                btn_open.setDisable(false);
            } else {
                tf_title.setText("");
                tf_title.setDisable(true);
                cb_active.setSelected(false);
                cb_active.setDisable(true);
                btn_open.setDisable(false);
                poll = null;
            }
            tf_token.setText(newValue);
        });
        cb_tokens.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setToken(newValue);
        });
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll(StatisticImpl.getTokens());
        cb_tokens.setItems(list);
    }

    public void setToken(String token) {
        Tuple3<String, String, Boolean> tuple = StatisticImpl.getPollInfo(token);
        Poll poll = new Poll();
        poll.title = tuple._1();
        poll.term = tuple._2();
        poll.active = tuple._3();
        poll.token = token;
        tf_title.setText(poll.title);
        tf_title.setDisable(false);
        cb_active.setSelected(poll.active);
        cb_active.setDisable(false);
        this.poll = poll;
    }

    @FXML
    public void handleOpenEvent(MouseEvent event) {
        if(poll != null) {
            hideWindow();
            StageHelper.openStatistic(parent, poll.title, poll.token);
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
