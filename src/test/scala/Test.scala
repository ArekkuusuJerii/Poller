import java.sql.Types

import com.google.gson.{Gson, GsonBuilder}
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
    val encuesta = PollBuilder + "Encuesta de Literatura" == "FEB-MAR - 2018"
    val pregunta1 = encuesta ?- "Que tan frecuentemente lee libros?"
    pregunta1 - "Diariamente"
    pregunta1 - "Cada semana"
    pregunta1 - "Cada mes"
    pregunta1 - "Cada siglo"
    pregunta1 - "Nunca"
    val pregunta2 = encuesta ?+ "Seleccione sus categorias preferidas"
    pregunta2 - "Libros de texto"
    pregunta2 - "Libros ficticios"
    pregunta2 - "Libros de poesia"
    pregunta2 - "Antologias"
    pregunta2 - "Otros"
    val pregunta3 = encuesta ? "Puede mencionar al menos un (1) libro de cada categoria?"

    //JSON
    val parser = new GsonBuilder().setPrettyPrinting().create
    println(parser.toJson(encuesta!!))
  }

  def deserialize(token: Token): Unit = {
    val poll = Poll.read(token)
    val json = new Gson()
    println(json.toJson(poll))
  }

  deserialize()
}
