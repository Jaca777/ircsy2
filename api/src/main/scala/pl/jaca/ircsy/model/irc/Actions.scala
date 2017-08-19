package pl.jaca.ircsy.model.irc

import pl.jaca.ircsy.model.irc.Protocol.ClientData

object Actions {
  sealed trait Action

  case object Disconnect extends Action

  case class Connect(clientData: ClientData) extends Action

  case class SendMessage(channel: String, message: String) extends Action

  case class SendPrivateMessage(user: String, message: String) extends Action

  case class JoinChannel(channel: String) extends Action

  case class LeaveChannel(channel: String, msg: String) extends Action
}
