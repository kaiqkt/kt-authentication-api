package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.AuthenticationDto
import com.kaiqkt.authentication.domain.dtos.AuthorizationTokenDto
import com.kaiqkt.authentication.domain.dtos.IntrospectDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.enums.AuthenticationType
import com.kaiqkt.authentication.domain.models.enums.Scope
import com.kaiqkt.authentication.domain.utils.Constants
import io.azam.ulidj.ULID
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
        val sessionId = claims.getStringClaim(Constants.SID_KEY)
        val session = sessionService.findById(sessionId)

        return IntrospectDto(
            active = session != null,
            sid = sessionId,
            iss = claims.issuer,
            sub = claims.subject,
            scope = claims.getStringClaim(Constants.SCOPE_KEY),
            exp = claims.expirationTime.time,
            iat = claims.issueTime.time
        )
    }

    fun getTokens(tokenDto: AuthorizationTokenDto.Create): AuthenticationDto {
        return refreshToken(tokenDto)
    }

    fun login(email: String, password: String): AuthenticationDto {
        val user = userService.findByEmailAndType(email, AuthenticationType.PASSWORD)

        if (!passwordEncoder.matches(password, user.password)) {
            throw DomainException(ErrorType.INVALID_PASSWORD)
        }

        val sessionId = ULID.random()
        val tokens = tokenService.issueTokens(user.id, sessionId, listOf(Scope.USER))

        sessionService.save(sessionId, tokens.refreshToken, user)

        log.info("User ${user.id} authenticated ${user.id} successfully")

        return tokens
    }

    private fun refreshToken(tokenDto: AuthorizationTokenDto.Create): AuthenticationDto {
        if (tokenDto.refreshToken == null) {
            throw DomainException(ErrorType.INVALID_GRANT_TYPE_ARGUMENTS)
        }

        val session = sessionService.findByRefreshToken(tokenDto.refreshToken)
        val user = session.user

        val tokens = tokenService.issueTokens(user.id, session.id, listOf())

        sessionService.save(session.id, tokens.refreshToken, user)

        log.info("Authentication for session ${session.id} of user ${user.id} successfully")

        return tokens
    }
}