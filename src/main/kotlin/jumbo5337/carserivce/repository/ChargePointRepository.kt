package jumbo5337.carserivce.repository

import jumbo5337.carserivce.model.ChargePoint

interface ChargePointRepository {


    fun findById(cpId: Long) : ChargePoint?

    fun addConnector(cpId: Long) : Long
}