package io.scala.uno

import scala.annotation.tailrec

trait CommandHandler{

	self: EventStore => 

	private def streamId(gameId: Int) = s"DiscardPile-$gameId"

	private def load(gameId: Int): (Int, State) = {

		@tailrec
		def fold(state:State, version:Int): (Int, State) = {

			val (events, lastEvent, nextEvent) = readStream(streamId(gameId), version, 500)

			val newState = events.foldLeft(state)(DiscardPile.apply)

			nextEvent match {
				case None    => (lastEvent, newState)
				case Some(n) => fold(newState,n)
			}
		}

		fold(State.empty, 0)
	}

	private def save(gameId: Int, expectedVersion: Int, events: Seq[Event]){
		appendToStream(streamId(gameId), expectedVersion, events)
	}

	def apply (gameId: Int)(command: Command) = {
		val (lastEvent,state) = load(gameId)

    println(s"gameId $gameId")
    println(s"command $command")
    println(s"lastEvent: $lastEvent")
    println(s"state: $state")

		
		val newEvent = DiscardPile.handle(command)(state)

		save(gameId, lastEvent, Seq(newEvent))
	}
}


//Later on, Module Async