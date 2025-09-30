package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.UserDto
import com.kaiqkt.authentication.domain.dtos.toModel
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.User
import com.kaiqkt.authentication.domain.models.enums.AuthenticationType
import com.kaiqkt.authentication.domain.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    fun create(userDto: UserDto.Create): User {
        if (userRepository.existsByEmail(userDto.email)) {
            throw DomainException(ErrorType.EMAIL_IN_USE)
        }

        val user = userDto.toModel().apply {
            this.password = passwordEncoder.encode(userDto.password)
        }

        userRepository.save(user)

        log.info("User ${user.id} created successfully")

        return user
    }

    fun findByEmailAndType(email: String, authType: AuthenticationType): User {
        return userRepository.findByEmailAndAuthenticationType(email, authType)
            ?: throw DomainException(ErrorType.USER_NOT_FOUND)
    }
}