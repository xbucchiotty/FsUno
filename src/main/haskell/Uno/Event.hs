module Uno.Event(Event(..)) where

import Uno.Deck

data Event = GameStarted Integer Card | CardPlayed Player Card deriving Show