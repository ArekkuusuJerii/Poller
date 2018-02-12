package cinnamon.poll;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		Parent layout = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("login.fxml")));
		stage.setTitle("Login");
		Scene scene = new Scene(layout, 500, 800);
		scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/modern_dark.css")).toExternalForm());
		stage.setResizable(false);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
