package jumbo5337.carserivce.service

import jumbo5337.carserivce.model.*
import jumbo5337.carserivce.repository.ChargePointRepository
import jumbo5337.carserivce.repository.SessionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AdminService(
    private val sessionRepository: SessionRepository,
    private val chargePointRepository: ChargePointRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun findSessions(
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): List<SessionData> {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw BadRequestException("Start date is after End date")
        }
        logger.info("get sessions request from [$startDate] to [$endDate]")
        return sessionRepository.selectSessionData(startDate, endDate)
    }

    fun addConnector(
        chargePointId: Long
    ): Response<Connector> {
        val chargePoint = chargePointRepository.findById(chargePointId)
            ?: throw NotFoundException("ChargePoint=[$chargePointId]: not found")
        val connectorId = chargePointRepository.addConnector(chargePointId)
        logger.info("Charge Point=[$chargePoint]: add new connector with id [$connectorId]")
        return Success(Connector(connectorId, chargePointId))
    }
}