package net.cinnamon.repository

import java.sql.Types

import net.cinnamon.controller.Menu
import net.cinnamon.entity.Question.Kind
import net.cinnamon.helper.SequenceHelper

import scala.collection.mutable

object PollImpl {
  type Token = String

  def getIsPollActive(token: Token): Boolean = {
    var out = false
    SequenceHelper.call(Map("token" -> token), Map("active" -> Types.BOOLEAN))("{call getIsPollActive(?,?)}",
      _.getOrElse("active", false) match {
        case any: Boolean => out = any;
        case _ =>
      }
    )
    out
  }

  def getIsPollOwner(token: Token): Boolean = {
    var out = false
    val owner = Int.box(Menu.getId)
    SequenceHelper.call(Map("owner" -> owner, "token" -> token), Map("isOwner" -> Types.BOOLEAN))("{call getIsPollOwner(?,?,?)}",
      _.getOrElse("isOwner", false) match {
        case any: Boolean => out = any;
        case _ =>
      }
    )
    out
  }

  def createPoll(title: String, active: Boolean)(token: Token): Token = {
    var token = ""
    val owner = Int.box(Menu.getId)
    val in = mutable.Map(
      "title" -> title,
      "owner" -> owner,
      "active" -> active
    )
    if(token != null && token.nonEmpty) in += "token" -> token
    SequenceHelper.call(in.toMap, Map("token" -> Types.VARCHAR))("{call createPoll(?,?,?,?)}",
      _.getOrElse("token", "") match {
        case any: String => token = any
        case _ =>
      }
    )
    token
  }

  def createQuestion(text: String, kind: Kind, token: Token)(id: Int): Int = {
    var id = -1
    val in = mutable.Map(
      "text" -> text,
      "token" -> token,
      "kind" -> kind.id
    )
    if(id > 0) in += "id" -> id
    SequenceHelper.call(in.toMap, Map("id" -> Types.INTEGER))("{call createQuestion(?,?,?,?)}",
      _.getOrElse("id", -1) match {
        case any: Int if any > 0 => id = any
        case _ =>
      }
    )
    id
  }

  def createAnswer(text: String, question: Int)(id: Int): Int = {
    var id = -1
    val in = mutable.Map(
      "answer" -> text,
      "question" -> question
    )
    if(id > 0) in += "id" -> id
    SequenceHelper.call(in.toMap, Map("id" -> Types.INTEGER))("{call createAnswer(?,?,?)}",
      _.getOrElse("id", -1) match {
        case any: Int if any > 0 => id = any
        case _ =>
      }
    )
    id
  }
}
