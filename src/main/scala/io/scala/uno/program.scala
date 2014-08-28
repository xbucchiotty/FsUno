package io.scala.uno

import scala.util.Random

object Program extends App{
	
	val commandHandler = new CommandHandler with GetEventStore

	def eventHandler(event: Event) = println(event)

	commandHandler.subscribe(eventHandler)

	val gameId = Random.nextInt(1000000)

	val handle = commandHandler(gameId)_

	  handle (StartGame(4, Digit(3, Red)))
    
    handle (PlayCard(0, Digit(3, Blue)))

    handle (PlayCard(1, Digit(8, Blue)))
    
    handle (PlayCard(2, Digit(8, Yellow)))
    
    handle (PlayCard(3, Digit(4, Yellow)))
    
    handle (PlayCard(0, Digit(4, Green)))
}