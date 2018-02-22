package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import net.cinnamon.entity.Answer;
import net.cinnamon.entity.Poll;
import net.cinnamon.entity.Question;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.helper.StyleHelper;
import net.cinnamon.repository.PollImpl;
import net.cinnamon.utils.LimitedTextArea;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") //Shut up
public class PollController implements IController {

    @FXML Pane pane_node;
    @FXML ImageView btn_done;
    @FXML ImageView btn_prev;
    @FXML ImageView btn_next;
    @FXML Label lb_user;
    @FXML Label lb_title;
    @FXML Label lb_term;
    private List<Tuple2<PaneNode, Parent>> nodes = new ArrayList<>();
    private Poll poll;
    private int index;

    @Override
    public void initialize() {
        lb_user.setText(MenuController.getEmail());
    }

    @FXML
    public void handleNextEvent(MouseEvent event) {
        if(index + 1 < nodes.size()) {
            move(index + 1);
        }
    }

    @FXML
    public void handlePreviousEvent(MouseEvent event) {
        if(index > 0) {
            move(index - 1);
        }
    }

    private void move(int position) {
        pane_node.getChildren().setAll(nodes.get(position)._2);
        index = position;
        btn_next.setDisable(position + 1 >= nodes.size());
        btn_done.setDisable(position + 1 != nodes.size());
        btn_prev.setDisable(position - 1 < 0);
    }

    @FXML
    public void handleDoneEvent(MouseEvent event) {
        if (nodes.stream().map(t -> t._1).peek(PaneNode::markDirty).allMatch(PaneNode::hasAnswer)) {
            AlertHelper.showConfirmation("¿Desea concluir la encuesta?").ifPresent(button -> {
                if (button == ButtonType.OK) {
                    savePollAnswer();
                    hideWindow();
                }
            });
        } else {
            AlertHelper.showAlert("Faltan preguntas por responder").showAndWait();
        }
    }

    @FXML
    public void handleCancelEvent(MouseEvent event) {
        AlertHelper.showAlert("¿Desea salir sin haber contestado la encuesta?").showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                hideWindow();
            }
        });
    }

    private void savePollAnswer() {
        int application = PollImpl.savePoll(poll);
        if(application != -1) {
            nodes.stream().map(t -> t._1).forEach(n -> n.save(poll.token, application));
            AlertHelper.showInformation("Ha contestado la encuesta satisfactoriamente").showAndWait();
        } else {
            AlertHelper.showError("No es posible contestar la encuesta").showAndWait();
        }
    }

    @Override
    public void hideWindow() {
        lb_title.getScene().getWindow().hide();
        StageHelper.openMenu("");
    }

    public void open(String token) {
        this.poll = Poll.read(token);
        this.lb_title.setText(poll.title);
        this.lb_term.setText(poll.term);
        VBox box = new VBox();
        poll.questions.forEach(question ->
                StageHelper.loadQuestion(nodes, question)
        );
        if(!nodes.isEmpty()) {
            move(0);
        }
    }

    public interface PaneNode {
        void loadQuestion(Question question);
        boolean hasAnswer();
        void markDirty();
        void save(String token, int application);
    }

    public static class OpenQuestion implements PaneNode {

        @FXML Label lb_question;
        @FXML LimitedTextArea tf_answer;
        private Question question;

        public void initialize() {
            tf_answer.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    markDirty();
                }
            });
            tf_answer.textProperty().addListener((observable, oldValue, newValue) -> {
                markDirty();
            });
        }

        @Override
        public void loadQuestion(Question question) {
            lb_question.setText(question.text);
            this.question = question;
        }

        @Override
        public boolean hasAnswer() {
            return !tf_answer.getText().isEmpty();
        }

        @Override
        public void markDirty() {
            boolean answered = hasAnswer();
            StyleHelper.apply(tf_answer, StyleHelper.BorderColor(), answered ? "green" : "red");
        }

        @Override
        public void save(String token, int application) {
            PollImpl.saveAnswerInput(token, application, question, tf_answer.getText());
        }
    }

    public static class SingleQuestion implements PaneNode {

        @FXML GridPane gp_answers;
        @FXML ToggleGroup tg_answer;
        @FXML Label lb_question;
        @FXML RadioButton rb_0;
        @FXML RadioButton rb_1;
        @FXML RadioButton rb_2;
        @FXML RadioButton rb_3;
        @FXML RadioButton rb_4;
        @FXML Label lb_0;
        @FXML Label lb_1;
        @FXML Label lb_2;
        @FXML Label lb_3;
        @FXML Label lb_4;
        private List<Pair<RadioButton, Label>> list = new ArrayList<>();
        private Question question;

        public void initialize() {
            tg_answer.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                markDirty();
            });
            list.add(new Pair<>(rb_0, lb_0));
            list.add(new Pair<>(rb_1, lb_1));
            list.add(new Pair<>(rb_2, lb_2));
            list.add(new Pair<>(rb_3, lb_3));
            list.add(new Pair<>(rb_4, lb_4));
        }

        @Override
        public void loadQuestion(Question question) {
            lb_question.setText(question.text);
            this.question = question;
            //Set answers
            List<Answer> answers = question.answers;
            List<Toggle> toggles = tg_answer.getToggles();
            for (int i = 0; i < 5; i++) {
                Pair<RadioButton, Label> pair = list.get(i);
                RadioButton button = pair.getKey();
                Label label = pair.getValue();
                if(i < answers.size()) {
                    Answer answer = answers.get(i);
                    toggles.get(i).setUserData(answer);
                    label.setText(answer.text);
                } else {
                    gp_answers.getChildren().get(i).setVisible(false);
                    button.setDisable(true);
                    button.setVisible(false);
                    label.setDisable(true);
                    label.setVisible(false);
                }
            }
        }

        @Override
        public boolean hasAnswer() {
            return tg_answer.getSelectedToggle() != null && tg_answer.getSelectedToggle().isSelected();
        }

        @Override
        public void markDirty() {
            boolean selected = hasAnswer();
            StyleHelper.apply(gp_answers, StyleHelper.BorderColor(), selected ? "green" : "red");
        }

        @Override
        public void save(String token, int application) {
            PollImpl.saveAnswerSelection(token, application, question, (Answer) tg_answer.getSelectedToggle().getUserData());
        }
    }

    public static class MultipleQuestion implements PaneNode {

        @FXML GridPane gp_answers;
        @FXML Label lb_question;
        @FXML CheckBox cb_0;
        @FXML CheckBox cb_1;
        @FXML CheckBox cb_2;
        @FXML CheckBox cb_3;
        @FXML CheckBox cb_4;
        @FXML Label lb_0;
        @FXML Label lb_1;
        @FXML Label lb_2;
        @FXML Label lb_3;
        @FXML Label lb_4;
        private List<Pair<CheckBox, Label>> list = new ArrayList<>();
        private Question question;

        public void initialize() {
            list.add(new Pair<>(cb_0, lb_0));
            list.add(new Pair<>(cb_1, lb_1));
            list.add(new Pair<>(cb_2, lb_2));
            list.add(new Pair<>(cb_3, lb_3));
            list.add(new Pair<>(cb_4, lb_4));
            list.stream().map(Pair::getKey).forEach(box -> {
                box.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    markDirty();
                });
            });
        }

        @Override
        public void loadQuestion(Question question) {
            lb_question.setText(question.text);
            this.question = question;
            //Set answers
            List<Answer> answers = question.answers;
            for (int i = 0; i < 5; i++) {
                Pair<CheckBox, Label> pair = list.get(i);
                CheckBox button = pair.getKey();
                Label label = pair.getValue();
                if(i < answers.size()) {
                    Answer answer = answers.get(i);
                    label.setText(answer.text);
                    button.setUserData(answer);
                } else {
                    gp_answers.getChildren().get(i).setVisible(false);
                    button.setDisable(true);
                    button.setVisible(false);
                    label.setDisable(true);
                    label.setVisible(false);
                }
            }
        }

        @Override
        public boolean hasAnswer() {
            return list.stream().map(Pair::getKey).anyMatch(CheckBox::isSelected);
        }

        @Override
        public void markDirty() {
            boolean selected = hasAnswer();
            StyleHelper.apply(gp_answers, StyleHelper.BorderColor(), selected ? "green" : "red");
        }

        @Override
        public void save(String token, int application) {
            list.stream().map(Pair::getKey).filter(CheckBox::isSelected)
                    .map(b -> (Answer) b.getUserData())
                    .forEach(answer -> PollImpl.saveAnswerSelection(token, application, question, answer));
        }
    }
}
