package net.cinnamon.helper

import java.sql.{CallableStatement, PreparedStatement}

import net.cinnamon.connection.DataBaseConnection

import scala.collection.mutable

object SequenceHelper {

  def update(data: List[_])(implicit sequence: String): Boolean = {
    DataBaseConnection.getConnection match {
      case Some(connection) =>
        var success = false
        try {
          val statement = connection.prepareStatement(sequence)
          if (sequence.contains("?")) polluteStatement(statement, data)
          success = statement.executeUpdate > 0
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

  def query(data: List[_])(sequence: String, function: mutable.Map[String, AnyRef] => Unit): Unit = {
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
            function(map)
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

  def call(in: Map[String, AnyRef], out: Map[String, Int])(call: String, function: mutable.Map[String, AnyRef] => Unit): Unit = {
    def setOut(statement: CallableStatement): Unit =
      out foreach { case (param, kind) => statement.registerOutParameter(param, kind) }
    def setIn(statement: CallableStatement): Unit =
      in foreach { case (param, any) => statement.setObject(param, any) }
    DataBaseConnection.getConnection match {
      case Some(connection) =>
        try {
          val statement = connection.prepareCall(call)
          setOut(statement)
          setIn(statement)
          statement.execute()
          if (out.isEmpty) {
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
              function(map)
            }
            result.close()
          } else {
            val map = mutable.Map[String, AnyRef]()
            out.keySet foreach (param => map += (param -> statement.getObject(param)))
            function(map)
          }
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
