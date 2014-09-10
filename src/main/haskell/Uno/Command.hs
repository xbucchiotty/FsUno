module Uno.Command(Command(..)) where

import Uno.Deck

data Command = StartGame Integer Card | PlayCard Player Card deriving Show