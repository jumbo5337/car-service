package jumbo5337.carserivce.service

import jumbo5337.carserivce.model.*
import jumbo5337.carserivce.repository.CustomerRepository
import jumbo5337.carserivce.repository.RFIDRepository
import jumbo5337.carserivce.repository.SessionRepository
import jumbo5337.carserivce.repository.VehicleRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class SessionService(
    private val rfidRepository: RFIDRepository,
    private val vehicleRepository: VehicleRepository,
    private val customerRepository: CustomerRepository,
    private val sessionRepository: SessionRepository,
) {


    fun initSession(
        rfidTagId: Long,
        connectorId: Long,
        customerId: Long,
        startMeter: Double,
        startTime: LocalDateTime,
    ): Response<Session> {
        val rfidTag = getRfid(rfidTagId)
        rfidTag.checkCustomer(customerId)
        rfidTag.checkVehicle()
        rfidTag.checkConnector(connectorId)
        val session = createSession(startMeter, startTime, connectorId, rfidTagId)
        return try {
            sessionRepository.initSession(session)
            Success(session)
        } catch (e: DuplicateKeyException) {
            val session = sessionRepository.findOpenSession(rfidTag)!!
            session.checkStartValues(session)
            Duplicate(session)
        }
    }

    fun completeSession(
        sessionId: UUID,
        rfidTagId: Long,
        connectorId: Long,
        customerId: Long,
        endMeter: Double,
        endTime: LocalDateTime,
    ): Response<Session> {
        val rfidTag = getRfid(rfidTagId)
        val session = getSession(sessionId)
        return try {
            rfidTag.checkCustomer(customerId)
            rfidTag.checkCustomer(customerId)
            rfidTag.checkConnector(connectorId)
            val closedSession = session.copy(
                isCompleted = true,
                isError = true,
                endTime = endTime,
                endMeter = endMeter
            )
            if (session.isCompleted && session.isError) {
                session.checkEndValues(closedSession)
                Duplicate(closedSession)
            } else {
                sessionRepository.completeSession(closedSession)
                Success(closedSession)
            }
        } catch (e: Exception) {
            if (!session.isCompleted) {
                sessionRepository.completeSession(session.copy(isError = true, message = e.message))
            }
            throw e
        }
    }


    private fun getSession(id: UUID): Session =
        sessionRepository.findById(id)
            ?: throw NotFoundException("Session=[$id]: not found")

    private fun getOpenSession(rfidTag: RFIDTag): Session =
        sessionRepository.findOpenSession(rfidTag)
            ?: throw NotFoundException("RFID=[${rfidTag.id}]: doesn't have open session")


    private fun getRfid(rfidTagId: Long): RFIDTag =
        rfidRepository.findById(rfidTagId)
            ?: throw NotFoundException("RFID=[$rfidTagId]: not found")

    private fun Session.checkStartValues(session: Session) {
        checkConnector(session.connectorId)
        if (this.startMeter != session.startMeter)
            throw ConflictException("RFID=[${session.rfidTagId}]: has another open session in this connector")
    }

    private fun Session.checkConnector(anotherConnectorId: Long) {
        if (this.connectorId != anotherConnectorId)
            throw ConflictException("RFID=[${this.rfidTagId}]: session is open in another connector")
    }

    private fun Session.checkEndValues(session: Session) {
        if (this.connectorId != session.connectorId)
            throw ConflictException("RFID=[${this.rfidTagId}]: session was open in another connector")
        if (this.endMeter != session.endMeter)
            throw ConflictException("RFID=[${session.rfidTagId}]: session ended with another meters")
    }

    private fun RFIDTag.checkCustomer(anotherCustomerId: Long) {
        if (this.customerId != anotherCustomerId)
            throw ConflictException("RFID=[$id]: is assigned to another customer")
    }

    private fun RFIDTag.checkVehicle() {
        if (vehicleRepository.isAssigned(this))
            throw ConflictException("RFID=[$id]: doesn't assigned to any vehicle")
    }

    private fun RFIDTag.checkConnector(connectorId: Long) {
        if (customerRepository.validateConnectorAndRfId(connectorId, this))
            throw ConflictException("RFID=[$id]: connector [$id] is assigned to another customer")
    }

    private fun createSession(
        startMeter: Double,
        startTime: LocalDateTime,
        connectorId: Long,
        rfidTagId: Long,
    ): Session = Session(
        id = UUID.randomUUID(),
        startMeter = startMeter,
        startTime = startTime,
        isError = false,
        isCompleted = false,
        endMeter = null,
        endTime = null,
        rfidTagId = connectorId,
        connectorId = rfidTagId,
        message = null
    )


}