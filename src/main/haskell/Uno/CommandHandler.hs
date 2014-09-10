module Uno.CommandHandler(GameId, Version, ReadStream, WriteToStream, applyCommand) where

import Uno.Command
import Uno.DiscardPile
import Uno.Event

type GameId = Integer
type Version = Integer
type StreamId = String

type ReadStream    = StreamId -> Version -> Integer -> ( [Event], Version, Maybe Version )
type WriteToStream = StreamId -> Version -> [Event] -> ()



streamId :: GameId -> StreamId
streamId gameId = "DiscardPile-" ++ show gameId



foldEvent :: ReadStream -> StreamId -> State -> Version -> (Version, State)
foldEvent readStream currentStreamId lastState version =
    case nextVersion of Nothing -> (streamVersion, newState)
                        Just n -> foldEvent readStream currentStreamId newState n
     where (events, streamVersion, nextVersion) = readStream currentStreamId  version 500
           newState = foldl applyEvent lastState events



load :: StreamId -> ReadStream -> (Version,State)
load currentStreamId readStream = foldEvent readStream currentStreamId emptyState 0



save :: WriteToStream -> StreamId -> Version -> [Event] -> ()
save writer currentStreamId expectedVersion events =
    writer currentStreamId expectedVersion events



applyCommand :: ReadStream -> WriteToStream -> GameId -> Command -> ()
applyCommand reader writer gameId command =
    save writer currentStreamId currentVersion [action $ currentState]
    where currentStreamId = streamId gameId
          (currentVersion, currentState) = load currentStreamId reader
          action = handle command