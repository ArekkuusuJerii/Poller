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

  import net.cinnamon.controller._
  import net.cinnamon.entity.Encuesta

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
    def openLogin(email: String = ""): Unit = openLogin(new Stage(), email)

    def openLogin(stage: Stage, email: String): Unit = {
      StageLoader.load(classOf[Login], stage, "view/login.fxml")(l => l.setEmail(email))
      stage.setTitle("Login")
      stage.centerOnScreen()
      stage.show()
    }

    def openMenu(email: String): Unit = {
      val stage = StageLoader.load(classOf[Menu], "view/menu.fxml")(m => {
        m.setEmail(email)
      })
      stage.setTitle("Menu")
      stage.centerOnScreen()
      stage.show()
    }

    def openRegister(): Unit = {
      val stage = StageLoader.load(classOf[Registro], "view/registro.fxml")(() => _)
      stage.setTitle("Register")
      stage.centerOnScreen()
      stage.show()
    }

    def openPoll(): Unit = {
      val stage = StageLoader.load(classOf[Encuesta], "view/encuesta.fxml")(() => _)
      stage.setTitle("Encuesta")
      stage.centerOnScreen()
      stage.show()
    }

    def openFile(window: Window): Unit = {
      val stage = StageLoader.load(classOf[Upload], "view/upload.fxml")(() => _)
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner(window)
      stage.setTitle("Crear")
      stage.centerOnScreen()
      stage.show()
    }

    def openVisualize(window: Window): Unit = {
      val stage = StageLoader.load(classOf[Visualizar], "view/visualize.fxml")(() => _)
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner(window)
      stage.setTitle("Ver Encuesta")
      stage.centerOnScreen()
      stage.show()
    }

    def openToken(window: Window, token: String): Unit = {
      val stage = StageLoader.load(classOf[Token], "view/token.fxml")(t => t.setToken(token))
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner(window)
      stage.setTitle("Token")
      stage.centerOnScreen()
      stage.show()
    }
  }

  object StageLoader {
    def load[A](context: Class[A], fxml: String)(f: A => Unit): Stage = load(context, new Stage, fxml)(f)

    def load[A](context: Class[A], stage: Stage, fxml: String)(f: A => Unit): Stage = {
      getURL(context, "img/poller.png") match {
        case Some(icon) => stage.getIcons.add(new Image(icon.toExternalForm))
        case None =>
      }
      getURL(context, fxml) match {
        case Some(layout) =>
          try {
            val loader = new FXMLLoader(layout)
            val scene = new Scene(loader.load())
            f(loader.getController[A])
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
