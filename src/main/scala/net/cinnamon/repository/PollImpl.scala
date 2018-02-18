package net.cinnamon.repository

import java.sql.Types

import net.cinnamon.controller.Login
import net.cinnamon.entity.Tipo
import net.cinnamon.helper.SequenceHelper

import scala.collection.mutable

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

  def createPoll(title: String, active: Boolean)(token: Token): Token = {
    var token = ""
    val owner = Int.box(Login.userID)
    val in = mutable.Map(
      "title" -> title,
      "owner" -> owner,
      "active" -> active
    )
    if(token != null && token.nonEmpty) in += "token" -> token
    SequenceHelper.call(in.toMap, Map("token" -> Types.VARCHAR))("{call createPoll(?,?,?,?)}",
      _.getOrElse("token", "") match {
        case any: String if any != null => token = any
      }
    )
    token
  }

  def createQuestion(text: String, kind: Tipo, token: Token)(id: Int): Int = {
    var id = -1
    val in = mutable.Map(
      "text" -> text,
      "kind" -> kind.ordinal(),
      "token" -> token
    )
    if(id > 0) in += "id" -> id
    SequenceHelper.call(in.toMap, Map("id" -> Types.VARCHAR))("{call createPoll(?,?,?,?)}",
      _.getOrElse("token", -1) match {
        case any: Int if any > 0 => id = any
      }
    )
    id
  }
}
