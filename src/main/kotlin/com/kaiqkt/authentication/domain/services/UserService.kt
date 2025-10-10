package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.UserDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.User
import com.kaiqkt.authentication.domain.models.enums.AuthenticationType
import com.kaiqkt.authentication.domain.repositories.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val roleService: RoleService
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    fun create(userDto: UserDto.Create): User {
        if (userRepository.existsByEmail(userDto.email)) {
            throw DomainException(ErrorType.EMAIL_ALREADY_IN_USE)
        }

        val user = User(
            email =  userDto.email,
            password = passwordEncoder.encode(userDto.password)
        )

        userRepository.save(user)

        log.info("User ${user.id} created")

        return user
    }

    @Transactional
    fun assignRole(userId: String, roleId: String){
        val role = roleService.findById(roleId)
        val user = findById(userId)

        user.roles.add(role)

        log.info("User $userId assigned to role $roleId")
    }

    fun findByEmailAndType(email: String, authType: AuthenticationType): User {
        return userRepository.findByEmailAndAuthenticationType(email, authType)
            ?: throw DomainException(ErrorType.USER_NOT_FOUND)
    }

    fun findById(id: String): User {
        return userRepository.findById(id).getOrNull()
            ?: throw DomainException(ErrorType.USER_NOT_FOUND)
    }
}