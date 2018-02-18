package net.cinnamon

import java.io.IOException
import java.util.Optional
import javafx.fxml.FXMLLoader
import javafx.scene.control.{Alert, ButtonType}
import javafx.scene.image.Image
import javafx.scene.{Node, Parent, Scene}
import javafx.stage.Stage

package helper {

  import javafx.stage.{Modality, Window}

  object AlertHelper {
    def showError(text: String): Alert = {
      val alert: Alert = new Alert(Alert.AlertType.ERROR, text)
      StyleHelper.apply(alert.getDialogPane, "css/modern_dark.css")
      alert
    }

    def showAlert(text: String): Alert = {
      val alert: Alert = new Alert(Alert.AlertType.WARNING, text)
      StyleHelper.apply(alert.getDialogPane, "css/modern_dark.css")
      alert
    }

    def showInformation(text: String): Alert = {
      val alert: Alert = new Alert(Alert.AlertType.INFORMATION, text)
      StyleHelper.apply(alert.getDialogPane, "css/modern_dark.css")
      alert
    }

    def showConfirmation(text: String): Optional[ButtonType] = {
      val alert: Alert = new Alert(Alert.AlertType.CONFIRMATION, text)
      StyleHelper.apply(alert.getDialogPane, "css/modern_dark.css")
      alert.showAndWait()
    }
  }

  object StyleHelper {
    type Style = String
    val TextColor: Style = "-fx-text-fill"

    def apply(node: Node, style: Style, value: String): Unit = {
      node.setStyle(style + ":" + value + ";")
    }

    def apply(node: Parent, style: Style): Unit = {
      StageLoader.getURL(getClass, style) match {
        case Some(css) => node.getStylesheets.add(css.toExternalForm)
        case None =>
      }
    }
  }

  object StageHelper {
    def openLogin(): Unit = openLogin(new Stage())

    def openLogin(stage: Stage): Unit = {
      StageLoader.load(getClass, stage, "view/login.fxml")
      stage.setTitle("Login")
      stage.centerOnScreen()
      stage.show()
    }

    def openMenu(): Unit = {
      val stage = StageLoader.load(getClass, "view/menu.fxml")
      stage.setTitle("Menu")
      stage.centerOnScreen()
      stage.show()
    }

    def openRegister(): Unit = {
      val stage = StageLoader.load(getClass, "view/registro.fxml")
      stage.setTitle("Register")
      stage.centerOnScreen()
      stage.show()
    }

    def openPoll(): Unit = {
      val stage = StageLoader.load(getClass, "view/encuesta.fxml")
      stage.setTitle("Encuesta")
      stage.centerOnScreen()
      stage.show()
    }

    def openFile(window: Window): Unit = {
      val stage = StageLoader.load(getClass, "view/upload.fxml")
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner(window)
      stage.setTitle("Crear")
      stage.centerOnScreen()
      stage.show()
    }

    def openVisualize(window: Window): Unit = {
      val stage = StageLoader.load(getClass, "view/visualize.fxml")
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner(window)
      stage.setTitle("Ver Encuesta")
      stage.centerOnScreen()
      stage.show()
    }
  }

  object StageLoader {
    def load(context: Class[_], fxml: String): Stage = load(context, new Stage, fxml)

    def load(context: Class[_], stage: Stage, fxml: String): Stage = {
      getURL(context, "img/poller.png") match {
        case Some(icon) => stage.getIcons.add(new Image(icon.toExternalForm))
        case None =>
      }
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
      stage.setResizable(false)
      stage.sizeToScene()
      stage
    }

    def getURL(context: Class[_], resource: String) = Option.apply(context.getClassLoader.getResource(resource))
  }

}
