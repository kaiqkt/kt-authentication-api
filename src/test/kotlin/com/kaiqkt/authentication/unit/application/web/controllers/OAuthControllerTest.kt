package com.kaiqkt.authentication.unit.application.web.controllers

import com.kaiqkt.authentication.application.web.controllers.OAuthController
import com.kaiqkt.authentication.domain.services.AuthenticationService
import com.kaiqkt.authentication.domain.services.AuthorizationService
import com.kaiqkt.authentication.unit.application.web.requests.AuthenticationTokenRequestV1Sampler
import com.kaiqkt.authentication.unit.application.web.requests.AuthorizationCodeRequestV1Sampler
import com.kaiqkt.authentication.unit.domain.dtos.AuthenticationDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.IntrospectionDtoSampler
import com.kaiqkt.authentication.unit.domain.models.AuthorizationCodeSampler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class OAuthControllerTest {
    private val authorizationService = mockk<AuthorizationService>()
    private val authenticationService = mockk<AuthenticationService>()
    private val oAuthController = OAuthController(authorizationService, authenticationService)

    @Test
    fun `given a request should authorize successfully`(){
        val request = AuthorizationCodeRequestV1Sampler.sample()

        every { authorizationService.create(any()) } returns AuthorizationCodeSampler.sample()

        oAuthController.authorize(request)

        verify { authorizationService.create(any()) }
    }


    @Test
    fun `given a request should authenticate successfully`(){
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