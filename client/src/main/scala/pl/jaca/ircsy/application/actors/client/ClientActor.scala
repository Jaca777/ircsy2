package pl.jaca.ircsy.application.actors.client

import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import monix.execution.Ack.Continue
import pl.jaca.ircsy.application.actors.client.ClientActor._
import pl.jaca.ircsy.infrastructure.actors.MessageLatching
import pl.jaca.ircsy.infrastructure.irc.IrcClient
import pl.jaca.ircsy.infrastructure.irc.kitteh.KittehIrcClient
import pl.jaca.ircsy.model.irc.Actions._
import pl.jaca.ircsy.model.irc.Events._
import pl.jaca.ircsy.model.irc.Protocol._

import scala.concurrent.Future

class ClientActor(
  clientId: String,
  server: ServerDesc
) extends PersistentActor with ActorLogging with MessageLatching {


  override def receiveCommand: Receive = awaitingConnectAction orElse latch

  def awaitingConnectAction: Receive = {
    case Connect(clientData) =>
      val client = makeAndConnectClient(clientData)
      context become (connecting(client, clientData) orElse latch)
  }

  private def makeAndConnectClient(clientData: ClientData): IrcClient = {
    val client = new KittehIrcClient(server, clientData)
    pipeEvents(client)
    client.connect()
    client
  }

  private def pipeEvents(client: IrcClient) =
    client.events.subscribe(
      event => {
        self ! event
        publishEvent(event)
        Future.successful(Continue)
      })


  def connecting(client: IrcClient, clientData: ClientData): Receive = {
    case Connected =>
      unlatchNow()
      context become connected(client, ConnectedState(Set.empty, clientData))

    case FailedToConnect =>
      throw ClientFailedToConnectException
  }

  def connected(client: IrcClient, state: ConnectedState): Receive = {
    import state._
    {
      // Actions
      case action@JoinChannel(channel) =>
        persist(action)(_ => client.joinChannel(channel))

      case action@LeaveChannel(channel, msg) =>
        persist(action)(_ => client.leaveChannel(channel, msg))
        client.leaveChannel(channel, msg)

      case SendMessage(channel, msg) =>
        client.sendChannelMessage(channel, msg)

      case SendPrivateMessage(user, msg) =>
        client.sendPrivateMessage(user, msg)

      case Disconnect =>
        client.stop()

      //State events
      case JoinedChannel(channel) =>
        context become connected(client, state.copy(channels = channels + channel))

      case LeftChannel(channel) =>
        saveSnapshot(ClientSnapshot(channels))
        context become connected(client, state.copy(channels = channels - channel))

      case SendEventToParent(event) =>
        context.parent ! event

      case SaveSnapshotSuccess(data) =>
        log.debug(s"Saving snapshot for ${
          data.persistenceId
        } succeeded")

      case SaveSnapshotFailure(data, cause) =>
        log.error(cause, s"Saving snapshot for ${
          data.persistenceId
        } failed")

    }
  }

  private def publishEvent(event: Event) {
    self ! SendEventToParent(event)
  }


  override def persistenceId: String = s"$clientId@${server.host}:${server.port}"

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, state: ConnectingState) =>
      val clientData = state.clientData
      val client = makeAndConnectClient(clientData)
      context become connecting(client, clientData)

    case SnapshotOffer(_, state: ConnectedState) =>
      val clientData = state.clientData
      val client = makeAndConnectClient(clientData)
      context become connecting(client, clientData)
      for (channel <- state.channels)
        self ! JoinChannel(channel)

    case JoinedChannel(channel) =>
      self ! JoinChannel(channel)
  }

}

object ClientActor {

  case class ConnectingState(clientData: ClientData)

  case class ConnectedState(channels: Set[String], clientData: ClientData)

  case class SendEventToParent(event: Event)

  case class ClientSnapshot(channels: Set[String])

  case object ClientDisconnectedException extends RuntimeException

  case object ClientFailedToConnectException extends RuntimeException

}
