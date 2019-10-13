package com.navneetgupta.withzio

import com.navneetgupta.common.Models._
import zio.ZIO

trait Calculator {
  val rootCalculator : Calculator.CalculatorService[Any]
}

object Calculator {
  trait CalculatorService[R] {
    def findRoot(cashFlows:List[CashflowAmount], guess: Double): ZIO[R, Nothing, Either[String, Double]]
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

