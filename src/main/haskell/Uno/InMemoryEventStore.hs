module Uno.InMemoryEventStore() where

import Uno.EventStore
import Uno.Event


type StreamId = String
type GameId = String

data Game = Game {gameId :: GameId}

streamId :: Game -> StreamId
streamId game = "Uno" ++ (gameId  game)

instance EventStore Game where

    readStream game version chunk = ([], 0, Nothing)

    writeToStream game version events = putStrLn ((gameId game) ++ (show events))