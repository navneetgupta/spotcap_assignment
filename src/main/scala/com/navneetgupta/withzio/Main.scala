package com.navneetgupta.withzio

import cats.effect._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import zio.{App, RIO, ZEnv, ZIO}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console._
import zio.interop.catz._

object Main extends App {

  // Using Environmental Effects Pattern


  type AppEnvironment = Clock with Blocking with Calculator
  type AppTask[A]     = RIO[AppEnvironment, A]

  val httpApp       = Router[AppTask](
    "/calculator" -> SpotcapRoutes().routes
  ).orNotFound

  val server = ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
    BlazeServerBuilder[AppTask]
      .bindHttp(9000, "localhost")
      .withHttpApp(httpApp)
      .serve
      .compile[AppTask, AppTask, ExitCode]
      .drain
  }

  override def run(args: List[String]) =
    (for {
      program <- server.provideSome[ZEnv] { base =>
        new Clock with Blocking with CalculatorLive {
          override val clock: Clock.Service[Any] = base.clock
          override val blocking: Blocking.Service[Any] = base.blocking
        }
      }
    } yield program).foldM(err => putStrLn(s"Execution failed with: $err") *> ZIO.succeed(1), _ => ZIO.succeed(0))
}