package io.scala.uno

trait CommandHandler {

  self: EventStore =>

  private def streamId(gameId: Int) = s"DiscardPile-$gameId"

  private def load(gameId: Int): (Int, State) = {
    ???
  }

  private def save(gameId: Int, expectedVersion: Int, events: Seq[Event]) {
    ???
  }

  def apply(gameId: Int)(command: Command) = {
    val (lastEvent, state) = load(gameId)

    val newEvent = DiscardPile.handle(command)(state)

    save(gameId, lastEvent, Seq(newEvent))
  }
}