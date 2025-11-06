package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.IntrospectDto
import com.kaiqkt.authentication.domain.utils.Constants
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val sessionService: SessionService,
    private val tokenService: TokenService,
) {
    private val log = LoggerFactory.getLogger(AuthorizationService::class.java)

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
            iat = claims.issueTime.time,
            roles = claims.getStringListClaim(Constants.Keys.ROLES),
            permissions = claims.getStringListClaim(Constants.Keys.PERMISSIONS),
        )
    }
}
