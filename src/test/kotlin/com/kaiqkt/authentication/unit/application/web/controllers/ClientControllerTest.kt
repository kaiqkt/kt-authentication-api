package com.kaiqkt.authentication.unit.application.web.controllers

import com.kaiqkt.authentication.application.web.controllers.ClientController
import com.kaiqkt.authentication.domain.services.ClientService
import com.kaiqkt.authentication.unit.application.web.requests.ClientRequestV1Sampler
import com.kaiqkt.authentication.unit.domain.models.ClientSampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class ClientControllerTest {
    private val clientService = mockk<ClientService>()
    private val controller = ClientController(clientService)

    @Test
    fun `given a client should create successfully`() {
        every { clientService.create(any()) } returns ClientSampler.sample()

        val response = controller.create(ClientRequestV1Sampler.sample())

        verify { clientService.create(any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a request to delete a client should delete successfully`() {
        justRun { clientService.delete(any()) }

        val response = controller.delete(ULID.random().toString())

        verify { clientService.delete(any()) }

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `given a request to find all a client with pagination when found should return successfully`() {
        every { clientService.findAll(any()) } returns PageImpl(listOf(ClientSampler.sample()))

        val response = controller.findAll(0, 20, "DESC", null)

        verify { clientService.findAll(any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a request to find a client should return successfully`() {
        every { clientService.findById(any()) } returns ClientSampler.sample()

        val response = controller.findById(ULID.random().toString())

        verify { clientService.findById(any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
