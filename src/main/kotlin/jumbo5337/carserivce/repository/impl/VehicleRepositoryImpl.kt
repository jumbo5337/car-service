package jumbo5337.carserivce.repository.impl

import jumbo5337.carserivce.model.RFIDTag
import jumbo5337.carserivce.repository.VehicleRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class VehicleRepositoryImpl(
    dataSource: DataSource
) : VehicleRepository {

    private val jdbcTemplate = JdbcTemplate(dataSource)

    private val tableName = "VEHICLES"

    override fun isAssigned(rfidTag: RFIDTag): Boolean {
        return jdbcTemplate.query(
                """
                SELECT COUNT(1) as count FROM $tableName
                WHERE rfid = ?
            """.trimIndent(),
                PreparedStatementSetter {
                    it.setLong(1, rfidTag.id)
                },
                ResultSetExtractor {
                    if (it.next())
                        it.getInt(1) > 0
                    else
                        false
                }
            ) ?: false

    }
}