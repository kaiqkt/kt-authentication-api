package com.kaiqkt.authentication.unit.application.web.controllers

import com.kaiqkt.authentication.application.web.controllers.SessionController
import com.kaiqkt.authentication.domain.services.SessionService
import com.kaiqkt.authentication.unit.domain.models.SessionSampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionControllerTest{
    private val sessionService = mockk<SessionService>()
    private val sessionController = SessionController(sessionService)

    @Test
    fun `given a request should revoke successfully`(){
        justRun { sessionService.revoke(any(), any()) }

        val response = sessionController.revokeById(ULID.random(), ULID.random())

        verify { sessionService.revoke(any(), any()) }

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `given a user id and parameters should return sessions paginated successfully`(){
        every { sessionService.findAllByUserId(any(), any()) } returns PageImpl(listOf(SessionSampler.sample()))

        val response = sessionController.findAllByUserId(ULID.random(), 0, 0, "ASC", null)

        verify { sessionService.findAllByUserId(any(), any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }
}