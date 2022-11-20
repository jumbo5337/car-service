package jumbo5337.carserivce.model

import java.time.Instant
import java.time.LocalDateTime
import java.util.*

interface Identified<T> {
    val id : T
}
data class User(
    val username: String,
)

data class Customer(
    override val id: Long,
    val name: String
) : Identified<Long>

data class ChargePoint(
    override val id: Long,
    val name: String,
    val customerId: Long,
) : Identified<Long>

data class Connector(
    override val id: Long,
    val chargePointId: Long
) : Identified<Long>

data class RFIDTag(
    override val id: Long,
    val name: String,
    val customerId: Long,
): Identified<Long>
data class Vehicle(
    val registrationPlate: String,
    val name: String
) : Identified<String> {
    override val id: String
        get() = name
}

data class Session(
    override val id: UUID,
    val startTime: LocalDateTime,
    val startMeter: Double,
    val endTime: LocalDateTime?,
    val endMeter: Double?,
    val isCompleted: Boolean,
    val isError: Boolean,
    val message: String?,
    val rfidTagId: Long,
    val connectorId: Long,
) : Identified<UUID>