package net.cinnamon.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import net.cinnamon.entity.Poll;
import net.cinnamon.entity.Question;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.repository.PollImpl;
import net.cinnamon.repository.StatisticImpl;

public class StatisticController implements IController {

    @FXML
    TextField tf_token;
    @FXML
    ScrollPane scroll_node;
    @FXML
    CheckBox cb_active;

    @Override
    public void initialize() {
        //NO - OP
    }

    @FXML
    public void handleOpenEvent(MouseEvent event) {
        if (!tf_token.getText().isEmpty() && tf_token.getText().length() == 8) {
            if (PollImpl.getIsPollOwner(tf_token.getText())) {
                open(PollImpl.readPoll(tf_token.getText()));
            } else AlertHelper.showError("Esta encuesta no te pertenece").showAndWait();
        } else AlertHelper.showError("Este token no es válido").showAndWait();
    }

    private void open(Poll poll) {
        this.tf_token.setText(poll.token);
        this.cb_active.setSelected(poll.active);
        VBox box = new VBox();
        poll.questions.forEach(question -> {
            box.getChildren().add(StageHelper.loadStatistic(poll.token, question));
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
        void load(String token, Question question);
    }

    public static class PieStatistic implements Statistic {

        @FXML
        PieChart pie_chart;

        @Override
        public void load(String token, Question question) {
            pie_chart.setTitle(question.text);
            ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
            StatisticImpl.getSelectionStatistics("FEB-MAR", token, question).forEach(t -> {
                data.add(new PieChart.Data(t._1, t._2));
            });
            pie_chart.setData(data);
        }
    }

    public static class InputStatistic implements Statistic {

        @Override
        public void load(String token, Question question) {

        }
    }
}
