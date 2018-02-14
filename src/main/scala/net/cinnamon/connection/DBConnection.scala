package net.cinnamon.connection

import java.sql.{Connection, DriverManager}
import java.util.{Locale, ResourceBundle}

object DBConnection {

	object I18n {
		def getBundle(implicit locale: Locale): ResourceBundle = ResourceBundle.getBundle("config", locale)

		def localized(key: String)(implicit locale: Locale): String = getBundle.getString(key)
	}

	def getConnection: Option[Connection] = {
		implicit val locale: Locale = Locale.ROOT
		//Database data
		val ip = I18n.localized("ip_address")
		val database = I18n.localized("database")
		val port = I18n.localized("port")
		//User data
		val user = I18n.localized("user")
		val password = I18n.localized("password")
		//Driver to use
		val driver = I18n.localized("driver")

		Driver.find(driver) match {
			case Some(format) => Option.apply(DriverManager.getConnection(
				String.format(format, ip, database, port), user, password)
			)
			case None => Option.empty
		}
	}

	object Driver {
		type Driver = String
		val MySql: Driver = "jdbc:mysql://%1$s:%2$s/%3$s"
		val SqlServer: Driver = "jdbc:sqlserver://%1$s:%3$s;databaseName=%2$s;authenticationScheme=JavaKerberos"

		def find(driver: String): Option[Driver] = driver.toLowerCase match {
			case "mysql" => Option.apply(MySql)
			case "sqlserver" => Option.apply(SqlServer)
		}
	}
}
