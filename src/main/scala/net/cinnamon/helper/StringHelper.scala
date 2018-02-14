package net.cinnamon.helper

import java.util.regex.Pattern

object StringHelper {

  private lazy val emailLong = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+\\.[A-Za-z0-9]+)$")
  private lazy val emailShort = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)$")

  def checkEmail(email: String): Boolean = emailShort.matcher(email).matches || emailLong.matcher(email).matches
}
