module Uno.EventStore(EventStore(..),Version,applyCommand) where

import Uno.Command
import Uno.DiscardPile
import Uno.Event


type Version = Integer

class EventStore a where

    readStream :: a -> Version -> Integer -> ([Event], Version, Maybe Version)

    writeToStream :: a -> Version -> [Event] -> IO ()



foldEvent :: (EventStore a) => a -> State -> Version -> (Version, State)
foldEvent aggregate lastState version =
    case nextVersion of Nothing -> (streamVersion, newState)
                        Just n  -> foldEvent aggregate newState n
     where (events, streamVersion, nextVersion) = readStream aggregate version 500
           newState = foldl applyEvent lastState events



load :: (EventStore a) => a -> (Version,State)
load aggregate = foldEvent aggregate emptyState 0



applyCommand :: (EventStore a) => a -> Command -> IO ()
applyCommand aggregate command =
 writeToStream aggregate currentVersion [action $ currentState]
 where (currentVersion, currentState) = load aggregate
       action = handle command