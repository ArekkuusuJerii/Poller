package net.cinnamon.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import net.cinnamon.helper.AlertHelper;
import net.cinnamon.helper.StageHelper;
import net.cinnamon.helper.StringHelper;
import net.cinnamon.helper.StyleHelper;
import net.cinnamon.repository.RegisterImpl;

public class RegisterController implements IController {

    @FXML
    public TextField tf_first_name;
    @FXML
    public TextField tf_second_name;
    @FXML
    public TextField tf_email;
    @FXML
    public TextField pf_password_0;
    @FXML
    public TextField pf_password_1;

    @Override
    public void initialize() {
        tf_first_name.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                boolean isValid = StringHelper.checkLettersOnly(tf_first_name.getText());
                StyleHelper.apply(tf_first_name, StyleHelper.TextColor(), isValid ? "white" : "red");
            }
        });
        tf_second_name.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                boolean isValid = StringHelper.checkLettersOnly(tf_second_name.getText());
                StyleHelper.apply(tf_second_name, StyleHelper.TextColor(), isValid ? "white" : "red");
            }
        });
        tf_email.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                boolean isEmail = StringHelper.checkEmail(tf_email.getText());
                if(isEmail) isEmail = RegisterImpl.canCreateAccount(tf_email.getText());
                StyleHelper.apply(tf_email, StyleHelper.TextColor(), isEmail ? "white" : "red");
            }
        });
        tf_first_name.setOnAction((event) -> tf_second_name.requestFocus());
        tf_second_name.setOnAction((event) -> tf_email.requestFocus());
        tf_email.setOnAction((event) -> pf_password_0.requestFocus());
        pf_password_0.setOnAction((event) -> pf_password_1.requestFocus());
    }

    @FXML
    public void handleCancelEvent(MouseEvent event) {
        StageHelper.openLogin("");
        hideWindow();
    }

    @FXML
    public void handleAcceptEvent(MouseEvent event) {
        if(StringHelper.checkEmail(tf_email.getText())) {
            if (RegisterImpl.canCreateAccount(tf_email.getText())) {
                boolean nonEmpty = nonEmptyFields();
                boolean matchingPasswords = passWordsMatch();
                if (nonEmpty && matchingPasswords) {
                    AlertHelper.showConfirmation("Registrarse como: '" + tf_email.getText() + "' ?")
                            .ifPresent(button -> {
                                if (button == ButtonType.OK) {
                                    //Send to DB
                                    boolean registrado = RegisterImpl.register(
                                            tf_first_name.getText(),
                                            tf_second_name.getText(),
                                            tf_email.getText(),
                                            pf_password_0.getText()
                                    );
                                    if (registrado) {
                                        StageHelper.openLogin(tf_email.getText());
                                        hideWindow();
                                    }
                                }
                            });
                } else {
                    if (!nonEmpty) {
                        AlertHelper.showError("No pueden haber campos vacios")
                                .showAndWait();
                    }
                    if (!matchingPasswords) {
                        AlertHelper.showError("Las contraseñas no coinciden")
                                .showAndWait()
                                .ifPresent(button -> {
                                    pf_password_0.setText("");
                                    pf_password_1.setText("");
                                });
                    }
                }
            } else AlertHelper.showError("Este correo ya está registrado").showAndWait();
        } else AlertHelper.showError(tf_email.getText() + " no es un correo válido").showAndWait();
    }

    private boolean nonEmptyFields() {
        return !tf_first_name.getText().isEmpty() &&
                !tf_second_name.getText().isEmpty() &&
                !pf_password_0.getText().isEmpty() &&
                !pf_password_1.getText().isEmpty() &&
                !tf_email.getText().isEmpty();
    }

    private boolean passWordsMatch() {
        String pass0 = pf_password_0.getText();
        String pass1 = pf_password_1.getText();
        return pass0.equals(pass1);
    }

    @Override
    public void hideWindow() {
        tf_email.getScene().getWindow().hide();
    }
}
