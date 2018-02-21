import java.sql.Types

import com.google.gson.Gson
import net.cinnamon.connection.DataBaseConnection
import net.cinnamon.entity.Poll
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
    val poll = PollBuilder + "Encuesta de Ejemplo" > "FEB-MAR"
    poll ? "Puedes seleccionar una respuesta?" - "Si" - "No"
    poll ?+ "¿Estas seguro de ello?" - "Absolutamente" - "Claro que no" - "No lo sé"
    poll ?- "¿Muy muy seguro?"
    val json = new Gson()
    println(json.toJson(poll!!))
  }

  def deserialize(token: Token): Unit = {
    val poll = Poll.read(token)
    val json = new Gson()
    println(json.toJson(poll))
  }

  deserialize()
}
