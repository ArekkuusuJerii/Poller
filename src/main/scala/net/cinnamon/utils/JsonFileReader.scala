package net.cinnamon.utils

import java.io._
import java.util.Optional

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.cinnamon.helper.AlertHelper

class JsonFileReader[A](c: Class[A], file: File) {
  def deserialize(): Optional[A] = try {
    val fileReader = new FileReader(file)
    val buffReader = new BufferedReader(fileReader)
    val writer = new StringWriter
    var line = buffReader.readLine
    while (line != null) {
      writer.write(line)
      line = buffReader.readLine
    }
    buffReader.close()
    fileReader.close()
    val json = writer.toString

    Optional.of(new Gson().fromJson(json, TypeToken.get(c).getType))
  } catch {
    case e: Exception =>
      println(e)
      AlertHelper.showError("Ha ocurrido un error abriendo el archivo").showAndWait()
      Optional.empty()
  }
}
