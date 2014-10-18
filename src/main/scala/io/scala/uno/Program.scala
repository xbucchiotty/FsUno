package io.scala.uno

import scala.util.Random

object Program extends App {

  val commandHandler = new CommandHandler with GetEventStore

  def eventHandler(event: Event) = println(event)

  commandHandler.subscribe(eventHandler)

  val gameId = Random.nextInt(1000000)

  val handle = commandHandler(gameId) _

  handle(StartGame(4, Digit(3, Red)))

}


