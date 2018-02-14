import net.cinnamon.connection.DBConnection

object Test {
	def main(args: Array[String]): Unit = {
		DBConnection.getConnection match {
			case None =>
				print("Fail!")
			case Some(_) =>
				print("Success!")
		}
	}
}
