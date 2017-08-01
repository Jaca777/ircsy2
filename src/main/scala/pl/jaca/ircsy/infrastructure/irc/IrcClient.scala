package pl.jaca.ircsy.infrastructure.irc


import monix.reactive.Observable
import pl.jaca.ircsy.model.client.Events.Event
import pl.jaca.ircsy.model.client.Protocol.{AuthCredentials, ServerDesc}

class IrcClient(
  serverDesc: ServerDesc,
  nickname: String,
  authCredentials: Option[AuthCredentials]
) {

  def connect() = ???

  def events(): Observable[Event] = ???

  def sendMessage(channel: String, msg: String) = ???

  def sendPrivateMessage(receiver: String, msg: String) = ???

  def joinChannel(channel: String) = ???

  def leaveChannel(channel: String) = ???

  def disconnect() = ???


}
