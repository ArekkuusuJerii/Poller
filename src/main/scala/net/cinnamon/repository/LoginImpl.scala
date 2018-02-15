package net.cinnamon.repository

import java.sql.Types

import net.cinnamon.helper.SequenceHelper

object LoginImpl {
  def canLogin(user: String, password: String): Boolean = {
    SequenceHelper.call(Map("email" -> user, "password" -> password), Map("login" -> Types.BOOLEAN))("{call canLogin(?,?,?)}", {
      map => return map.getOrElse("login", false).asInstanceOf[Boolean]
    })
    false
  }
}
