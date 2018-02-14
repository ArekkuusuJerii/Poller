package net.cinnamon.helper

import java.sql.{Connection, PreparedStatement}

object SequenceHelper {

	var lastKnownKey: Int = _

	def update(sequence: String, list: List[AnyRef], connection: Connection): Boolean = {
		var success = false
		try {
			val statement = connection.prepareStatement(sequence)
			if (sequence.contains("?")) polluteStatement(statement, list)

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
	}

	def query(sequence: String, list: List[AnyRef], connection: Connection)(f: Map[String, Unit] => Unit): Unit = {
		try {
			val statement = connection.prepareStatement(sequence)
			if (sequence.contains("?")) polluteStatement(statement, list)

			val result = statement.executeQuery()
			val metaData = result.getMetaData
			val columns = metaData.getColumnCount
			while (result.next) {
				val map = Map[String, Unit]()
				for (column <- 1 to columns) {
					map + metaData.getColumnClassName(column) -> result.getObject(column)
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
	}

	def getLastKnownKey: Option[Int] = lastKnownKey match {
		case n if n <= 0 => Option.empty
		case _ => Option(lastKnownKey)
	}

	private def polluteStatement(statement: PreparedStatement, list: List[AnyRef]): Unit = {
		list.zipWithIndex.foreach {
			case (o: AnyRef, count: Int) => statement.setObject(count, o)
		}
	}
}
