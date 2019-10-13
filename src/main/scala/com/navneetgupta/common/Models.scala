package com.navneetgupta.common

object Models {
   final case class Schedule(
                        id: Int,
                        principal: Double,
                        interestFee: Double)

   final case class Value(value: Double = 0.0)

  type CashflowAmount = Double
  type Principal = Double

  final case class CalculatorModel(
                               principal: Principal,
                               upfrontFee: Option[Value] = None,
                               cashflows: List[CashflowAmount])

  final case class InputForm(
                              principal: Double,
                              schedule: List[Schedule],
                              upfrontFee: Option[Value] = None,
                              upfrontCreditlineFee: Option[Value] = None
                              ) {
    require(principal > 0, "principal must be Greater than equal to zero")
    require(schedule.size > 0, "schedule must have atleast one element")
    def asCalculatorModel: CalculatorModel = {
      val negativeCashFlow = principal-upfrontFee.getOrElse(Value(value= 0.0)).value-upfrontCreditlineFee.getOrElse(Value(value= 0.0)).value
      CalculatorModel(principal, upfrontFee,cashflows= (-negativeCashFlow) :: schedule.map(sc => sc.principal+sc.interestFee))
    }
  }
  sealed trait ResponseModel
  final case class SuccessModel( apr: Double, irr: Double, success: Boolean = true) extends ResponseModel
  final case class FailureModel(msg: String,success: Boolean = false) extends ResponseModel

  object ResponseModel {
    def apply(irr: Double): ResponseModel = {
      val irrApprox = Math.round(irr * 1000000000)/1000000000.0
      // APR calculated in terms of years, IRR could be  calculated as part of month pr any period describing
      // the period in which the majority of the problem is defined (e.g., using months if most of the cash flows
      // occur at monthly intervals) and converted to a yearly period thereafter.

      val apr =  (Math.pow(1 + irrApprox, 12d) - 1) * 100
      val aprRoundedToSingleDecimalPlace = Math.round(apr * 10)/10.0
      SuccessModel(aprRoundedToSingleDecimalPlace, irrApprox, true)
    }

    def apply(msg:String): ResponseModel = FailureModel(msg)
  }
}
