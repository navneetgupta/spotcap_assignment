package com.navneetgupta.free

import com.navneetgupta.common.Models.CashflowAmount

object FreeStyleProgram {
  import cats.free.Free

  sealed trait CalculatorAlg[A]
  case class FindRoot(cashFlows: List[CashflowAmount], guess: Double) extends CalculatorAlg[Either[String, Double]]

  type Calculator[A] =  Free[CalculatorAlg, A]

  def findRoot(cashFlows: List[CashflowAmount], guess: Double): Calculator[Either[String, Double]] =
    Free.liftF(FindRoot(cashFlows, guess))
}
