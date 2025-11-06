package com.kaiqkt.authentication.unit.application.web.controllers

import com.kaiqkt.authentication.application.web.controllers.ResourceServerController
import com.kaiqkt.authentication.domain.services.ResourceServerService
import com.kaiqkt.authentication.unit.application.web.requests.ResourceServerRequestV1Sampler
import com.kaiqkt.authentication.unit.domain.models.ResourceServerSampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class ResourceServerControllerTest {
    private val resourceServerService = mockk<ResourceServerService>()
    private val controller = ResourceServerController(resourceServerService)

    @Test
    fun `given a resource server should create successfully`() {
        every { resourceServerService.create(any()) } returns ResourceServerSampler.sample()

        val response = controller.create(ResourceServerRequestV1Sampler.sample())

        every { resourceServerService.create(any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a request to delete a resource server should delete successfully`() {
        justRun { resourceServerService.delete(any()) }

        val response = controller.delete(ULID.random())

        verify { resourceServerService.delete(any()) }

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `given a request to find all a resource server with pagination when found should return successfully`() {
        every { resourceServerService.findAll(any()) } returns PageImpl(listOf(ResourceServerSampler.sample()))

        val response = controller.findAll(0, 0, "ASC", null)

        verify { resourceServerService.findAll(any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a request to find a resource server should return successfully`() {
        every { resourceServerService.findById(any()) } returns ResourceServerSampler.sample()

        controller.findById(ULID.random())

        verify { resourceServerService.findById(any()) }
    }
}
