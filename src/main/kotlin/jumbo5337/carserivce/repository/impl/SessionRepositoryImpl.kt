package jumbo5337.carserivce.repository.impl

import jumbo5337.carserivce.model.*
import jumbo5337.carserivce.repository.SessionRepository
import jumbo5337.carserivce.repository.impl.SessionRepositoryImpl.Companion.toSessionData
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.sql.DataSource

@Repository
class SessionRepositoryImpl(
    dataSource: DataSource
) : SessionRepository {

    val jdbcTemplate = JdbcTemplate(dataSource)

    private val tableName = "CHARGE_SESSIONS"

    override fun findById(id: UUID): Session? {
        return kotlin.runCatching {
            jdbcTemplate.queryForObject(
                """
           SELECT id, start_time, start_meter, is_completed, is_error, rfid_tag_id, connector_id, end_meter, end_time, message
           FROM $tableName WHERE id = ?
       """, SessionMapper, id
            )
        }.getOrNull()
    }

    override fun findOpenSession(rfidTag: RFIDTag): Session? {
        return jdbcTemplate.queryForObject(
            """
           SELECT id, start_time, start_meter, is_completed, is_error, rfid_tag_id, connector_id, end_meter, end_time, message
           FROM $tableName WHERE current_rfid = ?
       """, SessionMapper, rfidTag.id
        )
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

    override fun selectSessionData(startDate: LocalDateTime?, endDate: LocalDateTime?): List<SessionData> {
        return jdbcTemplate.query(
            """
                SELECT s.id as s_id, s.start_time as s_start_time, s.start_meter as s_start_meter,
                s.end_time as s_end_time, s.end_meter as s_end_meter,
                s.is_completed as s_is_completed, s.is_error as s_is_error, s.message as s_message, 
                cust.id as cust_id, cust.customer_name as cust_customer_name, 
                rf.id as rf_id, rf.tag_name as rf_tag_name, v.reg_number as v_reg_number,
                v.vehicle_name as v_vehicle_name, cp.id as cp_id, 
                cp.cp_name as cp_cp_name, con.id as con_id 
                FROM CHARGE_SESSIONS s
                INNER JOIN RFID_TAGS rf on s.rfid_tag_id = rf.id
                INNER JOIN VEHICLES v on v.rfid = rf.id
                INNER JOIN CUSTOMERS cust on cust.id = rf.customer_id
                INNER JOIN CONNECTORS con on s.connector_id = con.id
                INNER JOIN CHARGE_POINTS cp on cp.id = con.id
                WHERE s.start_time >= ? AND (s.end_time IS NULL OR s.end_time <= ?)
                --
            """.trimIndent(),
            SessionDataMapper,
            startDate.let {
                if (it == null)
                    Timestamp.valueOf(LocalDateTime.of(1970,1,1,0,0))
                else
                    Timestamp.valueOf(it)
            },
            endDate.let {
                if (it == null)
                    Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC")).plusHours(1))
                else
                    Timestamp.valueOf(it)
            }
        )
    }

    companion object {
        object SessionMapper : RowMapper<Session> {
            override fun mapRow(rs: ResultSet, rowNum: Int): Session = rs.toSession()
        }

        private fun ResultSet.toSession(): Session =
            Session(
                id = this.getObject("id", UUID::class.java),
                startMeter = this.getDouble("start_meter"),
                startTime = this.getTimestamp("start_time").toLocalDateTime(),
                isError = this.getBoolean("is_error"),
                isCompleted = this.getBoolean("is_completed"),
                endMeter = this.getDouble("end_meter"),
                endTime = this.getTimestamp("end_time")?.toLocalDateTime(),
                rfidTagId = this.getLong("rfid_tag_id"),
                connectorId = this.getLong("connector_id"),
                message = this.getString("message")
            )

        object SessionDataMapper : RowMapper<SessionData> {
            override fun mapRow(rs: ResultSet, rowNum: Int): SessionData = rs.toSessionData()
        }

        private fun ResultSet.toSessionData(): SessionData =
            SessionData(
                id = getObject("s_id", UUID::class.java),
                startMeter = getDouble("s_start_meter"),
                startTime = getTimestamp("s_start_time").toLocalDateTime(),
                isError = getBoolean("s_is_error"),
                isCompleted = getBoolean("s_is_completed"),
                endMeter = getDouble("s_end_meter").let {
                    if (it == 0.0) null else it
                },
                endTime = getTimestamp("s_end_time")?.toLocalDateTime(),
                rfidTag = SessionData.RFID(
                    id = getLong("rf_id"),
                    name = getString("rf_tag_name"),
                ),
                vehicle = SessionData.Vehicle(
                    registrationPlate = getString("v_reg_number"),
                    name = getString("v_vehicle_name")
                ),
                connector = getLong("con_id"),
                chargePoint = SessionData.ChargePoint(
                    id = getLong("cp_id"),
                    name = getString("cp_cp_name")
                ),
                customer = Customer(
                    id = getLong("cust_id"),
                    name = getString("cust_customer_name")
                ),
                message = this.getString("s_message")
            )
    }


}