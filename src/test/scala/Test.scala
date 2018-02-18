import java.sql.Types

import net.cinnamon.connection.DataBaseConnection
import net.cinnamon.helper.SequenceHelper
import net.cinnamon.repository.PollImpl.Token
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

  def callTest(): Unit = {
    SequenceHelper.call(Map(), Map())("{call test}", {
      map => print(map.getOrElse("hi", ""))
    })
  }

  def callInTest(): Unit = {
    SequenceHelper.call(Map("some" -> "Hello World!"), Map.empty)("{call test_in(?)}", {
      map => print(map.getOrElse("value", ""))
    })
  }

  def callOutTest(): Unit = {
    SequenceHelper.call(Map.empty, Map("value" -> Types.INTEGER))("{call test_out(?)}", {
      map => print(map.getOrElse("value", ""))
    })
  }

  def checkEmail(): Unit = {
    lazy val emails = List("ed@gmail.com", "ed@gail.com")
    emails foreach (email => println(email, RegisterImpl.canCreateAccount(email)))
  }

  def callRandomToken(): Unit = {
    SequenceHelper.call(Map.empty, Map("token" -> Types.VARCHAR))("{call generateToken(?)}",
      _.getOrElse("token", "") match {
        case any: Token => println(any)
      }
    )
  }

  callRandomToken()
}
