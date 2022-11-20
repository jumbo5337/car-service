package jumbo5337.carserivce.repository

import jumbo5337.carserivce.model.RFIDTag

interface VehicleRepository {

    fun isAssigned(rfidTag: RFIDTag) : Boolean

}