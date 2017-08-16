package pl.jaca.ircsy.infrastructure.irc.kitteh

import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent
import org.kitteh.irc.client.library.event.helper.ClientEvent
import org.kitteh.irc.client.library.event.user.PrivateMessageEvent
import pl.jaca.ircsy.model.client.Events.{Event, ReceivedChannelMessage, ReceivedPrivateMessage}

/**
  * @author Jaca777
  *         Created 2017-08-16 at 21
  */
private[irc] object EventMapping {
  def apply(event: ClientEvent): Event = event match {
    case e: ChannelMessageEvent =>
      ReceivedChannelMessage(e.getChannel.getName, e.getMessage)
    case e: PrivateMessageEvent =>
      ReceivedPrivateMessage(e.getActor.getNick, e.getMessage)
  }
}
