import net.cinnamon.connection.DataBaseConnection

object ConnectionTest extends App {
  DataBaseConnection.getConnection match {
    case None =>
      print("Fail!")
    case Some(connection) =>
      connection.close()
      print("Success!")
  }
}
