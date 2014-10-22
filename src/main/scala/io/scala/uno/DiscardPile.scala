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


  def startGame(playerCount: Int, firstCard: Card)(state: State): Seq[Event] = Seq.empty

  def handle(command: Command): (State => Seq[Event]) = command match {
    case StartGame(playerCount, firstCard) => startGame(playerCount, firstCard)
  }


  def evolve(state: State, event: Event): State = event match {

    case GameStarted(playerCount, firstCard) =>
      State.initial

  }

  def replay(events: Seq[Event]): State = events.foldLeft(State.initial)(DiscardPile.evolve)
}

