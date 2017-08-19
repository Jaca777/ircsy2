package pl.jaca.ircsy.application.actors.client

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, OneForOneStrategy, SupervisorStrategy}
import akka.remote.WireFormats.TimeUnit
import pl.jaca.ircsy.application.actors.client.ClientSupevisorActor.Start
import pl.jaca.ircsy.model.irc.Protocol.{AuthCredentials, ServerDesc}
import sun.awt.geom.AreaOp.SubOp

import scala.concurrent.duration._

class ClientSupevisorActor(
  clientId: String,

) extends Actor {
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 1.minute) {
    case _ => SupervisorStrategy.Escalate
  }

  def receive: Receive = {
    case _ => ???
  }
}

object ClientSupevisorActor {
  object Start
}
