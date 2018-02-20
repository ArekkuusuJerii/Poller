package net.cinnamon.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import net.cinnamon.entity.Poll;
import net.cinnamon.entity.Question;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.utils.LimitedTextArea;

import java.util.ArrayList;
import java.util.List;

public class PollController implements IController {

    @FXML
    private ScrollPane scroll_node;
    @FXML
    private Label lb_title;
    private List<PaneNode> nodes = new ArrayList<>();
    private Poll poll;

    @Override
    public void initialize() {
        //NO - OP
    }

    @FXML
    public void handleCancelEvent(MouseEvent event) {
        AlertHelper.showAlert("¿Desea salir sin haber contestado la encuesta?").showAndWait().ifPresent(button -> {
            if(button == ButtonType.OK) {
                hideWindow();
            }
        });
    }

    @FXML
    public void handleDoneEvent(MouseEvent event) {
        AlertHelper.showConfirmation("¿Desea concluir la encuesta?").ifPresent(button -> {
            if(button == ButtonType.OK) {
                hideWindow();
            }
        });
    }

    @Override
    public void hideWindow() {
        lb_title.getScene().getWindow().hide();
        StageHelper.openMenu("");
    }

    public void open(String token) {
        this.poll = Poll.read(token);
        this.lb_title.setText(poll.title);
        VBox box = new VBox();
        poll.questions.forEach(question ->
                box.getChildren().add(StageHelper.loadQuestion(nodes, question))
        );
        box.getChildren().add(StageHelper.loadDone(this));
        scroll_node.setContent(box);
        Platform.runLater(() -> {
            scroll_node.setVvalue(0);
        });
    }

    public interface PaneNode {
        void loadQuestion(Question question);
        void save(Poll poll);
    }

    public static class OpenQuestion implements PaneNode {

        @FXML
        private Label lb_question;
        @FXML
        private LimitedTextArea tf_answer;
        private Question question;

        @Override
        public void loadQuestion(Question question) {
            lb_question.setText(question.text);
            this.question = question;
        }

        @Override
        public void save(Poll poll) {

        }
    }

    public static class SingleQuestion implements PaneNode {

        @FXML
        private Label lb_question;
        private Question question;

        @Override
        public void loadQuestion(Question question) {
            lb_question.setText(question.text);
            this.question = question;
        }

        @Override
        public void save(Poll poll) {

        }
    }

    public static class MultipleQuestion implements PaneNode {

        @FXML
        private Label lb_question;
        @FXML
        private GridPane gp_node;
        private Question question;

        @Override
        public void loadQuestion(Question question) {
            lb_question.setText(question.text);
            this.question = question;
        }

        @Override
        public void save(Poll poll) {

        }
    }
}
