package net.cinnamon.utils

import java.io.IOException
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

object StageLoader {

	def load(context: Class[_], fxml: String): Stage = load(context, new Stage, fxml)

	def load(context: Class[_], stage: Stage, fxml: String): Stage = {
		getURL(context, fxml) match {
			case Some(layout) =>
				try {
					val scene = new Scene(FXMLLoader.load(layout))
					getURL(context, "css/modern_dark.css") match {
						case Some(css) => scene.getStylesheets.add(css.toExternalForm)
						case None =>
					}
					stage.setScene(scene)
				} catch {
					case e: IOException =>
						e.printStackTrace()
				}
			case None =>
		}
		stage
	}

	private def getURL(context: Class[_], resource: String) = Option.apply(context.getClassLoader.getResource(resource))
}
