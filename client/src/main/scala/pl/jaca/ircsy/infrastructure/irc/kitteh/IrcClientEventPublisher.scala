package pl.jaca.ircsy.infrastructure.irc.kitteh

import monix.reactive.subjects.PublishSubject
import net.engio.mbassy.listener.Handler
import org.kitteh.irc.client.library.Client
import org.kitteh.irc.client.library.event.helper.ClientEvent
import pl.jaca.ircsy.model.client.Events

/**
  * @author Jaca777
  *         Created 2017-08-16 at 21
  */
private[irc] case class IrcClientEventPublisher(client: Client) {
  def publishTo(clientEvents: PublishSubject[_ >: Events.Event]) : Unit =
    client.getEventManager.registerEventListener(new Listener(clientEvents))

  private class Listener(clientEvents: PublishSubject[_ >: Events.Event]){
    @Handler
    def handle(event: ClientEvent) =
      clientEvents.onNext(EventMapping(event))
  }

}
