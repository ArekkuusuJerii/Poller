package net.cinnamon.repository

import java.sql.Types
import java.util.Optional

import net.cinnamon.helper.SequenceHelper

object LoginImpl {
  def login(user: String, password: String): Optional[Integer] = {
    var option: Optional[Integer] = Optional.empty()
    SequenceHelper.call(Map("email" -> user, "password" -> password), Map("login" -> Types.INTEGER))("{call login(?,?,?)}", {
      _.getOrElse("login", -1) match {
        case any: Integer if any > 0 => option = Optional.of(any)
      }
    })
    option
  }
}
