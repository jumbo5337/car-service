package jumbo5337.carserivce.controller

import jumbo5337.carserivce.model.InfoResponse
import jumbo5337.carserivce.model.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/")
class InfoController(
    @Value("\${car-service.app-version}")
    private val appVersion: String,
    @Value("\${car-service.schema-version}")
    private val schemaVersion: String
) {

    @GetMapping("/app-info")
    fun getInfo(): ResponseEntity<*> =
        ResponseEntity.ok(InfoResponse(appVersion = appVersion, schemaVersion = schemaVersion))
}