package pl.jaca.ircsy.application.actors.client

import akka.actor.{Actor, ActorRef, ActorSelection, OneForOneStrategy, Props, SupervisorStrategy}
import pl.jaca.ircsy.application.actors.client.ClientSupervisorActor._
import pl.jaca.ircsy.model.irc.Actions.{Connect, Disconnect}
import pl.jaca.ircsy.model.irc.Events.Event
import pl.jaca.ircsy.model.irc.Protocol.{ClientData, ServerDesc}

import scala.concurrent.duration._

class ClientSupervisorActor(
  clientId: String,
  serverDesc: ServerDesc,
  eventCollector: ActorSelection
) extends Actor {
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 1.minute) {
    case _ => SupervisorStrategy.Escalate //TODO exceptions
  }

  override def receive: Receive = idle

  def idle: Receive = {
    case Start(clientData) =>
      val client = context.actorOf(Props(new ClientActor(clientId, serverDesc)), "client")
      client ! Connect(clientData)
      context become supervising(client)
  }

   def supervising(client: ActorRef): Receive = {
    case event: Event  => eventCollector ! event

    case stop: Stop =>
       client ! Disconnect
       context.stop(client)
       context become idle
  }
}

object ClientSupervisorActor {
  case class ClientEvent(clientId: String, serverDesc: ServerDesc, event: Event)

  case class Start(clientData: ClientData)

  object Stop
}
