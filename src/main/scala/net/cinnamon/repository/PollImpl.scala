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

  def getRandomToken: Token = {
    var token = ""
    SequenceHelper.call(Map.empty, Map("token" -> Types.VARCHAR))("{call generateToken(?)}",
      _.getOrElse("token", "") match {
        case any: Token => token = any
      }
    )
    token
  }

  def registerPoll(title: String): Token = {
    val token = getRandomToken

    token
  }

  def addQuestion(text: String, kind: Tipo, token: Token): Unit = {

  }
}
