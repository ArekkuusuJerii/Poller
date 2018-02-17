import java.sql.Types

import net.cinnamon.connection.DataBaseConnection
import net.cinnamon.helper.SequenceHelper
import net.cinnamon.repository.RegisterImpl

object Test extends App {
  def connectionTest() {
    DataBaseConnection.getConnection match {
      case None =>
        print("Fail!")
      case Some(connection) =>
        connection.close()
        print("Success!")
    }
  }

  def callTest() {
    SequenceHelper.call(Map(), Map())("{call test}", {
      map => print(map.getOrElse("hi", ""))
    })
  }

  def callInTest() {
    SequenceHelper.call(Map("some" -> "Hello World!"), Map.empty)("{call test_in(?)}", {
      map => print(map.getOrElse("value", ""))
    })
  }

  def callOutTest() {
    SequenceHelper.call(Map.empty, Map("value" -> Types.INTEGER))("{call test_out(?)}", {
      map => print(map.getOrElse("value", ""))
    })
  }

  def checkEmail() {
    lazy val emails = List("ed@gmail.com", "ed@gail.com")
    emails foreach(email => println(email, RegisterImpl.canCreateAccount(email)))
  }

  checkEmail()
}
