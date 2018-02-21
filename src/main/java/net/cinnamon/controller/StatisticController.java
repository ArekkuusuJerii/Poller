package net.cinnamon.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.cinnamon.entity.Poll;
import net.cinnamon.entity.Question;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.repository.PollImpl;
import net.cinnamon.repository.StatisticImpl;
import scala.Tuple3;

public class StatisticController implements IController {

    @FXML TextField tf_token;
    @FXML ScrollPane scroll_node;
    @FXML ChoiceBox<String> choice_box;
    @FXML TextField tf_title;
    @FXML CheckBox cb_active;
    private Poll poll = null;

    @Override
    public void initialize() {
        tf_token.setOnAction(event -> setTerms());
        choice_box.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                open(PollImpl.readPoll(tf_token.getText()));
            } else clear();
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
        cb_active.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(poll != null) {
                if (!newValue) {
                    poll.active = newValue;
                    poll.overwrite();
                } else {
                    String term = choice_box.getSelectionModel().getSelectedItem();
                    AlertHelper.showTextInput("Introduce nuevo periodo", "Periodo").ifPresent(newTerm -> {
                        if (!term.equalsIgnoreCase(newTerm)) {
                            poll.term = newTerm;
                            poll.overwrite();
                        }
                    });
                }
            }
        });
    }

    private void setTerms() {
        if(!tf_token.getText().isEmpty()) {
            if (tf_token.getText().length() == 8) {
                if (PollImpl.getIsPollOwner(tf_token.getText())) {
                    //Add Terms
                    ObservableList<String> list = FXCollections.observableArrayList("");
                    list.addAll(StatisticImpl.getTerms(tf_token.getText()));
                    choice_box.setItems(list);
                    choice_box.setDisable(false);
                    cb_active.setDisable(false);
                    tf_title.setDisable(false);
                    //Get Tuple data
                    Tuple3<String, String, Boolean> tuple = StatisticImpl.getPollInfo(tf_token.getText());
                    Poll poll = new Poll();
                    poll.title = tuple._1();
                    poll.term = tuple._2();
                    poll.active = tuple._3();
                    poll.token = tf_token.getText();
                    tf_title.setText(poll.title);
                    cb_active.setSelected(poll.active);
                    this.poll = poll; //Keep here!
                } else {
                    AlertHelper.showError("Esta encuesta no te pertenece").showAndWait();
                    clear();
                }
            } else {
                AlertHelper.showError("Este token no es válido").showAndWait();
                clear();
            }
        } else clear();
    }

    private void clear() {
        choice_box.setItems(FXCollections.emptyObservableList());
        choice_box.setDisable(true);
        cb_active.setDisable(true);
        scroll_node.setContent(new VBox());
        tf_title.setText("");
        tf_title.setDisable(true);
        poll = null;
    }

    private void open(Poll poll) {
        String term = choice_box.getSelectionModel().getSelectedItem();
        this.tf_token.setText(poll.token);
        VBox box = new VBox();
        poll.questions.forEach(question -> {
            box.getChildren().add(StageHelper.loadStatistic(poll.token, term, question));
            box.getChildren().addAll(new Separator());
        });
        scroll_node.setContent(box);
        Platform.runLater(() -> {
            scroll_node.setVvalue(0);
        });
    }

    @Override
    public void hideWindow() {
        tf_token.getScene().getWindow().hide();
    }

    public interface Statistic {
        void load(String token, String term, Question question);
    }

    public static class PieStatistic implements Statistic {

        @FXML PieChart pie_chart;

        @Override
        public void load(String token, String term, Question question) {
            pie_chart.setTitle(question.text);
            ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
            StatisticImpl.getSelectionStatistics(term, token, question).forEach(t -> {
                PieChart.Data item = new PieChart.Data(t._1, t._2);
                data.add(item);
            });
            pie_chart.setData(data);
        }
    }

    public static class InputStatistic implements Statistic {

        @FXML Label lb_text;

        @Override
        public void load(String token, String term, Question question) {
            lb_text.setText(question.text);
        }
    }
}
