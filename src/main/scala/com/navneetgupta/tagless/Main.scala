package com.navneetgupta.tagless

import cats.effect._
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {
  // Using Cats effect

  val httpApp =
    Router("/calculator" -> SpotcapRoutes.routes(CalculatorInterpretor[IO])).orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
    .bindHttp(9000 , "localhost")
    .withHttpApp(httpApp)
    .serve.compile.drain.as(ExitCode.Success)
}
