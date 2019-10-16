package com.navneetgupta.withzio

import com.navneetgupta.common.Models._
import zio.ZIO
import zio.blocking._

import scala.annotation.tailrec

trait CalculatorLive extends Calculator {
//  val simplePowerCalculator: SimplePowerCalculator.PowerCalculator[Any]

  override final val rootCalculator: Calculator.CalculatorService[Blocking] =  new Calculator.CalculatorService[Blocking] {
    private val ACCURACY = 1E-5
    private val MAX_ITTERATION = 25
    override def findRoot(cashflows: List[CashflowAmount], guess: Double): ZIO[Blocking, Throwable, Either[String, Double]] =
//      for {
//        fiber <- ().fork
//        resp <- fiber.join
//      } yield resp
      effectBlocking(newtonsMethod(guess, cashflows,0))

    // Initial guess can be improved TODO: Investigate optimal initial guess
    @tailrec
    def newtonsMethod(rateToTry: Double, cashflows: List[CashflowAmount], iterationCount: Int): Either[String,Double] = {
      if(iterationCount > MAX_ITTERATION)
        Left("Cann't find the approx root")
      else {
        // .foldLeft(fxValue, fxDerivativeValue, PowerCalculation === (1+x)^k == (1+x)*(1+x)^(k-1) )
        // Mainitining PowerCalculation to Avoid calling `Math.pow` every time.
        val (fx, fxderivative, _) = cashflows.zipWithIndex.foldLeft((0.0, 0.0, 1.0))((a, b) => {

          /**
           *
             ===========================================================
            |              c                                            |
            |   f(x) =  ------                                          |
            |           (1 + x)^k                                       |
            |                                                           |
            |                  -ck                    -k                |
            |   f`(x) =  --------------         =   ------ * f(x)       |
            |             (1 + x)^(k+1)             (1 + x)             |
            |                                                           |
            |     where k is denoted here by b._2 `timeperiod`          |
            |           c is denoted here by b._1 `cashflowamount`  |
            |           x is rateToTry(irr rate)                        |
            |                                                           |
             ===========================================================
           **/

          val fapplied = b._1 / a._3
          (a._1 + fapplied, a._2 -(b._2 / (1 + rateToTry)) * fapplied, a._3  * (1+rateToTry))
        })
        val nextRateToTry = rateToTry - fx / fxderivative
        if (Math.abs(nextRateToTry - rateToTry) <= ACCURACY) Right(nextRateToTry)
        else newtonsMethod(nextRateToTry, cashflows, iterationCount + 1)
      }
    }

  }
}

object CalculatorLive extends CalculatorLive

trait SimplePowerCalculatorLive extends SimplePowerCalculator {
  override val powerCalculator: SimplePowerCalculator.PowerCalculator[Any] = new SimplePowerCalculator.PowerCalculator[Any] {
    override def calculatePower(base: Principal, exponent: Principal): ZIO[Any, Nothing, Principal] =
      ZIO.effectTotal(Math.pow(base, exponent))
  }
}

object SimplePowerCalculatorLive extends SimplePowerCalculatorLive
