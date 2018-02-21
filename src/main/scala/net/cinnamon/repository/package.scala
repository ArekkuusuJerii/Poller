package net.cinnamon

package repository {

  import java.sql.Types
  import java.util.Optional

  import net.cinnamon.helper.SequenceHelper.call
  import net.cinnamon.repository.helper.->

  import scala.reflect.ClassTag

  object helper {
    def ->[A](map: Map[String, AnyRef], out: String)(f: A => Unit)(implicit tag: ClassTag[A]): Unit = map.getOrElse(out, null) match {
      case any: A => f(any)
      case _ =>
    }
  }

  object LoginImpl {
    def login(user: String, password: String): Optional[Integer] = {
      var option: Optional[Integer] = Optional.empty()
      call(Map("email" -> user, "password" -> password), Map("login" -> Types.INTEGER))("{call login(?,?,?)}",
        ->[Int](_, "login")(any => {
          option = Optional.of(any)
        })
      )
      option
    }
  }

  object RegisterImpl {
    def register(firstName: String, secondName: String, email: String, password: String): Boolean = {
      var out = false
      val in = Map(
        "firstName" -> firstName,
        "secondName" -> secondName,
        "email" -> email,
        "password" -> password
      )
      call(in, Map("success" -> Types.BOOLEAN))("{call register(?,?,?,?,?)}",
        ->[Boolean](_, "success")(out = _)
      )
      out
    }

    def canCreateAccount(email: String): Boolean = {
      var out = false
      call(Map("email" -> email), Map("confirm" -> Types.BOOLEAN))("{call canCreateAccount(?,?)}",
        ->[Boolean](_, "confirm")(out = _)
      )
      out
    }
  }

}
