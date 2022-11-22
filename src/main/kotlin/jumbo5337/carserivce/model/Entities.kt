package jumbo5337.carserivce.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.util.*

interface Identified<T> {
    val id: T
}


data class Customer(
    override val id: Long,
    val name: String
) : Identified<Long>

data class ChargePoint(
    override val id: Long,
    val name: String,
    val customerId: Long,
) : Identified<Long>

data class Connector(
    override val id: Long,
    val chargePointId: Long
) : Identified<Long>

data class RFIDTag(
    override val id: Long,
    val name: String,
    val customerId: Long,
) : Identified<Long>

data class Session(
    override val id: UUID,
    val startTime: LocalDateTime,
    val startMeter: Double,
    val endTime: LocalDateTime?,
    val endMeter: Double?,
    val isCompleted: Boolean,
    val isError: Boolean,
    val message: String?,
    val rfidTagId: Long,
    val connectorId: Long,
) : Identified<UUID>


@JsonInclude(JsonInclude.Include.NON_NULL)
data class SessionData(
    override val id: UUID,
    val startTime: LocalDateTime,
    val startMeter: Double,
    val endTime: LocalDateTime?,
    val endMeter: Double?,
    val isCompleted: Boolean,
    val isError: Boolean,
    val message: String?,
    val rfidTag: RFID,
    val connector: Long,
    val chargePoint: ChargePoint,
    val customer: Customer,
    val vehicle: Vehicle,
) : Identified<UUID> {

    data class RFID(
        val id: Long,
        val name: String
    )

    data class ChargePoint(
        val id: Long,
        val name: String
    )

    data class Vehicle(
        val registrationPlate: String,
        val name: String
    )
}

data class UserData(
    private val username: String,
    private val password: String,
    val role: GrantedAuthority,
    val customerId: Long?
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf(role)

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}

object AdminRole : GrantedAuthority {

    override fun getAuthority(): String = "ADMIN"
}

object CustomerRole : GrantedAuthority {
    override fun getAuthority(): String = "CUSTOMER"
}

