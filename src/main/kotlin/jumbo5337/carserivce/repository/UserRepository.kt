package jumbo5337.carserivce.repository

import jumbo5337.carserivce.model.UserData

interface UserRepository {

    fun findByUsername(username: String) : UserData?
}