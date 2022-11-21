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
        customerId: Long,
        request: InitSessionRequest,
    ): Response<Session> {
        val rfidTag = getRfid(request.rfidNumber)
        rfidTag.checkCustomer(customerId)
        rfidTag.checkVehicle()
        rfidTag.checkConnector(request.connector)
        val session = createSession(request)
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
        customerId: Long,
        sessionId: UUID,
        request: CompleteSessionRequest
    ): Response<Session> {
        val rfidTag = getRfid(request.rfidNumber)
        val session = getSession(sessionId)
        return try {
            session.validateEndValues(request)
            rfidTag.checkCustomer(customerId)
            rfidTag.checkCustomer(customerId)
            rfidTag.checkConnector(request.connector)
            val closedSession = session.complete(request)
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

    private fun Session.validateEndValues(request: CompleteSessionRequest) {
        if (this.startMeter > request.endMeter)
            throw ConflictException("RFID=[${this.rfidTagId}]: session end meter is lower than start meter")
        if (this.startTime > request.endTime)
            throw ConflictException("RFID=[${this.rfidTagId}]: session end time is earlier than start time")
    }

    private fun Session.checkEndValues(session: Session) {
        if (this.connectorId != session.connectorId)
            throw ConflictException("RFID=[${this.rfidTagId}]: session was open in another connector")
        if (this.endMeter != session.endMeter)
            throw ConflictException("RFID=[${session.rfidTagId}]: session ended with another meters")
    }

    private fun RFIDTag.checkCustomer(anotherCustomerId: Long) {
        if (this.customerId != anotherCustomerId)
            throw AuthorizationException("RFID=[$id]: is assigned to another customer")
    }

    private fun RFIDTag.checkVehicle() {
        if (!vehicleRepository.isAssigned(this))
            throw ConflictException("RFID=[$id]: doesn't assigned to any vehicle")
    }

    private fun RFIDTag.checkConnector(connectorId: Long) {
        if (!customerRepository.validateConnectorAndRfId(connectorId, this))
            throw AuthorizationException("RFID=[$id]: connector [$id] is assigned to another customer")
    }

    private fun Session.complete(
        request: CompleteSessionRequest
    ) : Session = this.copy(
        isCompleted = true,
        isError = true,
        endTime = request.endTime,
        endMeter = request.endMeter
    )

    private fun createSession(
        request: InitSessionRequest
    ): Session = Session(
        id = UUID.randomUUID(),
        startMeter = request.startMeter,
        startTime = request.startTime,
        isError = false,
        isCompleted = false,
        endMeter = null,
        endTime = null,
        rfidTagId = request.rfidNumber,
        connectorId = request.connector,
        message = null
    )


}