package com.kaiqkt.authentication.unit.application.web.controllers

import com.kaiqkt.authentication.application.web.controllers.UserController
import com.kaiqkt.authentication.domain.services.UserService
import com.kaiqkt.authentication.unit.application.web.requests.UserRequestV1Sampler
import com.kaiqkt.authentication.unit.domain.models.UserSampler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class UserControllerTest {
    private val userService = mockk<UserService>()
    private val userController = UserController(userService)

    @Test
    fun `given a request should create a user successfully`() {
        val request = UserRequestV1Sampler.sample()

        every { userService.create(any()) } returns UserSampler.sample()

        val response = userController.create(request)

        verify { userService.create(any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }
}