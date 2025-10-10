package com.kaiqkt.authentication.unit.application.web.controllers

import com.kaiqkt.authentication.application.web.controllers.PolicyController
import com.kaiqkt.authentication.domain.services.PolicyService
import com.kaiqkt.authentication.unit.application.web.requests.PolicyRequestV1Sampler
import com.kaiqkt.authentication.unit.domain.models.PolicySampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class PolicyControllerTest {
    private val policyService = mockk<PolicyService>()
    private val policyController = PolicyController(policyService)

    @Test
    fun `given a request should create a policy successfully`(){
        every { policyService.create(any(), any()) } returns PolicySampler.sample()

        val response = policyController.create(ULID.random(), PolicyRequestV1Sampler.sample())

        verify { policyService.create(any(), any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a resource server id should return all policies successfully`(){
        every { policyService.findAllByResourceId(any()) } returns listOf()

        policyController.findAllByResourceServerId(ULID.random())

        verify { policyService.findAllByResourceId(any()) }
    }

    @Test
    fun `given a policy id should delete successfully`(){
        justRun { policyService.delete(any()) }

        val response = policyController.delete(ULID.random())

        verify { policyService.delete(any()) }

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `given a request should return policies with pagination successfully`(){
        every { policyService.findAll(any(), any()) } returns PageImpl(listOf(PolicySampler.sample()))

        val response = policyController.findAll(0, 20, "DESC", null, ULID.random())

        verify {  policyService.findAll(any(), any()) }

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `given a policy id and a permission id should associate successfully`(){
        justRun { policyService.associatePermission(any(), any()) }

        val response = policyController.associatePermission(ULID.random(), ULID.random())

        verify { policyService.associatePermission(any(), any()) }

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun `given a policy id and a role id should associate successfully`(){
        justRun { policyService.associateRole(any(), any()) }

        val response = policyController.associateRole(ULID.random(), ULID.random())

        verify { policyService.associateRole(any(), any()) }

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }
}