package io.scala.uno


case class Turn(player: Int, playerCount: Int)


object Turn {

  def initial(): Turn = new Turn(0, 1)

  def start(count: Int): Turn = Turn(0, count)
}


case class State(gameAlreadyStarted: Boolean, player: Turn, topCard: Card)

object State {

  def initial: State = new State(gameAlreadyStarted = false, player = Turn.initial(), topCard = Digit(0, Red))
}


object DiscardPile {


  def startGame(playerCount: Int, firstCard: Card)(state: State): Event = {
    ???
  }

  def handle(command: Command): (State => Event) = command match {
    case StartGame(playerCount, firstCard) => startGame(playerCount, firstCard)
  }


  def apply(state: State, event: Event) = event match {

    case GameStarted(playerCount, firstCard) =>
      State(gameAlreadyStarted = true, player = Turn.start(playerCount), topCard = firstCard)

  }

  def replay(events: Seq[Event]) = events.foldLeft(State.initial)(DiscardPile.apply)
}

