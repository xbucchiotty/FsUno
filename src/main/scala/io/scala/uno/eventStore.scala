package io.scala.uno

trait EventStore{

	type Projection = (Event => Unit)

	def readStream(streamId: String, version: Int, count: Int): (Seq[Event], Int, Option[Int])

	def appendToStream(streamId: String, expectedVersion: Int, newEvents: Seq[Event]):Unit

	def subscribe(projection: Projection): Unit
}

class WrongExpectedVersion(m: String) extends RuntimeException(m)

trait ToyInMemoryEventStore extends EventStore{

	type Events = Seq[(Event,Int)]
	type Store = Map[String, Events]
	

	private var store:Store = Map.empty

	private var projections = Set.empty[Projection]


	override def subscribe(projection: Projection){
		projections += projection
	}

	override def readStream(streamId: String, version: Int, count: Int): (Seq[Event], Int, Option[Int]) = {
		store.get(streamId) match{

			case Some(stream) => 
				val events = stream.dropWhile{case (_,v) => v < version}
								   .takeWhile{case (_,v) => v <= version + count}
								   .toList

				val currentVersion = streamVersion(events)				   

				val nextVersion = stream
					.map{case (_, v) => v}
					.find{_ > version + count}

				(events.map{case (e,_) => e}, currentVersion, nextVersion)

			case None => (Seq.empty, -1, None)
		}
			
	}

	override def appendToStream(streamId: String, expectedVersion: Int, newEvents: Seq[Event]){
		val eventsWithVersion: Events = newEvents.zipWithIndex.map{case(e,v) => (e,v + expectedVersion + 1) }

		store.get(streamId) match{

			case Some(stream) if streamVersion(stream) == expectedVersion =>
				store += (streamId -> (stream ++ eventsWithVersion))

			case None if expectedVersion == -1 => 
				store += (streamId -> eventsWithVersion)

			case _ => 
				throw new WrongExpectedVersion(s"Wrong version for stream $streamId")
		}

		for{
			projection <- projections
		}{
			newEvents.foreach(projection)
		}
	}


	private def streamVersion(stream: Events) = stream.last._2


}