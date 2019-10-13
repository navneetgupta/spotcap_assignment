package com.navneetgupta.withzio

import com.navneetgupta.common.HttpSpec
import com.navneetgupta.common.Models._
import zio.{DefaultRuntime, ZIO}
import zio.console._
import org.http4s.implicits._
import org.http4s.{Status, _}
import zio.interop.catz._

import com.navneetgupta.withzio.{
  Calculator => ZCalculator,
  CalculatorLive => ZCalculatorLive
}

class SpotcapRoutesSpec extends HttpSpec {
  import SpotcapRoutesSpec._
  import SpotcapRoutesSpec.spotcapRoutes._

  val app = spotcapRoutes.routes.orNotFound

  describe("SpotcapRoutes") {
    it("should should say Spotcap!!!") {
      val req = request[SpotcapTask](Method.GET, "/")
      runWithEnv(
        check[SpotcapTask, String](app.run(req), Status.Ok, Some("Spotcap!!!")))

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
      val req = request[SpotcapTask](Method.POST, "/").withEntity(inputForm)
      runWithEnv(
        check[SpotcapTask, ResponseModel](
          app.run(req),
          Status.Ok,
          Some(SuccessModel(48.3, 0.033400878))))

    }

  }

}

object SpotcapRoutesSpec extends DefaultRuntime {

  val spotcapRoutes = SpotcapRoutes[ZCalculator]()

  val mkEnv =
    for {
      _ <- putStrLn("Starting testing")
      calc = ZCalculatorLive.rootCalculator
      env = new ZCalculator {
        override val rootCalculator: ZCalculator.CalculatorService[Any] = calc
      }
    } yield env

  def runWithEnv[E, A](task: ZIO[ZCalculator, E, A]): A =
    unsafeRun[E, A](mkEnv.flatMap(env => task.provide(env)))

}
