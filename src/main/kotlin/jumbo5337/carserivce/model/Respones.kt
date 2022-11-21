package jumbo5337.carserivce.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import java.time.ZoneId


sealed class Response<T : Identified<*>> {
    abstract val code: Int
    abstract val message: String
    abstract val entity: T

    fun toOkResponse() : OkResponse =
        OkResponse(code, message, entity.id)
}

data class Success<T : Identified<*>>(
    override val entity: T
) : Response<T>() {
    override val code: Int = 0
    override val message: String = "OK"
}

data class Duplicate<T : Identified<*>>(
    override val entity: T
) : Response<T>() {
    override val code: Int = 422
    override val message: String = "Entity [${entity.id}] already exists"
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OkResponse (
    val code: Int,
    val message: String,
    val id: Any?,
)

data class SessionsResponse(
    val code: Int = 0,
    val message: String = "OK",
    val sessions: List<SessionData>
)

data class InfoResponse(
    val code: Int = 0,
    val appVersion: String,
    val schemaVersion: String,
    val ts : LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)

data class ErrorResponse(
    val code: Int,
    val message: String
) {
    constructor(e: ServiceException) : this(code = e.code, message = e.message)
}