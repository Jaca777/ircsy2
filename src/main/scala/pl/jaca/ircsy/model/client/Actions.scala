package pl.jaca.ircsy.model.client

import pl.jaca.ircsy.model.client.Protocol.{AuthCredentials, ServerDesc}

object Actions {
  sealed trait Action

  case object Connect extends Action

  case object Disconnect extends Action

  case class SendMessage(channel: String, message: String) extends Action

  case class SendPrivateMessage(user: String, message: String) extends Action

  case class JoinChannel(channel: String) extends Action

  case class LeaveChannel(channel: String) extends Action
}
