package jumbo5337.carserivce.controller

import jumbo5337.carserivce.model.CompleteSessionRequest
import jumbo5337.carserivce.model.InitSessionRequest
import jumbo5337.carserivce.service.SessionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/session")
class SessionController(
    private val sessionService: SessionService
) {

    @PostMapping("/init")
    fun openSession(
        @RequestParam("customer") customerId: Long,
        @RequestBody() request: InitSessionRequest
    ): ResponseEntity<*> {
        request.validateRequest()
        return sessionService.initSession(customerId, request).let {
            ResponseEntity.ok(it.toOkResponse())
        }
    }

    @PostMapping("{sessionId}/complete")
    fun completeSession(
        @RequestParam("customer") customerId: Long,
        @PathVariable("sessionId") sessionId: UUID,
        @RequestBody() request: CompleteSessionRequest,
    ): ResponseEntity<*> {
        request.validateRequest()
        return sessionService.completeSession(customerId, sessionId, request).let {
            ResponseEntity.ok(it.toOkResponse())
        }
    }

}