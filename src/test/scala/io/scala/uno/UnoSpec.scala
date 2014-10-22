package io.scala.uno

import org.scalatest._


trait UnoSpec extends FunSuite with ShouldMatchers with GivenWhenThen {

  def let(testName: String)(spec: Specification) = {
    import spec._

    test(testName) {

      Given(givenEvents.mkString(", "))
      When(command.toString)
      Then(expectedEvents.mkString(", "))

      val state = DiscardPile.replay(givenEvents)

      val events = DiscardPile.handle(command)(state)

      events should be(expectedEvents)
    }
  }

  def Given(givenEvents: Seq[Event] = Nil) = new {
    def ~>(when: When) = new {
      def ~>(expect: Expect): Specification = Specification(givenEvents, when.command, expect.expectedEvents)
    }
  }

  case class When(command: Command)

  case class Expect(expectedEvents: Seq[Event])

  case class Specification(givenEvents: Seq[Event], command: Command, expectedEvents: Seq[Event])


}
