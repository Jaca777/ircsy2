package pl.jaca.ircsy.infrastructure.irc
import monix.reactive.Observable
import pl.jaca.ircsy.model.client.Events

import scala.collection.mutable
import scala.util.Try

/**
  * @author Jaca777
  *         Created 2017-08-16 at 22
  */
trait IrcClient {

  def connect(): Try[Unit]

  def events: Observable[Events.Event]

  def sendMessage(channel: String, msg: String): Try[Unit]

  def sendPrivateMessage(receiver: String, msg: String): Try[Unit]

  def joinChannel(channel: String): Try[Unit]

  def leaveChannel(channel: String, reason: String): Try[Unit]

  def getActiveChannels: mutable.Set[String]

  def stop(): Unit
}
