package net.cinnamon.helper

import javafx.scene.{Node, Parent}

import net.cinnamon.utils.StageLoader.getURL

object StyleHelper extends Enumeration {
  type Style = String
  val TextColor = "-fx-text-fill"

  def apply(node: Node, style: Style, value: String): Unit = {
    node.setStyle(style + ":" + value + ";")
  }

  def apply(node: Parent, style: Style): Unit = {
    getURL(getClass, style) match {
      case Some(css) => node.getStylesheets.add(css.toExternalForm)
      case None =>
    }
  }
}
