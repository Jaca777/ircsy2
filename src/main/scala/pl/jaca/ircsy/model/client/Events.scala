package pl.jaca.ircsy.model.client

object Events {

  sealed trait Event

  object Disconnected extends Event

  object Connected extends Event

  case class JoinedChannel(channel: String)

  case class LeftChannel(channel: String)

  // Failures

  sealed trait FailureEvent

  case class FailedToConnect(cause: String) extends FailureEvent

  case class FailedToJoinChannel(channel: String, cause: String) extends FailureEvent

}
