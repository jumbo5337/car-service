package jumbo5337.carserivce.configuration

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DatabaseConfig {


    @Bean(initMethod = "migrate")
    fun flyway(
        dataSource: DataSource,
        @Value("\${car-service.schema-verison}")
        version: String
    ): Flyway = Flyway.configure()
        .dataSource(dataSource)
        .baselineVersion(version)
        .load()

}