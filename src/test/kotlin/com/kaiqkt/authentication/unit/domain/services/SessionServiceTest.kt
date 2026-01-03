package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.repositories.SessionRepository
import com.kaiqkt.authentication.domain.services.SessionService
import com.kaiqkt.authentication.unit.domain.dtos.PageRequestDtoSampler
import com.kaiqkt.authentication.unit.domain.models.SessionSampler
import com.kaiqkt.authentication.unit.domain.models.UserSampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.Optional
import kotlin.test.assertEquals

class SessionServiceTest {
    private val sessionRepository = mockk<SessionRepository>()
    private val sessionTtl = 300L
    private val sessionService = SessionService(sessionRepository, sessionTtl)

    @Test
    fun `given a id, refresh token and a user should create a session`() {
        every { sessionRepository.save(any()) } returns SessionSampler.sample()

        sessionService.save(ULID.random(), "refreshToken", UserSampler.sample())

        verify { sessionRepository.save(any()) }
    }

    @Test
    fun `given a refresh token when exist a session should return successfully`() {
        every { sessionRepository.findByRefreshToken(any()) } returns SessionSampler.sample()

        sessionService.findByRefreshToken("refreshToken")

        verify { sessionRepository.findByRefreshToken(any()) }
    }

    @Test
    fun `given a refresh token when not exist a session should thrown a exception`() {
        every { sessionRepository.findByRefreshToken(any()) } returns null

        val exception =
            assertThrows<DomainException> {
                sessionService.findByRefreshToken("refreshToken")
            }

        verify { sessionRepository.findByRefreshToken(any()) }

        assertEquals(ErrorType.SESSION_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a session id when session exist should return it`() {
        every { sessionRepository.findById(any()) } returns Optional.of(SessionSampler.sample())

        val session = sessionService.findById(ULID.random())

        verify { sessionRepository.findById(any()) }

        assertNotNull(session)
    }

    @Test
    fun `given a session id when session not exist should return null`() {
        every { sessionRepository.findById(any()) } returns Optional.empty()

        val session = sessionService.findById(ULID.random())

        verify { sessionRepository.findById(any()) }

        assertNull(session)
    }

    @Test
    fun `given a id and a user id when session exist should revoke successfully`() {
        every { sessionRepository.findByIdAndUserId(any(), any()) } returns SessionSampler.sample()

        sessionService.revoke(ULID.random(), ULID.random())

        verify { sessionRepository.findByIdAndUserId(any(), any()) }
    }

    @Test
    fun `given a id and a user id when session does not exist should throw a domain exception`() {
        every { sessionRepository.findByIdAndUserId(any(), any()) } returns null

        val exception =
            assertThrows<DomainException> {
                sessionService.revoke(ULID.random(), ULID.random())
            }

        verify { sessionRepository.findByIdAndUserId(any(), any()) }

        assertEquals(ErrorType.SESSION_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a user id and a page request should return sessions paginated successfully`() {
        every { sessionRepository.findAll(any<Pageable>()) } returns PageImpl(listOf(SessionSampler.sample()))

        sessionService.findAllByUserId(ULID.random(), PageRequestDtoSampler.sample())

        verify { sessionRepository.findAll(any<Pageable>()) }
    }

    @Test
    fun `given a user id and a page request when has a invalid sort by field should thrown an exception`() {
        val exception =
            assertThrows<DomainException> {
                sessionService.findAllByUserId(ULID.random(), PageRequestDtoSampler.sample(sortBy = "invalid"))
            }

        assertEquals(ErrorType.INVALID_FIELD, exception.type)
    }
}
