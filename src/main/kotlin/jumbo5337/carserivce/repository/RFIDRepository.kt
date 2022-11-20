package jumbo5337.carserivce.repository

import jumbo5337.carserivce.model.RFIDTag

interface RFIDRepository {

    fun findById(
        rfidTagId: Long
    ) : RFIDTag?


}