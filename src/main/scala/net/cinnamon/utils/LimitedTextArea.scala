package net.cinnamon.utils

import javafx.beans.property.{IntegerProperty, SimpleIntegerProperty}
import javafx.scene.control.TextArea

class LimitedTextArea extends TextArea {
  private val maxLength: IntegerProperty = new SimpleIntegerProperty(-1)

  import java.util.Objects
  import javafx.beans.property.IntegerProperty

  def maxLengthProperty: IntegerProperty = maxLength

  def getMaxLength: Integer = maxLength.getValue

  def setMaxLength(maxLength: Integer): Unit = {
    Objects.requireNonNull(maxLength, "Max length cannot be null, -1 for no limit")
    this.maxLength.setValue(maxLength)
  }

  override def replaceText(start: Int, end: Int, insertedText: String): Unit = {
    if (this.getMaxLength <= 0) {
      super.replaceText(start, end, insertedText)
    }
    else {
      val currentText = if (this.getText == null) "" else this.getText
      val finalText = currentText.substring(0, start) + insertedText + currentText.substring(end)
      val exceeding = finalText.length - this.getMaxLength
      if (exceeding <= 0) {
        super.replaceText(start, end, insertedText)
      }
      else {
        val cutInsertedText = insertedText.substring(0, insertedText.length - exceeding)
        super.replaceText(start, end, cutInsertedText)
      }
    }
  }
}
