package jumbo5337.carserivce.repository

import jumbo5337.carserivce.model.RFIDTag

interface CustomerRepository {


    fun validateConnectorAndRfId(
        connectorId: Long,
        rfidTag: RFIDTag,
    ) : Boolean


}