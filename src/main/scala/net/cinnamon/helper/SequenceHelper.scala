package net.cinnamon.helper

import java.sql.{PreparedStatement, SQLType}

import net.cinnamon.connection.DataBaseConnection

import scala.collection.mutable

object SequenceHelper {

  var lastKnownKey: Int = _

  def update(data: List[_])(implicit sequence: String): Boolean = {
    DataBaseConnection.getConnection match {
      case Some(connection) =>
        var success = false
        try {
          val statement = connection.prepareStatement(sequence)
          if (sequence.contains("?")) polluteStatement(statement, data)
          success = statement.executeUpdate > 0
          val keys = statement.getGeneratedKeys
          lastKnownKey = if (keys.next) keys.getInt(1) else 0
          statement.close()
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          try {
            connection.close()
          } catch {
            case e: Exception => e.printStackTrace()
          }
          success
        }
        success
      case None => false
    }
  }

  def query(data: List[_])(implicit sequence: String, f: mutable.Map[String, AnyRef] => Unit): Unit = {
    DataBaseConnection.getConnection match {
      case Some(connection) =>
        try {
          val statement = connection.prepareStatement(sequence)
          if (sequence.contains("?")) polluteStatement(statement, data)
          val result = statement.executeQuery()
          val metaData = result.getMetaData
          val columns = metaData.getColumnCount
          while (result.next) {
            val map = mutable.Map[String, AnyRef]()
            for (column <- 1 to columns) {
              val name = metaData.getColumnName(column)
              val value = result.getObject(column)
              map += name -> value
            }
            f(map)
          }
          result.close()
          statement.close()
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          try {
            connection.close()
          } catch {
            case e: Exception => e.printStackTrace()
          }
        }
      case None =>
    }
  }

  private def polluteStatement(statement: PreparedStatement, list: List[_]): Unit = {
    list.zipWithIndex.foreach {
      case (o: Unit, count: Int) => statement.setObject(count, o)
    }
  }

  def call(in: Map[String, AnyRef], out: Map[String, SQLType])(implicit call: String, f: mutable.Map[String, AnyRef] => Unit): Unit = {
    DataBaseConnection.getConnection match {
      case Some(connection) =>
        try {
          val statement = connection.prepareCall(call)
          in foreach {case (param, any) => statement.setObject(param, any)}
          out foreach { case (param, kind) => statement.registerOutParameter(param, kind) }
          statement.execute()
          val result = statement.getResultSet
          val metaData = result.getMetaData
          val columns = metaData.getColumnCount
          while (result.next) {
            val map = mutable.Map[String, AnyRef]()
            for (column <- 1 to columns) {
              val name = metaData.getColumnName(column)
              val value = result.getObject(column)
              map += name -> value
            }
            f(map)
          }
          result.close()
          statement.close()
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          try {
            connection.close()
          } catch {
            case e: Exception => e.printStackTrace()
          }
        }
      case None =>
    }
  }

}
