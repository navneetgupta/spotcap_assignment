package com.navneetgupta.withzio

import io.circe.{Decoder, Encoder}
import zio.{RIO, ZIO}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import com.navneetgupta.common.Models._
import io.circe.generic.semiauto._

final case class SpotcapRoutes[R <: Calculator]() {

  type SpotcapTask[A] = RIO[R, A]
  implicit val valueEncoder: Encoder[Value] = deriveEncoder
  implicit val valueDecoder: Decoder[Value] = deriveDecoder
  implicit val schedulesEncoder: Encoder[Schedule] = deriveEncoder
  implicit val schedulesDecoder: Decoder[Schedule] = deriveDecoder
  implicit val inputFormEncoder: Encoder[InputForm] = deriveEncoder
  implicit val inputFormDecoder: Decoder[InputForm] = deriveDecoder
  implicit val responseEncoder: Encoder[ResponseModel] = deriveEncoder
  implicit val responseDecoder: Decoder[ResponseModel] = deriveDecoder

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[SpotcapTask, A] = jsonOf[SpotcapTask, A]
  implicit def circeJsonEncoder[A](implicit encoder: Encoder[A]): EntityEncoder[SpotcapTask, A] =
    jsonEncoderOf[SpotcapTask, A]

  def findRoot(cashflows: List[CashflowAmount], guess: Double): ZIO[Calculator, Nothing, Either[String, Double]] =
    ZIO.accessM(_.rootCalculator.findRoot(cashflows, guess))

  def calculatePower(base: Principal, exponent: Principal): ZIO[SimplePowerCalculator, Nothing, Principal] =
    ZIO.accessM(_.powerCalculator.calculatePower(base, exponent))

  val dsl: Http4sDsl[SpotcapTask] = Http4sDsl[SpotcapTask]
  import dsl._

  def routes: HttpRoutes[SpotcapTask] = {
    HttpRoutes.of[SpotcapTask] {
      case GET -> Root => Ok("Spotcap!!!")
      case req @ POST -> Root =>
        req.decode[InputForm] { inputForm =>
          // Initial guess can be improved TODO: Investigate optimal initial guess
          findRoot(inputForm.asCalculatorModel.cashflows, 0.01).flatMap(x => x.fold(msg => Ok(ResponseModel(msg)), a => Ok(ResponseModel(a))))
        }
    }
  }
}
