package pl.jaca.ircsy.infrastructure.irc.kitteh

import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject
import org.kitteh.irc.client.library.element.Channel
import org.kitteh.irc.client.{library => irc}
import pl.jaca.ircsy.infrastructure.irc.IrcClient
import pl.jaca.ircsy.model.irc.Events.Event
import pl.jaca.ircsy.model.irc.Exceptions
import pl.jaca.ircsy.model.irc.Exceptions.NotInAChannelException
import pl.jaca.ircsy.model.irc.Protocol.{AuthCredentials, ClientData, ServerDesc}

import scala.util.{Failure, Try}
import scala.collection.JavaConverters._

class KittehIrcClient(
  serverDesc: ServerDesc,
  clientData: ClientData
) extends IrcClient {

  private var client: irc.Client = _
  private val clientEvents: PublishSubject[Event] = PublishSubject()

  override def connect(): Unit =
    if (client == null) Try {
      client = buildClient()
      handleEvents()
    } else {
      Failure(new IllegalStateException("The client has already connected to IRC server"))
    }

  private def buildClient(): irc.Client = {
    irc.Client.builder()
      .serverHost(serverDesc.host)
      .serverPort(serverDesc.port)
      .nick(clientData.nickname)
      .user("Ircsy")
      .build()
  }

  private def handleEvents(): Unit = {
    IrcClientEventPublisher(client).publishTo(clientEvents)
  }

  override def events: Observable[Event] = clientEvents

  override def sendChannelMessage(channel: String, msg: String): Unit = {
    tryGetChannel(channel).sendMessage(msg)
  }

  override def sendPrivateMessage(receiver: String, msg: String): Unit = {
    require(!receiver.startsWith("#"), "Receiver name mustn't start with a # character")
    client.sendMessage(receiver, msg)
  }

  override def joinChannel(channel: String): Unit = {
    client.addChannel(channel)
  }

  override def leaveChannel(channel: String, reason: String): Unit = {
    tryGetChannel(channel).part(reason)
  }

  private def tryGetChannel(channel: String): Channel = {
    val optChannel = client.getChannel(channel)
    if (optChannel.isPresent) optChannel.get()
    else throw NotInAChannelException(channel)
  }

  override def getActiveChannels: Set[String] = client.getChannels.asScala.map((c: Channel) => c.getName).toSet

  override def stop(): Unit = {
    clientEvents.onComplete()
  }

}
