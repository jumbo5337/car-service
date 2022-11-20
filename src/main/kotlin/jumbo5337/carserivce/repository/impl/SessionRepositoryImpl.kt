package jumbo5337.carserivce.repository.impl

import jumbo5337.carserivce.model.RFIDTag
import jumbo5337.carserivce.model.Session
import jumbo5337.carserivce.repository.SessionRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types
import java.util.*
import javax.sql.DataSource

@Repository
class SessionRepositoryImpl(
    dataSource: DataSource
) : SessionRepository {

    private val jdbcTemplate = JdbcTemplate(dataSource)

    private val tableName = "CHARGE_SESSIONS"

    override fun findById(id: UUID): Session? {
        return jdbcTemplate.queryForObject("""
           SELECT id, start_time, start_meter, is_completed, is_success, rfid_tag_id, connector_id, end_meter, end_time, message
           FROM $tableName WHERE id = ?
       """, SessionMapper, id)
    }

    override fun findOpenSession(rfidTag: RFIDTag): Session? {
        return jdbcTemplate.queryForObject("""
           SELECT id, start_time, start_meter, is_completed, is_success, rfid_tag_id, connector_id, end_meter, end_time, message
           FROM $tableName WHERE current_rfid = ?
       """, SessionMapper, rfidTag.id)
    }

    override fun initSession(session: Session) {
        jdbcTemplate.execute(
            """
                INSERT INTO $tableName (id, start_time, start_meter, is_completed, is_error, rfid_tag_id, connector_id)
                VALUES ( ?, ?, ?, ?, ?, ?, ? )
            """.trimIndent()
        ) {
            it.setObject(1, session.id)
            it.setTimestamp(2, Timestamp.valueOf(session.startTime))
            it.setDouble(3, session.startMeter)
            it.setBoolean(4, false)
            it.setBoolean(5, false)
            it.setLong(6, session.rfidTagId)
            it.setLong(7, session.connectorId)
            it.execute()
        }
    }

    override fun completeSession(session: Session) {
        jdbcTemplate.execute(
            """
            UPDATE $tableName 
            SET is_completed = ?, is_error = ?,
            end_time = ?, end_meter = ?,
            message = ?  
            WHERE id = ?
        """.trimIndent()
        ) {
            it.setBoolean(1, session.isCompleted)
            it.setBoolean(2, session.isError)
            if (session.endTime != null) {
                it.setTimestamp(3, Timestamp.valueOf(session.endTime))
            } else {
                it.setNull(3, Types.TIMESTAMP)
            }
            if (session.endMeter != null) {
                it.setDouble(4, session.endMeter)
            } else {
                it.setNull(4, Types.DOUBLE)
            }
            if (session.message != null) {
                it.setString(5, session.message)
            } else {
                it.setNull(5, Types.VARCHAR)
            }
            it.setObject(6, session.id)
            it.execute()
        }
    }

    companion object {
        object SessionMapper : RowMapper<Session> {
            override fun mapRow(rs: ResultSet, rowNum: Int): Session = rs.toSession()
        }

        private fun ResultSet.toSession() : Session =
            Session(
                id = this.getObject("id", UUID::class.java),
                startMeter = this.getDouble("start_meter"),
                startTime = this.getTimestamp("start_time").toLocalDateTime(),
                isError = this.getBoolean("is_success"),
                isCompleted = this.getBoolean("is_completed"),
                endMeter = this.getDouble("end_meter"),
                endTime = this.getTimestamp("end_time").toLocalDateTime(),
                rfidTagId = this.getLong("rfid_tag_id"),
                connectorId = this.getLong("connector_id"),
                message = this.getString("message")
            )
    }


}