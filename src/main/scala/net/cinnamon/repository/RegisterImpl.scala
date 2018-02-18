package net.cinnamon.repository

import java.sql.Types

import net.cinnamon.helper.SequenceHelper

object RegisterImpl {
  def register(firstName: String, secondName: String, email: String, password: String): Boolean = {
    var out = false
    val in = Map(
      "firstName" -> firstName,
      "secondName" -> secondName,
      "email" -> email,
      "password" -> password
    )
    SequenceHelper.call(in, Map("success" -> Types.BOOLEAN))("{call register(?,?,?,?,?)}",
      _.getOrElse("success", false) match {
        case any: Boolean => out = any
        case _ =>
      }
    )
    out
  }

  def canCreateAccount(email: String): Boolean = {
    var out = false
    SequenceHelper.call(Map("email" -> email), Map("confirm" -> Types.BOOLEAN))("{call canCreateAccount(?,?)}",
      _.getOrElse("confirm", false) match {
        case any: Boolean => out = any
        case _ =>
      }
    )
    out
  }
}
