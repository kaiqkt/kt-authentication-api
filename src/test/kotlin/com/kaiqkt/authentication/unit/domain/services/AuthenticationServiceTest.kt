package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.dtos.enums.GrantType
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.services.AuthenticationService
import com.kaiqkt.authentication.domain.services.AuthorizationService
import com.kaiqkt.authentication.domain.services.SessionService
import com.kaiqkt.authentication.domain.services.TokenService
import com.kaiqkt.authentication.domain.utils.Constants
import com.kaiqkt.authentication.unit.domain.dtos.AuthenticationDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.AuthorizationTokenDtoSampler
import com.kaiqkt.authentication.unit.domain.models.AuthorizationCodeSampler
import com.kaiqkt.authentication.unit.domain.models.SessionSampler
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.security.MessageDigest
import java.util.*
import kotlin.test.assertEquals

class AuthenticationServiceTest {
    private val authorizationService = mockk<AuthorizationService>()
    private val sessionService = mockk<SessionService>()
    private val tokenService = mockk<TokenService>()
    private val authenticationService = AuthenticationService(authorizationService, sessionService, tokenService)

    @Test
    fun `given a authorization code when is valid should return a pair of tokens`() {
        val codeVerifierEncrypted = MessageDigest.getInstance(Constants.SHA_256).digest("code-verifier".toByteArray())
        val codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifierEncrypted)
        val tokenDto = AuthorizationTokenDtoSampler.sampleCreate(grantType = GrantType.AUTHORIZATION_CODE)
        val authorizationCode = AuthorizationCodeSampler.sample(codeChallenge)

        every { authorizationService.findByCodeAndRedirectUri(any(), any()) } returns authorizationCode
        justRun { authorizationService.deleteByCode(any()) }
        every { sessionService.save(any(), any(), any()) } returns SessionSampler.sample()
        every { tokenService.issueTokens(any(), any(), any()) } returns AuthenticationDtoSampler.sample()

        authenticationService.getTokens(tokenDto)

        verify { authorizationService.findByCodeAndRedirectUri(any(), any()) }
        verify { authorizationService.deleteByCode(any()) }
        verify { sessionService.save(any(), any(), any()) }
        verify { tokenService.issueTokens(any(), any(), any()) }
    }

    @Test
    fun `given a authorization code when is code null should thrown an exception`() {
        val tokenDto = AuthorizationTokenDtoSampler.sampleCreate(
            code = null,
            grantType = GrantType.AUTHORIZATION_CODE
        )

        val exception = assertThrows<DomainException> {
            authenticationService.getTokens(tokenDto)
        }

        assertEquals(ErrorType.INVALID_GRANT_TYPE_ARGUMENTS, exception.type)
    }

    @Test
    fun `given a authorization code when is code verifier null should thrown an exception`() {
        val tokenDto = AuthorizationTokenDtoSampler.sampleCreate(
            codeVerifier = null,
            grantType = GrantType.AUTHORIZATION_CODE
        )

        val exception = assertThrows<DomainException> {
            authenticationService.getTokens(tokenDto)
        }

        assertEquals(ErrorType.INVALID_GRANT_TYPE_ARGUMENTS, exception.type)
    }

    @Test
    fun `given a authorization code when code challenge is invalid should thrown a exception`() {
        val tokenDto = AuthorizationTokenDtoSampler.sampleCreate(grantType = GrantType.AUTHORIZATION_CODE)
        val authorizationCode = AuthorizationCodeSampler.sample()

        every { authorizationService.findByCodeAndRedirectUri(any(), any()) } returns authorizationCode

        val exception = assertThrows<DomainException> {
            authenticationService.getTokens(tokenDto)
        }

        verify { authorizationService.findByCodeAndRedirectUri(any(), any()) }

        assertEquals(ErrorType.INVALID_CODE_CHALLENGE, exception.type)
    }

    @Test
    fun `given a refresh token when exist a session attached should return a new pair of tokens`() {
        val tokenDto = AuthorizationTokenDtoSampler.sampleCreate()

        every { sessionService.findByRefreshToken(any()) } returns SessionSampler.sample()
        every { tokenService.issueTokens(any(), any(), any()) } returns AuthenticationDtoSampler.sample()
        every { sessionService.save(any(), any(), any()) } returns SessionSampler.sample()

        authenticationService.getTokens(tokenDto)

        verify { sessionService.findByRefreshToken(any()) }
        verify { tokenService.issueTokens(any(), any(), any()) }
        verify { sessionService.save(any(), any(), any()) }
    }

    @Test
    fun `given a refresh token when is invalid should thrown an exception`() {
        val tokenDto = AuthorizationTokenDtoSampler.sampleCreate(
            refreshToken = null
        )

        val exception = assertThrows<DomainException> {
            authenticationService.getTokens(tokenDto)
        }

        assertEquals(ErrorType.INVALID_GRANT_TYPE_ARGUMENTS, exception.type)
    }
}