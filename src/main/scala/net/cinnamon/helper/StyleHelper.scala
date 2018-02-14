package net.cinnamon.helper

import javafx.scene.Node

object StyleHelper extends Enumeration {
  type Style = String
  val TextColor = "-fx-text-fill"

  def apply(implicit node: Node, style: Style, value: String): Unit = {
    node.setStyle(style + ":" + value + ";")
  }
}
