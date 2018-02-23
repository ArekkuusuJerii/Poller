package net.cinnamon.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import net.cinnamon.entity.Poll;
import net.cinnamon.entity.Question;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.repository.PollImpl;
import net.cinnamon.repository.StatisticImpl;

public class StatisticController implements IController {

    @FXML ScrollPane scroll_node;
    @FXML ChoiceBox<String> choice_box;
    @FXML TextField tf_title;
    private String token;

    @Override
    public void initialize() {
        choice_box.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                open(PollImpl.readPoll(token));
            } else clear();
        });
    }

    public void setToken(String token) {
        if(!token.isEmpty()) {
            if (token.length() == 8) {
                if (PollImpl.getIsPollOwner(token)) {
                    //Add Terms
                    ObservableList<String> list = FXCollections.observableArrayList("");
                    list.addAll(StatisticImpl.getTerms(token));
                    choice_box.setItems(list);
                    choice_box.setDisable(false);
                    this.token = token;
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

    public void setTitle(String title) {
        tf_title.setText(title);
    }

    private void clear() {
        scroll_node.setContent(new VBox());
    }

    private void open(Poll poll) {
        String term = choice_box.getSelectionModel().getSelectedItem();
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

    @FXML
    public void handleCloseEvent(MouseEvent event) {
        hideWindow();
    }

    @Override
    public void hideWindow() {
        scroll_node.getScene().getWindow().hide();
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
        @FXML ScrollPane scroll_node;

        @Override
        public void load(String token, String term, Question question) {
            lb_text.setText(question.text);
            VBox box = new VBox();
            StatisticImpl.getInputStatistics(term, token, question).forEach(text -> {
                box.getChildren().addAll(StageHelper.loadInput(text));
            });
            scroll_node.setContent(box);
        }
    }
}
