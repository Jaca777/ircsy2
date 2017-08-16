package pl.jaca.ircsy.infrastructure.irc.kitteh

import monix.reactive.Observable
import monix.reactive.subjects.PublishSubject
import org.kitteh.irc.client.library.element.Channel
import org.kitteh.irc.client.{library => irc}
import pl.jaca.ircsy.infrastructure.irc.IrcClient
import pl.jaca.ircsy.model.client.Events.Event
import pl.jaca.ircsy.model.client.Exceptions
import pl.jaca.ircsy.model.client.Protocol.{AuthCredentials, ServerDesc}

import scala.util.{Failure, Try}
import scala.collection.JavaConverters._

class KittehIrcClient(
  serverDesc: ServerDesc,
  nickname: String,
  authCredentials: Option[AuthCredentials]
) extends IrcClient {

  private var client: irc.Client = _
  private val clientEvents: PublishSubject[Event] = PublishSubject()

  override def connect(): Try[Unit] =
    if (client == null) Try {
      client = buildClient()
      handleEvents()
    } else {
      Failure(new IllegalStateException("The client has already connected to IRC server"))
    }

  private def buildClient(): irc.Client = {
    irc.Client.builder()
      .bindHost(serverDesc.host)
      .bindPort(serverDesc.port)
      .nick(nickname)
      .build()
  }

  private def handleEvents(): Unit = {
    IrcClientEventPublisher(client).publishTo(clientEvents)
  }

  override def events: Observable[Event] = clientEvents

  override def sendMessage(channel: String, msg: String): Try[Unit] = Try {
    require(channel.startsWith("#"), "Channel name has to start with a # character")
    client.sendMessage(channel, msg)
  }

  override def sendPrivateMessage(receiver: String, msg: String): Try[Unit] = Try {
    require(!receiver.startsWith("#"), "Receiver name mustn't start with a # character")
    client.sendMessage(receiver, msg)
  }

  override def joinChannel(channel: String): Try[Unit] = Try {
    client.addChannel(channel)
  }

  override def leaveChannel(channel: String, reason: String): Try[Unit] = {
    tryGetChannel(channel).map(_.part(reason))
  }

  private def tryGetChannel(channel: String): Try[Channel] = {
    val optChannel = client.getChannel(channel)
    if (optChannel.isPresent) Try(optChannel.get())
    else Failure(Exceptions.NotInAChannelException(channel))
  }

  override def getActiveChannels: Set[String] = client.getChannels.asScala.toSet.map(_.getName)

  override def stop(): Unit = {
    clientEvents.onComplete()
  }

}
