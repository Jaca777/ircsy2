package pl.jaca.ircsy.model.client

import pl.jaca.ircsy.model.client.Protocol.{AuthCredentials, ServerDesc}

object Actions {
  sealed trait Action

  case class Connect(server: ServerDesc, nickname: String, authData: Option[AuthCredentials]) extends Action

  object Disconnect extends Action

  case class SendMessage(channel: String, message: String) extends Action

  case class SendPrivateMessage(user: String, message: String) extends Action

  case class JoinChannel(channel: String) extends Action

  case class LeaveChannel(channel: String) extends Action
}
