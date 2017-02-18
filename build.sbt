val slickVersion = "3.2.0-RC1"
val akkaVersion = "2.4.17"
val playVersion = "2.5.12"

scalaVersion in ThisBuild := "2.11.8"

organization in ThisBuild := "com.github.dnvriend"

parallelExecution in Test in ThisBuild := false

fork in Test in ThisBuild := true

libraryDependencies in ThisBuild += "com.h2database" % "h2" % "1.4.193"
libraryDependencies in ThisBuild += "com.typesafe.slick" %% "slick" % slickVersion
libraryDependencies in ThisBuild += "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
libraryDependencies in ThisBuild += "com.lihaoyi" %% "pprint" % "0.4.3"

lazy val playDeps: Seq[ModuleID] = Seq(
  "com.typesafe.play" %% "play-specs2" % playVersion,
  "com.typesafe.play" %% "play-jdbc-api" % playVersion,
  "com.typesafe.play" %% "play-jdbc-evolutions" % playVersion
)

lazy val slick3Test = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .dependsOn(playSlick)
  .dependsOn(playSlickEvolutions)
  .settings(
    libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.8",
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7",
    // https://www.playframework.com/documentation/2.5.x/PlaySlick
    libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % Test,
    libraryDependencies += "org.typelevel" %% "scalaz-scalatest" % "1.1.1" % Test
  )

lazy val playSlick = (project in file("play-slick"))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .settings(libraryDependencies ++= playDeps)

lazy val playSlickEvolutions = (project in file("evolutions"))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .settings(libraryDependencies ++= playDeps)
  .dependsOn(playSlick % "compile;test->test")
