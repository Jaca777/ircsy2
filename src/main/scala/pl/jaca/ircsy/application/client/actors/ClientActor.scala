package pl.jaca.ircsy.application.client.actors

import akka.actor.ActorLogging
import akka.persistence.PersistentActor
import pl.jaca.ircsy.application.client.actors.ClientActor.ClientConnectionState
import pl.jaca.ircsy.model.client.Actions._
import pl.jaca.ircsy.model.client.Protocol._

class ClientActor(id: String) extends PersistentActor with ActorLogging {
  override def receiveRecover: Receive = ???

  override def receiveCommand: Receive = disconnected

  def disconnected: Receive = {
    case Connect(desc, nickname, data) =>
      //connect
      context become connected(ClientConnectionState(desc, List.empty))
    case action: Action =>
      log.error(s"Unable to perform action $action, the client is not connected.")
  }

  def connected(state: ClientConnectionState): Receive = {

  }

  override def persistenceId: String = ???
}

object ClientActor {
  case class ClientConnectionState(server: ServerDesc, channels: List[String])
}