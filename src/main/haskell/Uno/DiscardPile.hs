module Uno.DiscardPile(
    Turn(..),
    State(..),
    handle,
    emptyState,
    applyEvent,
    replay) where


import Uno.Deck
import Uno.Event
import Uno.Command


data Turn = Turn{ player :: Player
                , playerCount :: Integer } deriving Show

next :: Turn -> Turn
next from = Turn (mod ((player from) + 1)  (playerCount from)) (playerCount from)

emptyTurn :: Turn
emptyTurn = Turn 0 1

start :: Integer -> Turn
start count = Turn 0 count

data State = State { gameAlreadyStarted :: Bool
                   , current :: Turn
                   , topCard :: Card} deriving Show

emptyState :: State
emptyState = State False emptyTurn (Digit 0 Red)


startGame :: Integer -> Card -> State -> Event
startGame playerCount firstCard state
    | playerCount < 2 = error "You should be at least 3 players"
    | gameAlreadyStarted state = error "You cannot start game twice"
    | otherwise = GameStarted playerCount firstCard


playCard :: Player -> Card -> State -> Event
playCard currentPlayer playedCard state
    | (player $ next $ current $ state) /= currentPlayer = error "Player should play at his turn"
    | otherwise = let lastCard = topCard state in
        case (playedCard , lastCard) of ((Digit playedNumber playedColor), (Digit lastNumber lastColor))
                                         | playedNumber == lastNumber || playedColor  == lastColor -> CardPlayed currentPlayer playedCard
                                        ( _ , _ )  -> error "Play same color or some value!"

handle :: Command -> State -> Event
handle (StartGame playerCount firstCard) = startGame playerCount firstCard
handle (PlayCard player playedCard) = playCard player playedCard

applyEvent :: State -> Event -> State
applyEvent state event = case event of GameStarted playerCount firstCard -> State True (start playerCount) firstCard
                                       CardPlayed player card -> State True (next $ current $ state) card

replay :: [Event] -> State
replay events = foldl applyEvent emptyState events