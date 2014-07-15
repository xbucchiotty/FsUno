package io.scala.uno

sealed trait Event

case class GameStarted(playerCount: Int, firstCard: Card) extends Event
case class CardPlayed (player: Int, card :Card) extends Event