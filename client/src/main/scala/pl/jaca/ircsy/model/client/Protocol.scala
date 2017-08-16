package pl.jaca.ircsy.model.client

object Protocol {

  case class ServerDesc(host: String, port: Int)

  case class AuthCredentials(login: String, password: String)

}
