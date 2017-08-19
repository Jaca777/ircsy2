package pl.jaca.ircsy.application

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.Server
import pl.jaca.ircsy.application.actors.client.ClientActor
import pl.jaca.ircsy.model.irc.Actions._
import pl.jaca.ircsy.model.irc.Protocol.{ClientData, ServerDesc}

object ClientApplication extends App {
  val system = ActorSystem()
  system.actorOf(Props(new Actor {

    val client = context.actorOf(Props(new ClientActor("1", ServerDesc(f"irc.freenode.net", 7070))))
    client ! Connect(ClientData("IrcsyBot"))
    Thread.sleep(20000)
    client ! JoinChannel("#dup")
    client ! SendMessage("#dup", "ELO")
    client ! LeaveChannel("#dup", "chuj")
    client ! Disconnect

    override def receive: Receive = {
      case any => println(any)
    }
  }))


}
