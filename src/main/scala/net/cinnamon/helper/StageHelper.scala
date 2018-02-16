package net.cinnamon.helper

import javafx.stage.Stage

import net.cinnamon.utils.StageLoader

object StageHelper {
  def openLogin(stage: Stage): Unit = {
    StageLoader.load(getClass, stage, "view/login.fxml")
    stage.setTitle("Login")
    stage.centerOnScreen()
    stage.show()
  }

  def openLogin(): Unit = openLogin(new Stage())

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
}
