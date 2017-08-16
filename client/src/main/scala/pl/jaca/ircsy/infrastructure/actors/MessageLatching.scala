package pl.jaca.ircsy.infrastructure.actors

import akka.actor.AbstractActor.Receive
import akka.actor.Actor

/**
  * @author Jaca777
  *         Created 2017-08-06 at 16
  *
  * Buffers the messages and sends them
  * in the receiving order when the unlatchNow
  * method is called.
  *
  */
trait MessageLatching {
  this: Actor =>

  private var bufferedMessages = List[Any]()

  def latch: Receive = {
    case any =>
      this.bufferedMessages = any :: bufferedMessages
  }

  def unlatchNow() = {
    for {
      msg <- bufferedMessages
    } self ! msg
  }
}
