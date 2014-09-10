module Uno.Deck (
    Color(..),
    Card(..),
    Direction(..),
    Player) where

data Color = Red | Green | Blue | Yellow deriving (Show,Eq)

data Card = Digit Int Color | KickBack Color deriving (Show,Eq)

data Direction = ClockWise | CounterClockWise deriving (Show,Eq)

type Player = Integer