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
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("login.fxml")));
		primaryStage.setTitle("Hello World");
		primaryStage.setScene(new Scene(root, 300, 275));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
