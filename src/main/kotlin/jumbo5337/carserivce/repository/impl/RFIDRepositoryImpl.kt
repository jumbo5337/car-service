package jumbo5337.carserivce.repository.impl

import jumbo5337.carserivce.model.RFIDTag
import jumbo5337.carserivce.model.Session
import jumbo5337.carserivce.repository.RFIDRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

@Repository
class RFIDRepositoryImpl(
    dataSource: DataSource
) : RFIDRepository {

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val tableName = "RFID_TAGS"

    override fun findById(rfidTagId: Long): RFIDTag? {
        return kotlin.runCatching {
            jdbcTemplate.queryForObject(
                """
           SELECT id, tag_name, customer_id
           FROM $tableName WHERE id = ?
       """, RFIDTagMapper, rfidTagId
            )
        }.getOrNull()
    }


    companion object {

        object RFIDTagMapper : RowMapper<RFIDTag> {
            override fun mapRow(rs: ResultSet, rowNum: Int): RFIDTag = rs.toRFIDTag()
        }

        private fun ResultSet.toRFIDTag(): RFIDTag =
            RFIDTag(
                id = getLong("id"),
                name = getString("tag_name"),
                customerId = getLong("customer_id")
            )
    }
}