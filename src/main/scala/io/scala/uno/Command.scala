package io.scala.uno

sealed trait Command

case class StartGame(playerCount: Int, firstCard: Card) extends Command