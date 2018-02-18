package net.cinnamon.repository

import java.sql.Types

import net.cinnamon.controller.Login
import net.cinnamon.entity.Tipo
import net.cinnamon.helper.SequenceHelper

object PollImpl {
  type Token = String

  def getIsPollActive(token: Token): Boolean = {
    var out = false
    SequenceHelper.call(Map("token" -> token), Map("active" -> Types.BOOLEAN))("{call getIsPollActive(?,?)}",
      _.getOrElse("active", false) match {
        case any: Boolean => out = any;
      }
    )
    out
  }

  def getIsPollOwner(token: Token): Boolean = {
    var out = false
    val owner = Int.box(Login.userID)
    SequenceHelper.call(Map("owner" -> owner, "token" -> token), Map("isOwner" -> Types.BOOLEAN))("{call getIsPollOwner(?,?,?)}",
      _.getOrElse("isOwner", false) match {
        case any: Boolean => out = any;
      }
    )
    out
  }

  def createPoll(title: String, active: Boolean): Token = {
    var token = ""
    val owner = Int.box(Login.userID)
    val in = Map(
      "title" -> title,
      "owner" -> owner,
      "active" -> active
    )
    SequenceHelper.call(in, Map("token" -> Types.VARCHAR))("{call createPoll(?,?,?,?)}",
      _.getOrElse("token", "") match {
        case any: String if any != null => token = any
      }
    )
    token
  }

  def createQuestion(text: String, kind: Tipo, token: Token): Unit = {

  }
}
