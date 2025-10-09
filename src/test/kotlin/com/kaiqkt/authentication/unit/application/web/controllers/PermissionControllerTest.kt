package com.kaiqkt.authentication.unit.application.web.controllers

import com.kaiqkt.authentication.application.web.controllers.PermissionController
import com.kaiqkt.authentication.domain.services.PermissionService
import com.kaiqkt.authentication.unit.application.web.requests.PermissionRequestV1Sampler
import com.kaiqkt.authentication.unit.domain.models.PermissionSampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class PermissionControllerTest {
    private val permissionService = mockk<PermissionService>()
    private val permissionController = PermissionController(permissionService)

    @Test
    fun `given a request should find all permissions paginated successfully`() {
        every { permissionService.findAll(any()) } returns PageImpl(listOf(PermissionSampler.sample()))

        permissionController.findAll(0, 0, "ASC", null, null)

        verify { permissionService.findAll(any()) }
    }

    @Test
    fun `given a request to find all a permissions by resource id with pagination when found should return successfully`() {
        every {
            permissionService.findAllByResourceServerId(
                any(),
                any()
            )
        } returns PageImpl(listOf(PermissionSampler.sample()))

        val response = permissionController.findAll( 0, 0, "ASC", null, ULID.random())

        verify { permissionService.findAllByResourceServerId(any(), any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a resource id and a permission should create successfully`() {
        every { permissionService.create(any(), any()) } returns PermissionSampler.sample()

        val response = permissionController.create(ULID.random(), PermissionRequestV1Sampler.sample())

        verify { permissionService.create(any(), any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a request should delete a permission successfully`(){
        justRun { permissionService.delete(any()) }

        permissionController.delete(ULID.random())

        verify { permissionService.delete(any()) }
    }
}