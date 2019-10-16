package com.navneetgupta.withzio

import com.navneetgupta.common.Models._
import zio.ZIO
import zio.blocking.Blocking

trait Calculator {
  val rootCalculator : Calculator.CalculatorService[Blocking]
}

object Calculator {
  trait CalculatorService[R] {
    def findRoot(cashFlows:List[CashflowAmount], guess: Double): ZIO[R, Throwable, Either[String, Double]]
  }
}

trait SimplePowerCalculator {
  val powerCalculator: SimplePowerCalculator.PowerCalculator[Any]
}

object SimplePowerCalculator {
  trait PowerCalculator[R] {
    def calculatePower(base: Double, exponent: Double): ZIO[R, Nothing, Double]
  }
}

