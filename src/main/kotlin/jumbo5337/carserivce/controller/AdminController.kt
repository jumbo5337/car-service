package jumbo5337.carserivce.controller

import jumbo5337.carserivce.model.InitSessionRequest
import jumbo5337.carserivce.model.SessionsResponse
import jumbo5337.carserivce.service.AdminService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val adminService: AdminService,
) {

    @GetMapping("/sessions")
    fun getSessions(
        @RequestParam("from", required = false) from: LocalDateTime?,
        @RequestParam("to", required = false) to: LocalDateTime?
    ): ResponseEntity<*> = adminService.findSessions(from, to).let {
        ResponseEntity.ok(SessionsResponse(sessions = it))
    }

    @PutMapping("/connector")
    fun getSessions(
        @RequestParam("chargePoint", required = false) chargePointId: Long
    ): ResponseEntity<*> = adminService.addConnector(chargePointId).let {
        ResponseEntity.ok().body(it.toOkResponse())
    }
}