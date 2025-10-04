package com.kaiqkt.authentication.unit.application.web.controllers

import com.kaiqkt.authentication.application.web.controllers.OAuthController
import com.kaiqkt.authentication.domain.services.AuthenticationService
import com.kaiqkt.authentication.unit.application.web.requests.AuthenticationTokenRequestV1Sampler
import com.kaiqkt.authentication.unit.application.web.requests.LoginRequestV1Sampler
import com.kaiqkt.authentication.unit.domain.dtos.AuthenticationDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.IntrospectionDtoSampler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class OAuthControllerTest {
    private val authenticationService = mockk<AuthenticationService>()
    private val oAuthController = OAuthController(authenticationService)

    @Test
    fun `given a request should authenticate successfully`(){
        val request = LoginRequestV1Sampler.sample()

        every { authenticationService.login(any(), any()) } returns AuthenticationDtoSampler.sample()

        oAuthController.login(request)

        verify { authenticationService.login(any(), any()) }
    }


    @Test
    fun `given a request should issue and return the tokens successfully`(){
        val request = AuthenticationTokenRequestV1Sampler.sample()

        every { authenticationService.getTokens(any()) } returns AuthenticationDtoSampler.sample()

        oAuthController.token(request)

        verify { authenticationService.getTokens(any()) }
    }

    @Test
    fun `given a request should introspected successfully`(){
        every { authenticationService.introspect(any()) } returns IntrospectionDtoSampler.sample()

        oAuthController.introspect("access-token")

        verify { authenticationService.introspect(any()) }
    }
}