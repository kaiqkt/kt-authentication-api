package com.kaiqkt.authentication.unit.application.web.controllers

import com.kaiqkt.authentication.application.web.controllers.AuthController
import com.kaiqkt.authentication.domain.services.AuthenticationService
import com.kaiqkt.authentication.domain.services.AuthorizationService
import com.kaiqkt.authentication.unit.application.web.requests.AuthenticationTokenRequestV1Sampler
import com.kaiqkt.authentication.unit.domain.dtos.AuthenticationDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.IntrospectionDtoSampler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class AuthControllerTest {
    private val authorizationService = mockk<AuthorizationService>()
    private val authenticationService = mockk<AuthenticationService>()
    private val authController = AuthController(authorizationService, authenticationService)

    @Test
    fun `given a request should issue and return the tokens successfully`() {
        val request = AuthenticationTokenRequestV1Sampler.sample()

        every { authorizationService.getTokens(any()) } returns AuthenticationDtoSampler.sample()

        authController.token(request)

        verify { authorizationService.getTokens(any()) }
    }

    @Test
    fun `given a request should introspect successfully`() {
        every { authenticationService.introspect(any()) } returns IntrospectionDtoSampler.sample()

        authController.introspect("access-token")

        verify { authenticationService.introspect(any()) }
    }
}
