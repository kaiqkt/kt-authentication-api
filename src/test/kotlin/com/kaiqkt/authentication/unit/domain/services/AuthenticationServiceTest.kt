package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.services.AuthenticationService
import com.kaiqkt.authentication.domain.services.SessionService
import com.kaiqkt.authentication.domain.services.TokenService
import com.kaiqkt.authentication.unit.domain.models.SessionSampler
import com.nimbusds.jwt.JWTClaimsSet
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthenticationServiceTest {

    private val sessionService = mockk<SessionService>()
    private val tokenService = mockk<TokenService>()

    private val authenticationService = AuthenticationService(sessionService, tokenService)

    @Test
    fun `given a introspection when session exists should return successfully with active true`() {
        val jwtClaimsSet = JWTClaimsSet
            .Builder()
            .claim("sid", ULID.random())
            .issuer("iss")
            .subject("sub")
            .claim("scope", "")
            .expirationTime(Date.from(Instant.now()))
            .issueTime(Date.from(Instant.now()))
            .build()

        every { tokenService.getClaims(any()) } returns jwtClaimsSet
        every { sessionService.findById(any()) } returns SessionSampler.sample()

        val introspectDto = authenticationService.introspect("token")

        verify { tokenService.getClaims(any()) }
        verify { sessionService.findById(any()) }

        assertTrue { introspectDto.active }
    }

    @Test
    fun `given a introspection when session not exists should return successfully with active false`() {
        val jwtClaimsSet = JWTClaimsSet
            .Builder()
            .claim("sid", ULID.random())
            .issuer("iss")
            .subject("sub")
            .claim("scope", "")
            .expirationTime(Date.from(Instant.now()))
            .issueTime(Date.from(Instant.now()))
            .build()

        every { tokenService.getClaims(any()) } returns jwtClaimsSet
        every { sessionService.findById(any()) } returns null

        val introspectDto = authenticationService.introspect("token")

        verify { tokenService.getClaims(any()) }
        verify { sessionService.findById(any()) }

        assertFalse { introspectDto.active }
    }
}