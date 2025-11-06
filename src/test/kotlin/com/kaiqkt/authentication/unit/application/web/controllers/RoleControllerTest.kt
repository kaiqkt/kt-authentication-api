package com.kaiqkt.authentication.unit.application.web.controllers

import com.kaiqkt.authentication.application.web.controllers.RoleController
import com.kaiqkt.authentication.domain.services.RoleService
import com.kaiqkt.authentication.unit.application.web.requests.RoleRequestV1Sampler
import com.kaiqkt.authentication.unit.domain.models.RoleSampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class RoleControllerTest {
    private val roleService = mockk<RoleService>()
    private val roleController = RoleController(roleService)

    @Test
    fun `given a request should create a role successfully`() {
        every { roleService.create(any()) } returns RoleSampler.sample()

        val response = roleController.create(RoleRequestV1Sampler.sample())

        verify { roleService.create(any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a role id and a permission should associate successfully`() {
        justRun { roleService.associate(any(), any()) }

        val response = roleController.associate(ULID.random(), ULID.random())

        verify { roleService.associate(any(), any()) }

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `given a role id should delete successfully`() {
        justRun { roleService.delete(any()) }

        val response = roleController.delete(ULID.random())

        verify { roleService.delete(any()) }

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `given a request should return roles with pagination successfully`() {
        every { roleService.findAll(any()) } returns PageImpl(listOf(RoleSampler.sample()))

        val response = roleController.findAll(0, 0, "DESC", null)

        verify { roleService.findAll(any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a role id should return a role successfully`() {
        every { roleService.findById(any()) } returns RoleSampler.sample()

        val response = roleController.findById(ULID.random())

        verify { roleService.findById(any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
