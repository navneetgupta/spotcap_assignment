
ThisBuild / scalaVersion     := "2.12.4"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.navneetgupta"

lazy val root = (project in file("."))
  .settings(
    name := "spotcap",
    libraryDependencies ++= {
      val Http4sVersion = "0.21.0-M5"
      val CirceVersion = "0.12.2"
      Seq(
        "org.scalatest" 		% "scalatest_2.12" 					% "3.0.5" % "test",
        "org.typelevel" 		%% "cats-core" 							% "2.0.0-RC1",
        "org.typelevel" 		%% "cats-free" 							% "2.0.0-RC1",
        "org.typelevel" 		%% "cats-effect" 							% "2.0.0-RC2",
        "dev.zio"						%% "zio" 										% "1.0.0-RC17",
        "org.http4s"        %% "http4s-blaze-server" 		% Http4sVersion,
        "org.http4s"        %% "http4s-circe"        		% Http4sVersion,
        "org.http4s"        %% "http4s-dsl"          		% Http4sVersion,
        "io.circe"          %% "circe-core"          		% CirceVersion,
        "io.circe"          %% "circe-generic"       		% CirceVersion,
        "io.circe"          %% "circe-parser"       		% CirceVersion,
        "io.circe" 					%% "circe-literal" 					% CirceVersion,
        "dev.zio"        		%% "zio-interop-cats"    		% "2.0.0.0-RC8",
        compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
      )
    }
  )
