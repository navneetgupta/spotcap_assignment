package com.navneetgupta.free

import cats.effect._
import cats.implicits._
import org.http4s.server._
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {
  val httpApp =
    Router("/calculator" -> SpotcapRoutes.routes[IO](FreeStyleProgramInterptetor.calculatorInterpretor[IO])).orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(9000 , "localhost")
      .withHttpApp(httpApp)
      .serve.compile.drain.as(ExitCode.Success)
}
