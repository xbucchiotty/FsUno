package io.scala.uno

trait CommandHandler {

  self: EventStore =>

  private def streamId(gameId: Int) = s"DiscardPile-$gameId"

  def load(gameId: Int): (Int, State) = {
    (-1, State.initial)
  }

  def save(gameId: Int, expectedVersion: Int, events: Seq[Event]) {

  }

  def apply(gameId: Int)(command: Command) = {
    val (lastEvent, state) = load(gameId)

    val newEvents = DiscardPile.handle(command)(state)

    save(gameId, lastEvent, newEvents)
  }
}