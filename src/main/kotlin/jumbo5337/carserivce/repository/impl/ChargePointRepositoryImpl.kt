package jumbo5337.carserivce.repository.impl

import jumbo5337.carserivce.model.ChargePoint
import jumbo5337.carserivce.repository.ChargePointRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.queryForObject
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import javax.sql.DataSource


@Repository
class ChargePointRepositoryImpl(
    dataSource: DataSource
) : ChargePointRepository {

    private val jdbcTemplate = JdbcTemplate(dataSource)

    override fun findById(cpId: Long): ChargePoint? {
        return kotlin.runCatching {
            jdbcTemplate.queryForObject(
                """SELECT id, cp_name, customer_id FROM CHARGE_POINTS WHERE id = ? """, cpId
            ) { rs, rowNum ->
                ChargePoint(
                    id = rs.getLong("id"),
                    name = rs.getString("cp_name"),
                    customerId = rs.getLong("customer_id")
                )
            }
        }.getOrNull()
    }

    override fun addConnector(cpId: Long): Long {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(
            PreparedStatementCreator {
                it.prepareStatement("""INSERT INTO CONNECTORS (cpid) VALUES ($cpId)""", arrayOf("id"))
            }, keyHolder
        )
        return keyHolder.key!!.toLong()
    }

}