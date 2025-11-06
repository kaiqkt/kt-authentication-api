package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.repositories.ClientRepository
import com.kaiqkt.authentication.domain.services.ClientService
import com.kaiqkt.authentication.domain.services.PolicyService
import com.kaiqkt.authentication.domain.services.TokenService
import com.kaiqkt.authentication.unit.domain.dtos.ClientDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.PageRequestDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.PolicySampler
import com.kaiqkt.authentication.unit.domain.models.ClientSampler
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import java.util.Optional
import kotlin.test.assertEquals

class ClientServiceTest {
    private val clientRepository = mockk<ClientRepository>()
    private val policyService = mockk<PolicyService>()
    private val tokenService = mockk<TokenService>()
    private val clientService = ClientService(clientRepository, policyService, tokenService)

    @Test
    fun `given a client to create, when policy is not found, should throw a domain exception`() {
        val clientDto = ClientDtoSampler.sample()

        every { policyService.findAllById(any()) } returns emptyList()

        val exception =
            assertThrows<DomainException> {
                clientService.create(clientDto)
            }

        assertEquals(ErrorType.POLICY_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a client to create, should create successfully and return a client`() {
        val clientDto = ClientDtoSampler.sample()
        val policy = PolicySampler.sample()
        val client = ClientSampler.sample()

        every { policyService.findAllById(any()) } returns listOf(policy)
        every { tokenService.opaqueToken() } returns "secret"
        every { clientRepository.save(any()) } returns client

        val result = clientService.create(clientDto)

        verify(exactly = 1) { clientRepository.save(any()) }

        assertEquals(client.id, result.id)
    }

    @Test
    fun `given a client id to delete, should delete successfully`() {
        val clientId = "1"

        every { clientRepository.deleteById(any()) } just runs

        clientService.delete(clientId)

        verify { clientRepository.deleteById(clientId) }
    }

    @Test
    fun `given a client id, when not found, should throw a domain exception`() {
        val clientId = "1"

        every { clientRepository.findById(any()) } returns Optional.empty()

        val exception =
            assertThrows<DomainException> {
                clientService.findById(clientId)
            }

        assertEquals(ErrorType.CLIENT_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a client id, when found, should return the client`() {
        val client = ClientSampler.sample()
        val clientId = client.id

        every { clientRepository.findById(any()) } returns Optional.of(client)

        val result = clientService.findById(clientId)

        assertEquals(client, result)
    }

    @Test
    fun `given a page request with invalid sort field, should throw a domain exception`() {
        val pageRequest = PageRequestDtoSampler.sample(sortBy = "invalid-field")

        val exception =
            assertThrows<DomainException> {
                clientService.findAll(pageRequest)
            }

        assertEquals(ErrorType.INVALID_FIELD, exception.type)
    }

    @Test
    fun `given a page request, should return a page of clients`() {
        val pageRequest = PageRequestDtoSampler.sample()
        val client = ClientSampler.sample()
        val page = PageImpl(listOf(client))

        every { clientRepository.findAll(pageRequest.toDomain(setOf("name", "createdAt"))) } returns page

        val result = clientService.findAll(pageRequest)

        assertEquals(page, result)
    }
}
