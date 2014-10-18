package io.scala.uno

sealed trait Event

case class GameStarted(playerCount: Int, firstCard: Card) extends Event