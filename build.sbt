
lazy val slick3Test = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .dependsOn(playSlick)
  .dependsOn(playSlickEvolutions)
  .settings(
    scalaVersion := "2.11.8",
    parallelExecution in Test := false,
    fork in Test := true,
    libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.8",
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.12",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.4.12",
    libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.4.12",
    libraryDependencies += "com.h2database" % "h2" % "1.4.193",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7",
    // https://www.playframework.com/documentation/2.5.x/PlaySlick
    libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.0-M2",
    libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0-M2",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.12" % Test,
    libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4.12" % Test,
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % Test,
    libraryDependencies += "org.typelevel" %% "scalaz-scalatest" % "1.1.1" % Test
  )

lazy val playSlick = (project in file("play-slick"))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    scalaVersion := "2.11.8",
    parallelExecution in Test := false,
    fork in Test := true,
    libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.0-M2",
    libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0-M2",
    libraryDependencies += "com.typesafe.play" %% "play-specs2" % "2.5.10",
    libraryDependencies += "com.typesafe.play" %% "play-jdbc-api" % "2.5.10",
    libraryDependencies += "com.typesafe.play" %% "play-jdbc-evolutions" % "2.5.10",
    libraryDependencies += "com.h2database" % "h2" % "1.4.193"
  )

lazy val playSlickEvolutions = (project in file("evolutions"))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
      scalaVersion := "2.11.8",
      parallelExecution in Test := false,
      fork in Test := true,
      libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.0-M2",
      libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0-M2",
      libraryDependencies += "com.typesafe.play" %% "play-specs2" % "2.5.10",
      libraryDependencies += "com.typesafe.play" %% "play-jdbc-api" % "2.5.10",
      libraryDependencies += "com.typesafe.play" %% "play-jdbc-evolutions" % "2.5.10",
      libraryDependencies += "com.h2database" % "h2" % "1.4.193"
  ).dependsOn(playSlick % "compile;test->test")
