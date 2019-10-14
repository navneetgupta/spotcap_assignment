package com.navneetgupta.free

import cats.arrow.FunctionK
import cats.effect.Effect
import cats.implicits._
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import com.navneetgupta.common.Models._
import com.navneetgupta.free.FreeStyleProgram.CalculatorAlg
import io.circe.generic.semiauto._

class SpotcapRoutes[F[_]:Effect] extends Http4sDsl[F] {
  implicit val valueEncoder: Encoder[Value] = deriveEncoder
  implicit val valueDecoder: Decoder[Value] = deriveDecoder
  implicit val schedulesEncoder: Encoder[Schedule] = deriveEncoder
  implicit val schedulesDecoder: Decoder[Schedule] = deriveDecoder
  implicit val inputFormEncoder: Encoder[InputForm] = deriveEncoder
  implicit val inputFormDecoder: Decoder[InputForm] = deriveDecoder
  implicit val responseEncoder: Encoder[ResponseModel] = deriveEncoder
  implicit val responseDecoder: Decoder[ResponseModel] = deriveDecoder

  implicit val reqDecoder: EntityDecoder[F, InputForm] = jsonOf
  implicit val resEncoder: EntityEncoder[F, ResponseModel] = jsonEncoderOf

  def calculatorEndPoint(interpretor: FunctionK[CalculatorAlg, F]) = HttpRoutes.of[F] {
    case GET -> Root => Ok("Spotcap!!!")
    case req @ POST -> Root =>
      req.decode[InputForm] { inputForm =>
        val a = FreeStyleProgram.findRoot(inputForm.asCalculatorModel.cashflows, 0.00).foldMap(interpretor)//.flatMap(x => msg => Ok(ResponseModel(msg)), a => Ok(ResponseModel(a)))
        a.flatMap(x => x.fold(msg => Ok(ResponseModel(msg)), a => Ok(ResponseModel(a))))
      }
  }
}

object SpotcapRoutes {
  def routes[F[_]: Effect](interpretor: FunctionK[CalculatorAlg, F]): HttpRoutes[F] = new SpotcapRoutes[F].calculatorEndPoint(interpretor)
}