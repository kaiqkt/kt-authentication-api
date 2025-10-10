package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.AuthenticationDto
import com.kaiqkt.authentication.domain.dtos.AuthorizationTokenDto
import com.kaiqkt.authentication.domain.dtos.IntrospectDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.Role
import com.kaiqkt.authentication.domain.models.enums.AuthenticationType
import com.kaiqkt.authentication.domain.utils.Constants
import io.azam.ulidj.ULID
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val sessionService: SessionService,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(AuthenticationService::class.java)

    fun introspect(token: String): IntrospectDto {
        val claims = tokenService.getClaims(token)
        val sessionId = claims.getStringClaim(Constants.Keys.SID)
        val session = sessionService.findById(sessionId)

        return IntrospectDto(
            active = session != null,
            sid = sessionId,
            iss = claims.issuer,
            sub = claims.subject,
            exp = claims.expirationTime.time,
            iat = claims.issueTime.time
        )
    }

    fun getTokens(tokenDto: AuthorizationTokenDto): AuthenticationDto {
       return when(tokenDto) {
            is AuthorizationTokenDto.Password -> login(tokenDto)
            is AuthorizationTokenDto.Refresh -> refreshToken(tokenDto)
        }
    }

    @Transactional
    private fun login(tokenDto: AuthorizationTokenDto.Password): AuthenticationDto {
        val user = userService.findByEmailAndType(tokenDto.email, AuthenticationType.PASSWORD)
        val roles = user.roles

        if (!passwordEncoder.matches(tokenDto.password, user.password)) {
            throw DomainException(ErrorType.INVALID_CREDENTIALS)
        }

        val sessionId = ULID.random()
        val tokens = tokenService.issueTokens(user.id, sessionId, roles)

        sessionService.save(sessionId, tokens.refreshToken, user)

        log.info("User ${user.id} authenticated")

        return tokens
    }

    private fun refreshToken(tokenDto: AuthorizationTokenDto.Refresh): AuthenticationDto {
        val session = sessionService.findByRefreshToken(tokenDto.refreshToken)
        val user = session.user
        val roles = user.roles

        val tokens = tokenService.issueTokens(user.id, session.id, roles)

        sessionService.save(session.id, tokens.refreshToken, user)

        log.info("Refresh authentication for session ${session.id} of user ${user.id}")

        return tokens
    }
}