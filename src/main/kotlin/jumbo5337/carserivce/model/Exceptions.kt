package jumbo5337.carserivce.model


open class ServiceException(
    code: Int,
    message: String
) : RuntimeException(message)


class ConflictException(
    message: String
) : ServiceException(code = 409, message)

class NotFoundException(
    message: String
) : ServiceException(code = 404, message)

class AuthorizationException(
    message: String
) : ServiceException(code = 403, message)