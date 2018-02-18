import java.sql.Types

import com.google.gson.Gson
import net.cinnamon.connection.DataBaseConnection
import net.cinnamon.helper.SequenceHelper
import net.cinnamon.repository.PollImpl.Token
import net.cinnamon.repository.RegisterImpl
import net.cinnamon.utils.PollBuilder

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

  def deserialize(): Unit = {
    val poll = PollBuilder + "Test"
    poll ? "Is this a test?" - "Yes" - "No"
    poll ?+ "Are you sure?" - "Yes" - "Yes" - "Yes"
    poll ?- "Really...?"
    val json = new Gson()
    println(json.toJson(poll!))
  }

  deserialize()
}
