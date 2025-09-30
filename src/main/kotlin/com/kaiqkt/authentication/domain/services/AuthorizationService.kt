package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.AuthorizationCodeDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.AuthorizationCode
import com.kaiqkt.authentication.domain.models.enums.AuthenticationType
import com.kaiqkt.authentication.domain.repositories.AuthorizationCodeRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class AuthorizationService(
    private val authorizationRepository: AuthorizationCodeRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserService,
    @param:Value("\${authorization.code-ttl}")
    private val codeTtl: Long
) {
    private val log = LoggerFactory.getLogger(AuthorizationService::class.java)

    fun create(codeDto: AuthorizationCodeDto.Create): AuthorizationCode {
        val user = userService.findByEmailAndType(codeDto.email, AuthenticationType.PASSWORD)

        if (!passwordEncoder.matches(codeDto.password, user.password)) {
            throw DomainException(ErrorType.INVALID_PASSWORD)
        }

        val authorizationCode = AuthorizationCode(
            code = generateCode(),
            redirectUri = codeDto.redirectUri,
            codeChallenge = codeDto.codeChallenge,
            user = user,
            expireAt = LocalDateTime.now().plusSeconds(codeTtl)
        )

        authorizationRepository.save(authorizationCode)

        log.info("Authorization code created for user ${user.id}")

        return authorizationCode
    }

    fun findByCodeAndRedirectUri(code: String, redirectUri: String): AuthorizationCode {
        return authorizationRepository.findByCode(code, redirectUri)
            ?: throw DomainException(ErrorType.AUTHORIZATION_CODE_NOT_FOUND)
    }

    @Transactional
    fun deleteByCode(code: String) {
        authorizationRepository.deleteByCode(code)
    }

    private fun generateCode(): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(UUID.randomUUID().toString().toByteArray())
}
