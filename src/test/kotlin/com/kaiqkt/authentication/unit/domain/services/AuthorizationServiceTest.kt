package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.services.AuthorizationService
import com.kaiqkt.authentication.domain.services.SessionService
import com.kaiqkt.authentication.domain.services.TokenService
import com.kaiqkt.authentication.domain.services.UserService
import com.kaiqkt.authentication.unit.domain.dtos.AuthenticationDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.AuthorizationTokenDtoSampler
import com.kaiqkt.authentication.unit.domain.models.SessionSampler
import com.kaiqkt.authentication.unit.domain.models.UserSampler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals

class AuthorizationServiceTest {
    private val sessionService = mockk<SessionService>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val tokenService = mockk<TokenService>()
    private val userService = mockk<UserService>()
    private val authorizationService =
        AuthorizationService(sessionService, passwordEncoder, tokenService, userService)

    @Test
    fun `given a refresh token when exist a session attached should return a new pair of tokens`() {
        val tokenDto = AuthorizationTokenDtoSampler.sampleRefresh()

        every { sessionService.findByRefreshToken(any()) } returns SessionSampler.sample()
        every { tokenService.issueTokens(any(), any(), any(), any()) } returns AuthenticationDtoSampler.sample()
        every { sessionService.save(any(), any(), any()) } returns SessionSampler.sample()

        authorizationService.getTokens(tokenDto)

        verify { sessionService.findByRefreshToken(any()) }
        verify { tokenService.issueTokens(any(), any(), any(), any()) }
        verify { sessionService.save(any(), any(), any()) }
    }

    @Test
    fun `given a email and password when user exist but password does not match should thrown an exception`() {
        val tokenDto = AuthorizationTokenDtoSampler.samplePassword()

        every { userService.findByEmailAndType(any(), any()) } returns UserSampler.sample()
        every { passwordEncoder.matches(any(), any()) } returns false

        val exception =
            assertThrows<DomainException> {
                authorizationService.getTokens(tokenDto)
            }

        assertEquals(ErrorType.INVALID_CREDENTIALS, exception.type)

        verify { userService.findByEmailAndType(any(), any()) }
        verify { passwordEncoder.matches(any(), any()) }
    }

    @Test
    fun `given a email and password when user exist and password does match should authenticate successfully`() {
        val tokenDto = AuthorizationTokenDtoSampler.samplePassword()

        every { userService.findByEmailAndType(any(), any()) } returns UserSampler.sample()
        every { passwordEncoder.matches(any(), any()) } returns true
        every { sessionService.save(any(), any(), any()) } returns SessionSampler.sample()
        every { tokenService.issueTokens(any(), any(), any(), any()) } returns AuthenticationDtoSampler.sample()

        authorizationService.getTokens(tokenDto)

        verify { userService.findByEmailAndType(any(), any()) }
        verify { passwordEncoder.matches(any(), any()) }
        verify { sessionService.save(any(), any(), any()) }
        verify { tokenService.issueTokens(any(), any(), any(), any()) }
    }
}
