package pl.jaca.ircsy.model.irc

object Protocol {

  case class ServerDesc(host: String, port: Int)

  case class ClientData(nickname: String, authCredentials: Option[AuthCredentials] = None)

  case class AuthCredentials(login: String, password: String)

}
