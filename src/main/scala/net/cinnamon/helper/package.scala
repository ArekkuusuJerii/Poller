package net.cinnamon

import java.io.IOException
import java.util.Optional
import javafx.fxml.FXMLLoader
import javafx.scene.control.{Alert, ButtonType}
import javafx.scene.image.Image
import javafx.scene.{Node, Parent, Scene}
import javafx.stage.Stage

package helper {

  import javafx.scene.control.{Label, TextInputDialog}
  import javafx.stage.{Modality, Window}

  import net.cinnamon.controller.PollController.PaneNode
  import net.cinnamon.controller.StatisticController.Statistic
  import net.cinnamon.controller._
  import net.cinnamon.entity.Question
  import net.cinnamon.entity.Question.Kind
  import net.cinnamon.repository.PollImpl.Token

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

    def showTextInput(text: String, input: String)(in: String): Optional[String] = {
      val dialog: TextInputDialog = new TextInputDialog(in)
      StyleHelper.apply(dialog.getDialogPane, "css/modern_dark.css")
      dialog.setHeaderText(text)
      dialog.setContentText("Periodo")
      dialog.showAndWait()
    }
  }

  object StyleHelper {
    type Style = String
    val TextFont: Style = "-fx-font"
    val TextColor: Style = "-fx-text-fill"
    val BorderColor: Style = "-fx-border-color"
    val BackgroundColor: Style = "-fx-background-color"

    def apply(node: Node, style: Style, value: String): Unit = {
      val append = node.getStyle match {
        case pre: String => pre + ";" + style + ":" + value + ";"
        case _ => style + ":" + value + ";"
      }
      node.setStyle(append)
    }

    def apply(node: Parent, style: Style): Unit = {
      StageLoader.getURL(getClass, style) match {
        case Some(css) => node.getStylesheets.add(css.toExternalForm)
        case None =>
      }
    }
  }

  object StageHelper {
    implicit def apply[A](a: A): Unit = {}

    def openLogin(email: String = ""): Unit = openLogin(new Stage(), email)

    def openLogin(stage: Stage, email: String): Unit = {
      StageLoader.load(classOf[LoginController], stage, "view/login.fxml")(l => l setEmail email )
      stage.setTitle("Login")
      stage.centerOnScreen()
      stage.show()
    }

    def openMenu(email: String): Unit = {
      val stage = StageLoader.load(classOf[MenuController], "view/menu.fxml")(m => {
        email match {
          case e: String if e nonEmpty => m.setEmail(e)
          case _ => m.setEmail(MenuController.getEmail)
        }
      })
      stage.setTitle("Menu")
      stage.centerOnScreen()
      stage.show()
    }

    def openRegister(): Unit = {
      val stage = StageLoader.load(classOf[RegisterController], "view/register.fxml")
      stage.setTitle("Register")
      stage.centerOnScreen()
      stage.show()
    }

    def openPoll(token: Token): Unit = {
      val stage = StageLoader.load(classOf[PollController], "view/poll.fxml")(p => p.open(token))
      stage.setTitle("Encuesta")
      stage.centerOnScreen()
      stage.show()
    }

    def openUseToken(window: Window): Unit = {
      val stage = StageLoader.load(classOf[UseToken],"view/use_token.fxml")(u => u.setParent(window))
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner(window)
      stage.setTitle("Token")
      stage.centerOnScreen()
      stage.show()
    }

    def openViewToken(window: Window): Unit = {
      val stage = StageLoader.load(classOf[ViewToken],"view/view_token.fxml")(u => u.setParent(window))
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner(window)
      stage.setTitle("Token")
      stage.centerOnScreen()
      stage.show()
    }

    def openUpload(window: Window): Unit = {
      val stage = StageLoader.load(classOf[UploadController], "view/upload.fxml")(u => u.setParent(window))
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner(window)
      stage.setTitle("Crear")
      stage.centerOnScreen()
      stage.show()
    }

    def openStatistic(window: Window, title: String, token: Token): Unit = {
      val stage = StageLoader.load(classOf[StatisticController], "view/statistic.fxml")(s => {
        s.setToken(token)
        s.setTitle(title)
      })
      stage.initModality(Modality.WINDOW_MODAL)
      stage.setTitle("Ver Encuesta")
      stage.initOwner(window)
      stage.centerOnScreen()
      stage.show()
    }

    def openToken(window: Window, token: Token): Unit = {
      val stage = StageLoader.load(classOf[TokenController],"view/token.fxml")(t => t setToken token )
      stage.initModality(Modality.WINDOW_MODAL)
      stage.initOwner(window)
      stage.setTitle("Token")
      stage.centerOnScreen()
      stage.show()
    }

    def loadQuestion(list: java.util.List[(PaneNode, Parent)], question: Question): Parent = {
      val view = question.kind match {
        case Kind.SINGLE => "view/part/single_question.fxml"
        case Kind.MULTIPLE => "view/part/multiple_question.fxml"
        case Kind.OPEN => "view/part/open_question.fxml"
      }
      StageLoader.getURL(getClass, view) match {
        case Some(layout) =>
          try {
            val loader = new FXMLLoader(layout)
            val parent: Parent = loader.load()
            val controller = loader.getController[PaneNode]
            controller.loadQuestion(question)
            list.add((controller, parent))
            return parent
          } catch {
            case e: IOException =>
              e.printStackTrace()
          }
        case None =>
      }
      null
    }

    def loadStatistic(token: Token, term: String, question: Question): Parent = {
      val view = question.kind match {
        case Kind.OPEN => "view/part/statistic_list.fxml"
        case _ => "view/part/statistic_chart.fxml"
      }
      StageLoader.getURL(getClass, view) match {
        case Some(layout) =>
          try {
            val loader = new FXMLLoader(layout)
            val parent: Parent = loader.load()
            val controller = loader.getController[Statistic]
            controller.load(token, term, question)
            return parent
          } catch {
            case e: IOException =>
              e.printStackTrace()
          }
        case None =>
      }
      null
    }

    def loadInput(text: String): Parent = {
      StageLoader.getURL(getClass, "view/part/statistic_input.fxml") match {
        case Some(layout) =>
          try {
            val loader = new FXMLLoader(layout)
            val parent: Parent = loader.load()
            val label: Label = parent.lookup("#lb_text").asInstanceOf[Label]
            label.setText(text)
            return parent;
          } catch {
            case e: IOException =>
              e.printStackTrace()
          }
        case None =>
      }
      null
    }
  }

  object StageLoader {
    def load[A](context: Class[A], fxml: String)(implicit f: A => Unit): Stage = load(context, new Stage, fxml)(f)

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
            stage.setResizable(false)
            stage.setScene(scene)
            stage.sizeToScene()
          } catch {
            case e: IOException =>
              e.printStackTrace()
          }
        case None =>
      }
      stage
    }

    def getURL(context: Class[_], resource: String) = Option.apply(context.getClassLoader.getResource(resource))
  }

}
