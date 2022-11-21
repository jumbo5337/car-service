package jumbo5337.carserivce.controller

import jumbo5337.carserivce.model.ErrorResponse
import jumbo5337.carserivce.model.ClientException
import jumbo5337.carserivce.model.ServiceException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ServerWebInputException

@ControllerAdvice(
    assignableTypes = [
        SessionController::class,
        AdminController::class
    ]
)
class BaseControllerAdvice {

    @ExceptionHandler(value = [ServiceException::class])
    fun handle(e: ServiceException): ResponseEntity<*> =
        ResponseEntity.status(e.HttpCode).body(ErrorResponse(e))

    @ExceptionHandler(value = [ServerWebInputException::class])
    fun handle(e: ServerWebInputException): ResponseEntity<*> = ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse(code = 400, e.message))

}

