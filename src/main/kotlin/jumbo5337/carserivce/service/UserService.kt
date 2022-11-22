package jumbo5337.carserivce.service

import jumbo5337.carserivce.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsPasswordService
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails? {
        val result = userRepository.findByUsername(username)
       return result
    }

}