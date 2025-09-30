package com.kaiqkt.authentication.unit.application.web.controllers

import com.kaiqkt.authentication.application.web.controllers.SessionController
import com.kaiqkt.authentication.domain.services.SessionService
import io.azam.ulidj.ULID
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class SessionControllerTest{
    private val sessionService = mockk<SessionService>()
    private val sessionController = SessionController(sessionService)

    @Test
    fun `given a request should revoke successfully`(){
        justRun { sessionService.revoke(any(), any()) }

        sessionController.revokeById(ULID.random(), ULID.random())

        verify { sessionService.revoke(any(), any()) }
    }
}