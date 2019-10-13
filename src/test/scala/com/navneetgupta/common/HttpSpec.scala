package com.navneetgupta.common

import cats.effect.Sync
import cats.implicits._
import org.http4s._
import org.scalatest.{Assertion, FunSpec, OptionValues}

class HttpSpec extends FunSpec with OptionValues {
  protected def request[F[_]](method: Method, uri: String): Request[F] =
    Request(method = method, uri = Uri.fromString(uri).toOption.get)

  // https://http4s.org/v0.21/testing/
  protected def check[F[_], A](
                                actual:          F[Response[F]],
                                expectedStatus:  Status,
                                expectedBody:    Option[A]
                              )( implicit
                                 F: Sync[F],
                                 ev: EntityDecoder[F, A]
                              ): F[Unit] =
    for {
      actual       <- actual
      _            <- expectedBody.fold[F[Assertion]](
        actual.body.compile.toVector.map(s => assert(s.isEmpty)))(
        expected => actual.as[A].map(x => assert(x === expected, s"Body was $x instead of $expected.") )
      )
      _            <- F.delay(assert(actual.status == expectedStatus, s"Status was ${actual.status} instead of $expectedStatus."))
    } yield ()
}
