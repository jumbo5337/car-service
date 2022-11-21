package jumbo5337.carserivce

import jumbo5337.carserivce.model.InitSessionRequest
import jumbo5337.carserivce.repository.SessionRepository
import jumbo5337.carserivce.repository.impl.SessionRepositoryImpl
import jumbo5337.carserivce.service.SessionService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CarSerivceApplication

fun main(args: Array<String>) {
    runApplication<CarSerivceApplication>(*args)
}
