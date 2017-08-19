package pl.jaca.ircsy.infrastructure.irc
import monix.reactive.Observable
import pl.jaca.ircsy.model.irc.Events

import scala.collection.mutable
import scala.util.Try

/**
  * @author Jaca777
  *         Created 2017-08-16 at 22
  */
trait IrcClient {

  def connect(): Unit

  def events: Observable[Events.Event]

  def sendChannelMessage(channel: String, msg: String): Unit

  def sendPrivateMessage(receiver: String, msg: String): Unit

  def joinChannel(channel: String): Unit

  def leaveChannel(channel: String, reason: String): Unit

  def getActiveChannels: Set[String]

  def stop(): Unit
}
