package io.scala.uno



case class Turn(player: Int, playerCount: Int){

	def next = this.copy(player = (player + 1) % playerCount)

	def isNot(requester: Int) = player != requester
}



object Turn{

	def apply(): Turn = new Turn(0,1)

	def start(count: Int): Turn = Turn(0, count)
}



case class State(gameAlreadyStarted: Boolean , player: Turn , topCard: Card)

object State{

	def empty: State = new State(gameAlreadyStarted = false, player = Turn(), topCard = Digit(0, Red) )	
}



object DiscardPile{



	def startGame(playerCount: Int, firstCard: Card)(state: State): GameStarted = {
		if(playerCount <= 2) 
			throw new IllegalArgumentException("You should be at least 3 players")
		
		if(state.gameAlreadyStarted)
			throw new IllegalStateException("You cannot start game twice")

		GameStarted(playerCount, firstCard)
	}

	def playCard (player: Int, card: Card)(state: State) = {
		if(state.player.isNot(player)){
			throw new IllegalStateException("Player should play at his turn")
		}

		(card,state.topCard) match{
			case (Digit(n1, color1), Digit(n2, color2) ) if n1 == n2 || color1 == color2 =>
				CardPlayed(player, card)

			case _ =>
				throw new IllegalStateException("Play same color or same value !")
		}

	}


	def handle(command: Command)= command match{
			case StartGame(playerCount, firstCard) => startGame(playerCount, firstCard)(_)
			case PlayCard (player, card)           => playCard (player, card)(_)
		
	}


	def apply(state:State, event:Event) = event match{
		
		case GameStarted(playerCount, firstCard) => 
			State(gameAlreadyStarted=true, player = Turn.start(playerCount), topCard = firstCard)

		case CardPlayed(player, card) =>
			state.copy(player = state.player.next, topCard = card)	
	}

	def replay(events:Seq[Event]) = events.foldLeft(State.empty)(DiscardPile.apply)
}

