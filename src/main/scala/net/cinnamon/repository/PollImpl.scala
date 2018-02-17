package net.cinnamon.repository

import java.sql.Types

import net.cinnamon.helper.SequenceHelper

object PollImpl {
  def getIsPollActive(token: String): Boolean = {
    var out = false
    SequenceHelper.call(Map("token" -> token), Map("active" -> Types.BOOLEAN))("{call getIsPollActive(?,?)}",
      _.getOrElse("active", false) match {
        case any: Boolean => out = any;
      }
    )
    out
  }
}
