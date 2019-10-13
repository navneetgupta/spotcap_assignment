package com.navneetgupta.tagless

import cats.effect.IO
import com.navneetgupta.common.HttpSpec
import com.navneetgupta.common.Models.{InputForm, ResponseModel, Schedule, SuccessModel, Value}
import io.circe.{Decoder, Encoder}
import org.http4s.implicits._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder, Method, Status}

class SpotcapRoutesSpec extends HttpSpec {
  import SpotcapRoutesSpec._
  import SpotcapRoutesSpec.routes._

  implicit val valueEncoder: Encoder[Value] = deriveEncoder
  implicit val valueDecoder: Decoder[Value] = deriveDecoder
  implicit val schedulesEncoder: Encoder[Schedule] = deriveEncoder
  implicit val schedulesDecoder: Decoder[Schedule] = deriveDecoder
  implicit val inputFormEncoder: Encoder[InputForm] = deriveEncoder
  implicit val inputFormDecoder: Decoder[InputForm] = deriveDecoder
  implicit val responseEncoder: Encoder[ResponseModel] = deriveEncoder
  implicit val responseDecoder: Decoder[ResponseModel] = deriveDecoder

  implicit val reqDecoder: EntityDecoder[IO, ResponseModel] = jsonOf
  implicit val resEncoder: EntityEncoder[IO, InputForm] = jsonEncoderOf

  val app = SpotcapRoutesSpec.routes.orNotFound

  describe("SpotcapRoutes With CatsEffect") {
    it("should should say Spotcap!!!") {
      val req = request[IO](Method.GET, "/")
      runIO(
        check[IO, String](app.run(req), Status.Ok, Some("Spotcap!!!")))

    }

    it("should calculate APR and IPR correctly") {
      val inputForm = InputForm(
        51020400,
        List(
          Schedule(1, 3595000, 1530600),
          Schedule(2, 3702800, 1422800),
          Schedule(3, 3813900, 1311700),
          Schedule(4, 3928300, 1197300),
          Schedule(5, 4046200, 1079400),
          Schedule(6, 4167600, 958000),
          Schedule(7, 4292600, 833000),
          Schedule(8, 4421400, 704200),
          Schedule(9, 4554000, 571600),
          Schedule(10, 4690600, 435000),
          Schedule(11, 4831400, 294200),
          Schedule(12, 4976600, 149300)
        ),
        Some(Value(1020400))
      )
      val req = request[IO](Method.POST, "/").withEntity(inputForm)
      runIO(
        check[IO, ResponseModel](
          app.run(req),
          Status.Ok,
          Some(SuccessModel(48.3, 0.033400878))))

    }

  }
}

object SpotcapRoutesSpec {
  val routes = SpotcapRoutes.routes(CalculatorInterpretor[IO])

  def runIO[A](task:IO[A]) = task.unsafeRunSync()
}