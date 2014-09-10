package io.scala.uno

sealed trait Color

case object Red    extends Color
case object Green  extends Color
case object Blue   extends Color
case object Yellow extends Color


sealed trait Card

case class Digit   (value:Int, color:Color) extends Card
case class KickBack(color:Color) 			extends Card

sealed trait Direction
case object  ClockWise 		 extends Direction
case object  CounterClockWise extends Direction
