package net.cinnamon.helper

import java.util.Optional
import javafx.scene.control.{Alert, ButtonType}

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
