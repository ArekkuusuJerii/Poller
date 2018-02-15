package net.cinnamon.repository

import java.sql.Types
import java.util.Optional

import net.cinnamon.helper.SequenceHelper

object LoginImpl {
  def login(user: String, password: String): Optional[Integer] = {
    SequenceHelper.call(Map("email" -> user, "password" -> password), Map("login" -> Types.INTEGER))("{call login(?,?,?)}", {
      map =>
        map.getOrElse("login", -1) match {
          case any: Integer => return Optional.of(any)
          case -1 =>
        }
    })
    Optional.empty()
  }
}
