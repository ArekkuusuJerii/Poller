package net.cinnamon.repository

import java.sql.Types

import net.cinnamon.controller.Login
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

  def getIsPollOwner(token: String): Boolean = {
    var out = false
    SequenceHelper.call(Map("owner" -> Login.userID, "token" -> token), Map("isOwner" -> Types.BOOLEAN))("{call getIsPollOwner(?,?)}",
      _.getOrElse("isOwner", false) match {
        case any: Boolean => out = any;
      }
    )
    out
  }
}
