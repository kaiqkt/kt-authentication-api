package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.AuthorizationDto
import com.kaiqkt.authentication.domain.dtos.TokensDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.enums.AuthenticationType
import io.azam.ulidj.ULID
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthorizationService(
    private val sessionService: SessionService,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val userService: UserService,
    private val clientService: ClientService,
) {
    private val log = LoggerFactory.getLogger(AuthorizationService::class.java)

    fun getTokens(authorizationDto: AuthorizationDto): TokensDto =
        when (authorizationDto) {
            is AuthorizationDto.Password -> login(authorizationDto)
            is AuthorizationDto.Refresh -> refresh(authorizationDto)
        }

    @Transactional
    private fun login(passwordDto: AuthorizationDto.Password): TokensDto {
        val client = clientService.findById(passwordDto.clientId)
        val user = userService.findByEmailAndType(passwordDto.email, AuthenticationType.PASSWORD)

        if (!passwordEncoder.matches(passwordDto.password, user.password)) {
            throw DomainException(ErrorType.INVALID_CREDENTIALS)
        }

        val sessionId = ULID.random()
        val tokens =
            tokenService.issueTokens(
                subject = user.id,
                audience = passwordDto.clientId,
                sid = sessionId,
                roles = user.roles,
                permissions = setOf(),
            )

        sessionService.save(sessionId, client, tokens.refreshToken, user)

        log.info("User ${user.id} authenticated")

        return tokens
    }

    private fun refresh(refreshDto: AuthorizationDto.Refresh): TokensDto {
        val session = sessionService.findByClientIdAndRefreshToken(refreshDto.clientId, refreshDto.refreshToken)
        val user = session.user

        val tokens =
            tokenService.issueTokens(
                subject = user.id,
                audience = session.client.id,
                sid = session.id,
                roles = user.roles,
                permissions = setOf(),
            )

        sessionService.save(session.id, session.client, tokens.refreshToken, user)

        log.info("Refresh authentication for session ${session.id} of user ${user.id}")

        return tokens
    }
}
