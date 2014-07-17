package io.scala.uno

import scala.annotation.tailrec

trait EventStore{

	def readStream(streamId: String, offset: Int,chunkSize: Int): (Seq[Event], Event, Option[Int])

	def appendToStream(streamId: String, expectedVersion: Event, events: Seq[Event]):Unit
}


trait CommandHandler{

	self: EventStore => 

	private def streamId(gameId: Int) = s"DiscardPile-$gameId"

	private def load(gameId: Int): (Event, State) = {

		@tailrec
		def fold(state:State, version:Int): (Event, State) = {

			val (events, lastEvent, nextEvent) = readStream(streamId(gameId), version, 500)

			val newState = events.foldLeft(state)(DiscardPile.apply)

			nextEvent match {
				case None    => (lastEvent, newState)
				case Some(n) => fold(newState,n)
			}
		}

		fold(State.empty, 0)
	}

	private def save(gameId: Int, expectedVersion: Event, events: Seq[Event]){
		appendToStream(streamId(gameId), expectedVersion, events)
	}

	def apply (command: Command, gameId: Int) = {
		val (lastEvent,state) = load(gameId)
		
		val newEvent = DiscardPile.handle(command)(state)

		save(gameId, lastEvent, Seq(newEvent))
	}
}


//Later on, Module Async