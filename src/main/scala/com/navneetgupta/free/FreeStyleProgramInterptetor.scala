package com.navneetgupta.free

import cats.effect.Effect
import com.navneetgupta.free.FreeStyleProgram.{CalculatorAlg, FindRoot}

import scala.annotation.tailrec

object FreeStyleProgramInterptetor {
  private val ACCURACY = 1E-5
  private val MAX_ITTERATION = 25

  import cats.implicits._
  import cats.~>

  def calculatorInterpretor[F[_]: Effect] = new (CalculatorAlg ~> F) {
    override def apply[A](fa: CalculatorAlg[A]): F[A] = fa match {
      case FindRoot(cashFlows, guess) => newtonsMethod(guess, cashFlows, 0).pure[F]
    }
  }

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
