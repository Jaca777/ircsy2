package pl.jaca.ircsy.model.irc

/**
  * @author Jaca777
  *         Created 2017-08-16 at 20
  */
object Exceptions {
  case class NotInAChannelException(channel: String)
    extends RuntimeException(s"Cannot perform action, the client is not in connected to the $channel channel")

}
