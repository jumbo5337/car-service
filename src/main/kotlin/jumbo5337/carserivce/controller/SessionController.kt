package jumbo5337.carserivce.controller

import jumbo5337.carserivce.model.AuthorizationException
import jumbo5337.carserivce.model.CompleteSessionRequest
import jumbo5337.carserivce.model.InitSessionRequest
import jumbo5337.carserivce.model.UserData
import jumbo5337.carserivce.service.SessionService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/session")
class SessionController(
    private val sessionService: SessionService
) {

    @PostMapping("/init")
    fun openSession(
        @RequestBody() request: InitSessionRequest,
        auth: Authentication
    ): ResponseEntity<*> {
        request.validateRequest()
        val customerId = (auth.principal as? UserData)?.customerId ?: throw AuthorizationException("Authorization failed")
        return sessionService.initSession(customerId, request).let {
            ResponseEntity.ok(it.toOkResponse())
        }
    }

    @PostMapping("{sessionId}/complete")
    fun completeSession(
        @PathVariable("sessionId") sessionId: UUID,
        @RequestBody() request: CompleteSessionRequest,
        auth: Authentication
    ): ResponseEntity<*> {
        request.validateRequest()
        val customerId = (auth.principal as? UserData)?.customerId ?: throw AuthorizationException("Authorization failed")
        return sessionService.completeSession(customerId, sessionId, request).let {
            ResponseEntity.ok(it.toOkResponse())
        }
    }

}