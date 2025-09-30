package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.AuthenticationDto
import com.kaiqkt.authentication.domain.dtos.AuthorizationTokenDto
import com.kaiqkt.authentication.domain.dtos.IntrospectionDto
import com.kaiqkt.authentication.domain.dtos.enums.GrantType
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.utils.Constants
import com.kaiqkt.authentication.domain.utils.EncryptHelper
import io.azam.ulidj.ULID
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val authorizationService: AuthorizationService,
    private val sessionService: SessionService,
    private val tokenService: TokenService
) {
    private val log = LoggerFactory.getLogger(AuthenticationService::class.java)

    fun introspect(token: String): IntrospectionDto {
        val claims = tokenService.getClaims(token)
        val sessionId = claims.getStringClaim(Constants.SID_KEY)
        val session = sessionService.findById(sessionId)

        return IntrospectionDto(
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
        return when (tokenDto.grantType) {
            GrantType.AUTHORIZATION_CODE -> authorizationCode(tokenDto)
            GrantType.REFRESH_TOKEN -> refreshToken(tokenDto)
        }
    }

    @Transactional
    private fun authorizationCode(tokenDto: AuthorizationTokenDto.Create): AuthenticationDto {
        if (tokenDto.code == null || tokenDto.codeVerifier ==  null) {
            throw DomainException(ErrorType.INVALID_GRANT_TYPE_ARGUMENTS)
        }

        val authorizationCode = authorizationService.findByCodeAndRedirectUri(tokenDto.code, tokenDto.redirectUri)
        val user = authorizationCode.user
        val derived = EncryptHelper.encrypt(tokenDto.codeVerifier)

        if (derived != authorizationCode.codeChallenge) {
            throw DomainException(ErrorType.INVALID_CODE_CHALLENGE)
        }

        authorizationService.deleteByCode(tokenDto.code)

        val sessionId = ULID.random()
        val tokens = tokenService.issueTokens(user.id, sessionId, listOf())

        sessionService.save(sessionId, tokens.refreshToken, user)

        log.info("Authenticated ${user.id} successfully")

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