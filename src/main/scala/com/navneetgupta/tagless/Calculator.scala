package com.navneetgupta.tagless

import com.navneetgupta.common.Models._

trait Calculator[F[_]] {
  def findRoot(cashFlows: List[CashflowAmount], guess: Double): F[Either[String, Double]]
}

object Calculator {
  def apply[F[_]](implicit F: Calculator[F]): Calculator[F] = F
}