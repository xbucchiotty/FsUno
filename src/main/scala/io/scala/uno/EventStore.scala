package io.scala.uno

import java.io.Closeable

import akka.actor.{Actor, ActorSystem}
import eventstore._

import scala.concurrent.Await
import EventStore.Projection

trait EventStore {

  def readStream(streamId: String, version: Int, count: Int): (Seq[Event], Int, Option[Int])

  def appendToStream(streamId: String, expectedVersion: Int, newEvents: Seq[Event]): Unit

  def subscribe(projection: Projection): Unit
}

object EventStore {
  type Projection = (Event => Unit)
}

class WrongExpectedVersion(m: String) extends RuntimeException(m)

trait ToyInMemoryEventStore extends EventStore {

  def subscribe(projection: Projection) {
    ???
  }

  override def readStream(streamId: String, version: Int, count: Int): (Seq[Event], Int, Option[Int]) = {
    ???
  }

  override def appendToStream(streamId: String, expectedVersion: Int, newEvents: Seq[Event]) {
    ???
  }


}

trait GetEventStore extends EventStore {

  import eventstore.{EsConnection, EventNumber, EventStream, ExpectedVersion, ReadStreamEvents, Settings}

  import scala.concurrent.duration._

  private val system = ActorSystem()

  import system.dispatcher

  private val connection: EsConnection = EsConnection(system, Settings(defaultCredentials = Some(UserCredentials("admin", "changeit"))))

  def subscribe(projection: Projection) {
    connection.subscribeToAll(new SubscriptionObserver[IndexedEvent] {
      override def onError(e: Throwable) {
        println(s"[ERROR] : $e")
      }

      override def onEvent(event: IndexedEvent, subscription: Closeable) {
        val unoEvent = deserializeEvent(event.event.data)
        projection(unoEvent)
      }

      override def onClose() {

      }

      override def onLiveProcessingStart(subscription: Closeable) {

      }
    })
  }

  override def readStream(streamId: String, version: Int, count: Int): (Seq[Event], Int, Option[Int]) = {

    Await.result(connection.future(
      ReadStreamEvents(
        EventStream(streamId)))
      .map(stream => {
      val events = stream.events.map(_.data).map(deserializeEvent)

      val version = stream.lastEventNumber.value

      val nextSliceVersion = if (stream.endOfStream) {
        None
      } else {
        Some(stream.nextEventNumber.asInstanceOf[EventNumber.Exact].value)
      }

      (events, version, nextSliceVersion)
    }).recover {
      case EsException(EsError.StreamNotFound, _) => (Seq.empty[Event], -1, None)
    }, atMost = 5.seconds)
  }

  override def appendToStream(streamId: String, expectedVersion: Int, newEvents: Seq[Event]) {

    val eventData = newEvents.map(serializeEvent).toList

    val streamVersion = if (expectedVersion == -1) ExpectedVersion.NoStream else ExpectedVersion(expectedVersion)

    val writeOperation = connection.future(WriteEvents(
      EventStream(streamId),
      eventData, streamVersion))

    Await.ready(writeOperation
      , atMost = 5.seconds)

  }

  import scala.pickling._
  import scala.pickling.json._

  def serializeEvent(event: Event): EventData = {
    val output = new StringOutput()
    event.pickleTo(output)
    val json = output.result()

    EventData.Json(event.getClass.getSimpleName, data = json)

  }

  def deserializeEvent(data: EventData): Event = data match {
    case EventData(_, _, Content(bytes, _), _) =>
      bytes.decodeString("UTF-8").unpickle[Event]
  }

}