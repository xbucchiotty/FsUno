package io.scala.uno

class StartGameSpec extends UnoSpec {

  let("Started game should be started") {
    Given() ~>
      When(StartGame(4, Digit(3, Red))) ~>
      Expect(Seq(GameStarted(4, Digit(3, Red))))
  }

}