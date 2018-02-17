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
          polluteStatement(statement, data)
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

  private def polluteStatement(statement: PreparedStatement, list: List[_]): Unit = {
    list.zipWithIndex.foreach {
      case (o: Unit, count: Int) => statement.setObject(count, o)
    }
  }

  def query(data: List[_])(sequence: String, function: Map[String, AnyRef] => Unit): Unit = {
    DataBaseConnection.getConnection match {
      case Some(connection) =>
        try {
          val statement = connection.prepareStatement(sequence)
          polluteStatement(statement, data)
          if (statement.execute())
            executeStatement(statement, function)
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

  def call(in: Map[String, AnyRef], out: Map[String, Int])(call: String, function: Map[String, AnyRef] => Unit): Unit = {
    def setOut(statement: CallableStatement): Unit =
      out foreach { case (param, kind) => statement.registerOutParameter(param, kind) }

    def setIn(statement: CallableStatement): Unit =
      in foreach { case (param, any) => statement.setObject(param, any) }

    def collectOutputs(statement: CallableStatement): Unit = {
      val map = mutable.Map[String, AnyRef]()
      out.keySet foreach (param => map += (param -> statement.getObject(param)))
      function(map.toMap)
    }

    DataBaseConnection.getConnection match {
      case Some(connection) =>
        try {
          val statement = connection.prepareCall(call)
          setOut(statement)
          setIn(statement)
          if (statement.execute() && out.isEmpty)
            executeStatement(statement, function)
          if (out.nonEmpty)
            collectOutputs(statement)
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

  def executeStatement(statement: PreparedStatement, function: Map[String, AnyRef] => Unit): Unit = {
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
      function(map.toMap)
    }
    result.close()
    if (statement.getMoreResults) executeStatement(statement, function)
  }
}
