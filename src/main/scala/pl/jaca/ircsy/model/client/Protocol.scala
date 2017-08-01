package pl.jaca.ircsy.model.client

object Protocol {

  case class ServerDesc(host: String, port: String)

  case class AuthCredentials(login: String, password: String)

}
