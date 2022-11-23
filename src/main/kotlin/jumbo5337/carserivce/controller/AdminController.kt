package jumbo5337.carserivce.controller

import jumbo5337.carserivce.model.BadRequestException
import jumbo5337.carserivce.model.SessionsResponse
import jumbo5337.carserivce.service.AdminService
import jumbo5337.carserivce.service.DateTimeParser
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val adminService: AdminService,
) {

    @GetMapping("/sessions")
    fun getSessions(
        @RequestParam("from", required = false) from: String?,
        @RequestParam("to", required = false) to: String?,
        auth: Authentication?,
    ): ResponseEntity<*> {
        val fromDate = from?.let {
            parserLocalDate(it)
        }
        val toDate = to?.let {
            parserLocalDate(it)
        }
       return adminService.findSessions(fromDate, toDate).let {
            ResponseEntity.ok(SessionsResponse(sessions = it))
        }
    }

    @PutMapping("/connector")
    fun getSessions(
        @RequestParam("chargePoint", required = false) chargePointId: Long
    ): ResponseEntity<*> = adminService.addConnector(chargePointId).let {
        ResponseEntity.ok().body(it.toOkResponse())
    }


    private fun parserLocalDate(string: String) : LocalDateTime {
        return try {
            DateTimeParser.parse(string)
        } catch (e: Exception) {
            throw BadRequestException("Failed to parse date $string")
        }
    }
}