package jumbo5337.carserivce.repository.impl

import jumbo5337.carserivce.model.RFIDTag
import jumbo5337.carserivce.repository.CustomerRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class CustomerRepositoryImpl(
    dataSource: DataSource
) : CustomerRepository {

    private val jdbcTemplate = JdbcTemplate(dataSource)

    override fun validateConnectorAndRfId(connectorId: Long, rfidTag: RFIDTag): Boolean {
        return jdbcTemplate.query(
            """
                WITH RFID_CHECK(cid) as (
                    SELECT c.id as cid FROM CUSTOMERS c
                    WHERE id = (select customer_id from RFID_TAGS where id = ?)
                    GROUP BY c.id
                )
                SELECT count(1) as count FROM CUSTOMERS c
                INNER JOIN CHARGE_POINTS cp on c.id = cp.customer_id
                WHERE cp.id = (select cpid from CONNECTORS where id = ?)
                AND c.id = (SELECT cid from RFID_CHECK limit 1)
            """.trimIndent(),
            PreparedStatementSetter {
                it.setLong(1, rfidTag.id)
                it.setLong(2, connectorId)
            },
            ResultSetExtractor {
                it.getLong("count") > 0
            }
        ) ?: false
    }
}