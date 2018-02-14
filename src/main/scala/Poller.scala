import javafx.application.Application
import javafx.stage.Stage

import net.cinnamon.utils.StageLoader

class Poller extends Application {
	override def start(stage: Stage): Unit = {
		StageLoader.load(getClass, stage, "views/login.fxml").setTitle("Login")
		stage.setResizable(false)
		stage.centerOnScreen()
		stage.show()
	}
}

object Main {
	def main(args: Array[String]): Unit = Application.launch(classOf[Poller], args: _*)
}