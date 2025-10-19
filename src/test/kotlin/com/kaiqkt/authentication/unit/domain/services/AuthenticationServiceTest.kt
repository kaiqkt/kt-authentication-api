package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.services.AuthenticationService
import com.kaiqkt.authentication.domain.services.ClientService
import com.kaiqkt.authentication.domain.services.SessionService
import com.kaiqkt.authentication.domain.services.TokenService
import com.kaiqkt.authentication.domain.services.UserService
import com.kaiqkt.authentication.unit.domain.dtos.AuthenticationDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.AuthorizationTokenDtoSampler
import com.kaiqkt.authentication.unit.domain.models.ClientSampler
import com.kaiqkt.authentication.unit.domain.models.SessionSampler
import com.kaiqkt.authentication.unit.domain.models.UserSampler
import com.nimbusds.jwt.JWTClaimsSet
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthenticationServiceTest {
    private val sessionService = mockk<SessionService>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val tokenService = mockk<TokenService>()
    private val userService = mockk<UserService>()
    private val clientService = mockk<ClientService>()
    private val authenticationService =
        AuthenticationService(sessionService, passwordEncoder, tokenService, userService, clientService)

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

    @Test
    fun `given a refresh token when exist a session attached should return a new pair of tokens`() {
        val tokenDto = AuthorizationTokenDtoSampler.sampleRefresh()

        every { sessionService.findByClientIdAndRefreshToken(any(),any()) } returns SessionSampler.sample()
        every { tokenService.issueTokens(any(), any(), any(),any(),any()) } returns AuthenticationDtoSampler.sample()
        every { sessionService.save(any(), any(), any(),any()) } returns SessionSampler.sample()

        authenticationService.getTokens(tokenDto)

        verify { sessionService.findByClientIdAndRefreshToken(any(), any()) }
        verify { tokenService.issueTokens(any(), any(), any(), any(), any()) }
        verify { sessionService.save(any(), any(), any(), any()) }
    }

    @Test
    fun `given a client id, email and password when user exist but password does not match should thrown an exception`() {
        val tokenDto = AuthorizationTokenDtoSampler.samplePassword()

        every { clientService.findById(any()) } returns ClientSampler.sample()
        every { userService.findByEmailAndType(any(), any()) } returns UserSampler.sample()
        every { passwordEncoder.matches(any(), any()) } returns false

        val exception = assertThrows<DomainException> {
            authenticationService.getTokens(tokenDto)
        }

        assertEquals(ErrorType.INVALID_CREDENTIALS, exception.type)

        verify { clientService.findById(any()) }
        verify { userService.findByEmailAndType(any(), any()) }
        verify { passwordEncoder.matches(any(), any()) }
    }

    @Test
    fun `given a email and password when user exist and password does match should authenticate successfully`() {
        val tokenDto = AuthorizationTokenDtoSampler.samplePassword()

        every { clientService.findById(any()) } returns ClientSampler.sample()
        every { userService.findByEmailAndType(any(), any()) } returns UserSampler.sample()
        every { passwordEncoder.matches(any(), any()) } returns true
        every { sessionService.save(any(), any(), any(), any()) } returns SessionSampler.sample()
        every { tokenService.issueTokens(any(), any(), any(), any(), any()) } returns AuthenticationDtoSampler.sample()

        authenticationService.getTokens(tokenDto)

        verify { clientService.findById(any()) }
        verify { userService.findByEmailAndType(any(), any()) }
        verify { passwordEncoder.matches(any(), any()) }
        verify { sessionService.save(any(), any(), any(), any()) }
        verify { tokenService.issueTokens(any(), any(), any(), any(), any()) }
    }

}