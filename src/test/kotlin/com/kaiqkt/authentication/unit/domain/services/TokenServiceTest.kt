package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.services.TokenService
import io.azam.ulidj.ULID
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class TokenServiceTest {
    private val issuer = "kt-authentication-api"
    private val accessTokenTll = 300L
    private val accessTokenSecret = "k2p4/4gX5h0x/1B9Y9O2VJ6ZcfjWQmV5UQJ4cZP9YvE="
    private val tokenService = TokenService(issuer, accessTokenTll, accessTokenSecret)

    @Test
    fun `given a token should return JWTClaimsSet`() {
        val subjectId = ULID.random()
        val sidId = ULID.random()

        val tokens = tokenService.issueTokens(subjectId, sidId)

        val claims = tokenService.getClaims(tokens.accessToken)

        assertEquals(issuer, claims.issuer)
        assertEquals(subjectId, claims.subject)
        assertEquals(sidId, claims.getClaim("sid"))
    }

    @Test
    fun `given a token when signature is invalid thrown an DomainException`() {
        val tokens = tokenService.issueTokens(ULID.random(), ULID.random())

        tokenService::class.java.getDeclaredField("accessTokenSecret").apply {
            isAccessible = true
            set(tokenService, "tk2p4/4gX5h0x/1B9Y9O2VJ6ZcfjWQmV5UQJ4cZP9YvE==")
        }

        val exception = assertThrows<DomainException> {
            tokenService.getClaims(tokens.accessToken)
        }

        assertEquals(ErrorType.INVALID_TOKEN, exception.type)
    }

    @Test
    fun `given a token when is expired thrown an DomainException`() {
        tokenService::class.java.getDeclaredField("accessTokenTll").apply {
            isAccessible = true
            set(tokenService, 1L)
        }

        val tokens = tokenService.issueTokens(ULID.random(), ULID.random())

        Thread.sleep(1001)

        val exception = assertThrows<DomainException> {
            tokenService.getClaims(tokens.accessToken)
        }

        assertEquals(ErrorType.EXPIRED_TOKEN, exception.type)
    }

    @Test
    fun `given a token when is invalid thrown an DomainException`() {
        val exception = assertThrows<DomainException> {
            tokenService.getClaims("token")
        }

        assertEquals(ErrorType.INVALID_TOKEN, exception.type)
    }
}