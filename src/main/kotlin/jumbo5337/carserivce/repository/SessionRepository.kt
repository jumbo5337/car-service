package jumbo5337.carserivce.repository

import jumbo5337.carserivce.model.RFIDTag
import jumbo5337.carserivce.model.Session
import java.util.*

interface SessionRepository {

    fun findById(id: UUID) : Session?

    fun findOpenSession(rfidTag: RFIDTag) : Session?

    fun initSession(session: Session)

    fun completeSession(session: Session)
}