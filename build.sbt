resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies += "org.scalaz.stream" %% "scalaz-stream" % "0.5a"

libraryDependencies += "io.spray" %% "spray-client" % "1.3.1"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.4"

libraryDependencies += "com.geteventstore" %% "eventstore-client" % "0.5.0"

libraryDependencies += "org.scala-lang" %% "scala-pickling" % "0.8.0"

retrieveManaged := true

scalaVersion := "2.11.2"

