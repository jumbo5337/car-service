package jumbo5337.carserivce.repository.impl

import jumbo5337.carserivce.model.AdminRole
import jumbo5337.carserivce.model.CustomerRole
import jumbo5337.carserivce.model.Session
import jumbo5337.carserivce.model.UserData
import jumbo5337.carserivce.repository.UserRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementSetter
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Repository
import java.util.*
import javax.sql.DataSource

@Repository
class UserRepositoryImpl(dataSource: DataSource) : UserRepository {


    private val jdbcTemplate = JdbcTemplate(dataSource)
    override fun findByUsername(username: String): UserData? {
        return jdbcTemplate.query(
            """
                SELECT username, password, customer_id, role as count FROM USERS
                WHERE username = ?
            """.trimIndent(),
            PreparedStatementSetter {
                it.setString(1, username)
            },
            ResultSetExtractor {
                if (it.next()) {
                    val role = when (it.getInt("role")) {
                        1 -> AdminRole
                        2 -> CustomerRole
                        else -> error("Unexpected role id [${it.getInt("role")}]")
                    }
                    val customerId = it.getLong("customer_id")
                    if (customerId == 0L && role == CustomerRole) {
                        error("Customer must have customerId")
                    }
                    UserData(
                        username = it.getString("username"),
                        password = it.getString("password"),
                        customerId = it.getLong("customer_id"),
                        role = role
                    )
                } else
                    null
            }
        )
    }

}