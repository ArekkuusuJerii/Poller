package net.cinnamon.repository

import java.sql.Types

import net.cinnamon.helper.SequenceHelper

object RegisterImpl {
  def register(firstName: String, secondName: String, email: String, password: String): Boolean = {
    val in = Map(
      "firstName" -> firstName,
      "secondName" -> secondName,
      "email" -> email,
      "password" -> password
    )
    SequenceHelper.call(in, Map("success" -> Types.BOOLEAN))("{call register(?,?,?,?,?)}", {
      map => return map.getOrElse("success", false).asInstanceOf[Boolean]
    })
    false
  }
}
