package com.navneetgupta.tagless

import cats.Applicative
import cats.implicits._
import com.navneetgupta.common.Models._

import scala.annotation.tailrec

class CalculatorInterpretor[F[_]: Applicative] extends Calculator[F] {

  private val ACCURACY = 1E-5
  private val MAX_ITTERATION = 25

  override def findRoot(cashFlows: List[CashflowAmount], guess: Double): F[Either[String, Double]] =
    (newtonsMethod(guess, cashFlows, 0)).pure[F]

  // Initial guess can be improved TODO: Investigate optimal initial guess
  @tailrec
  private def newtonsMethod(rateToTry: Double, cashflows: List[CashflowAmount], iterationCount: Int): Either[String,Double] = {
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

object CalculatorInterpretor {
  def apply[F[_]: Applicative](): CalculatorInterpretor[F] = new CalculatorInterpretor()
}