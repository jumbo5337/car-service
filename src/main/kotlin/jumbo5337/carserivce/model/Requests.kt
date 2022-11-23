package jumbo5337.carserivce.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jumbo5337.carserivce.service.DateTimeParser
import java.time.LocalDateTime
import java.time.ZoneId

sealed class Request {

    abstract fun validateRequest()
}

data class InitSessionRequest(
    val connector: Long,
    val rfidNumber: Long,
    val startMeter: Double,
    @field:JsonDeserialize(using = DateTimeDeserializer::class)
    val startTime: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
) : Request (){
    override fun validateRequest() {
        if (connector < 0)
            throw BadRequestException("Connector should be positive")
        if (rfidNumber < 0)
            throw BadRequestException("RFID number should be positive")
        if (startMeter < 0)
            throw BadRequestException("Meter value should be positive")
    }
}

data class CompleteSessionRequest(
    val connector: Long,
    val rfidNumber: Long,
    val endMeter: Double,
    @field:JsonDeserialize(using = DateTimeDeserializer::class)
    val endTime: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
) : Request (){

    override fun validateRequest() {
        if (connector < 0)
            throw BadRequestException("Connector should be positive")
        if (rfidNumber < 0)
            throw BadRequestException("RFID number should be positive")
        if (endMeter < 0)
            throw BadRequestException("Meter value should be positive")
    }
}

class DateTimeDeserializer : JsonDeserializer<LocalDateTime>() {
    override fun deserialize(p0: JsonParser, p1: DeserializationContext?): LocalDateTime {
        return DateTimeParser.parse(p0.text)
    }
}