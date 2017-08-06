package pl.jaca.ircsy.application.actors.client

import akka.actor.ActorLogging
import akka.persistence.PersistentActor
import monix.execution.Ack
import monix.execution.Ack.Continue
import monix.reactive.Observable
import monix.reactive.subjects.{AsyncSubject, PublishSubject, Subject}
import pl.jaca.ircsy.application.actors.client.ClientActor._
import pl.jaca.ircsy.infrastructure.irc.IrcClient
import pl.jaca.ircsy.model.client.Actions._
import pl.jaca.ircsy.model.client.Events._
import pl.jaca.ircsy.model.client.Protocol._

import scala.concurrent.Future

class ClientActor(server: ServerDesc, nickname: String, credentials: Option[AuthCredentials]) extends PersistentActor with ActorLogging {

  private val client = new IrcClient(server, nickname, credentials)
  pipeEvents(client)
  client.connect()

  override def receiveCommand: Receive = connecting

  def connecting: Receive = {
    case Connected =>
      context become connected(Set.empty)

    case FailedToConnect =>
      throw ClientFailedToConnectException

    case action: Action =>
      log.warning(s"Unable to perform action $action, the client is not connected.")
  }


  private def pipeEvents(client: IrcClient) =
    client.events().subscribe(
      event => {
        context.self ! event
        Future.successful(Continue)
      })


  def connected(channels: Set[String]): Receive = {
    // Actions
    case JoinChannel(channel) =>
      client.joinChannel(channel)

    case LeaveChannel(channel) =>
      client.leaveChannel(channel)

    case SendMessage(channel, msg) =>
      client.sendMessage(channel, msg)

    case SendPrivateMessage(user, msg) =>
      client.sendPrivateMessage(user, msg)

    case Disconnect =>
      client.stop()

    //State events
    case event@JoinedChannel(channel) =>
      context become connected(channels + channel)
      persist(event)(publishEvent)

    case event@LeftChannel(channel) =>
      context become connected(channels - channel)
      saveSnapshot(ClientSnapshot(channels))
      publishEvent(event)

    case Disconnected =>
      throw ClientDisconnectedException
  }

  private def publishEvent(event: Event) {
    context.parent ! event
  }

  override def persistenceId: String = s"$server@$nickname"

  override def receiveRecover: Receive = {
    case JoinedChannel(channel) =>
      client.events().doOnNext {
        case Connected =>
          self ! JoinChannel(channel)
      }
  }

}

object ClientActor {

  case class ClientSnapshot(channels: Set[String])

  case object ClientDisconnectedException extends RuntimeException

  case object ClientFailedToConnectException extends RuntimeException

}
