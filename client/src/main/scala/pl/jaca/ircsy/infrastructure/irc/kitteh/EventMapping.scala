package pl.jaca.ircsy.infrastructure.irc.kitteh

import org.kitteh.irc.client.library.event.channel.{ChannelJoinEvent, ChannelMessageEvent}
import org.kitteh.irc.client.library.event.client.{ClientConnectedEvent, ClientReceiveCommandEvent, ClientReceiveNumericEvent}
import org.kitteh.irc.client.library.event.helper.ClientEvent
import org.kitteh.irc.client.library.event.user.{PrivateMessageEvent, ServerNoticeEvent}
import pl.jaca.ircsy.model.irc.Events._
import scala.collection.JavaConverters._

/**
  * @author Jaca777
  *         Created 2017-08-16 at 21
  *
  *         Mapps kitteh events to more compact and fit for distributed solution ircsy model.
  */
private[irc] object EventMapping {

  def apply(event: ClientEvent): Event = event match {

    case e: ChannelMessageEvent =>
      ReceivedChannelMessage(e.getChannel.getName, e.getMessage)

    case e: PrivateMessageEvent =>
      ReceivedPrivateMessage(e.getActor.getNick, e.getMessage)

    case e: ChannelJoinEvent =>
      JoinedChannel(e.getAffectedChannel.get().getName)

    case e: ClientReceiveNumericEvent =>
      NumericEvent(e.getNumeric)

    case e: ServerNoticeEvent =>
      ReceivedServerNotice(e.getMessage)

    case e: ClientReceiveCommandEvent =>
      ReceivedClientCommand(e.getCommand, e.getParameters.asScala.toList)

    case e: ClientConnectedEvent =>
      Connected
  }



  def undefined(e: ClientEvent) =
    UnsupportedEvent(s"Mapping client event ${e.getClass.getName} failed, mapping not defined.")
}
