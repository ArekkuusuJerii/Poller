import javafx.application.Application
import javafx.stage.Stage

import net.cinnamon.helper.StageHelper

class Poller extends Application {
	override def start(stage: Stage): Unit = {
		StageHelper.openLogin(stage)
		stage.toFront()
	}
}

object Main {
	def main(args: Array[String]): Unit = Application.launch(classOf[Poller], args: _*)
}