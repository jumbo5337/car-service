package jumbo5337.carserivce.model

import org.springframework.http.HttpStatus

open class ServiceException(
    open val code: Int,
    open val HttpCode: HttpStatus,
    override val message: String
) : RuntimeException(message)

open class ClientException(
    override val code: Int,
    override val message: String
) : ServiceException(code, HttpStatus.OK, message)

class BadRequestException(
    override val message: String
) : ServiceException(code = 400, HttpStatus.BAD_REQUEST, message)

class ConflictException(
    message: String
) : ClientException(code = 409, message)

class NotFoundException(
    message: String
) : ClientException(code = 404, message)

class AuthorizationException(
    message: String
) : ClientException(code = 403, message)