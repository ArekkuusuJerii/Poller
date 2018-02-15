package net.cinnamon.repository

import net.cinnamon.connection.DataBaseConnection

object LoginImpl {
  def canLogin(user: String, password: String): Boolean = {
    DataBaseConnection.getConnection match {
      case None =>
      case Some(connection) =>
        connection.close()
        return true
    }
    false
  }
}
