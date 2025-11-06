package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.repositories.ResourceServerRepository
import com.kaiqkt.authentication.domain.services.ResourceServerService
import com.kaiqkt.authentication.unit.domain.dtos.PageRequestDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.ResourceServerDtoSampler
import com.kaiqkt.authentication.unit.domain.models.ResourceServerSampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceServerServiceTest {
    private val resourceServerRepository = mockk<ResourceServerRepository>()
    private val service = ResourceServerService(resourceServerRepository)

    @Test
    fun `given a user id and a resource server dto should create successfully`() {
        every { resourceServerRepository.save(any()) } returns ResourceServerSampler.sample()

        service.create(ResourceServerDtoSampler.sample())

        verify { resourceServerRepository.save(any()) }
    }

    @Test
    fun `given a page request dto when sort field are valid should return all resource server successfully`() {
        every { resourceServerRepository.findAll(any<PageRequest>()) } returns PageImpl(listOf(ResourceServerSampler.sample()))

        service.findAll(PageRequestDtoSampler.sample())

        verify { resourceServerRepository.findAll(any<PageRequest>()) }
    }

    @Test
    fun `given a page request dto when sort field are invalid should throw an exception`() {
        val exception =
            assertThrows<DomainException> {
                service.findAll(PageRequestDtoSampler.sample(sortBy = "desc"))
            }

        assertEquals(ErrorType.INVALID_FIELD, exception.type)
    }

    @Test
    fun `given a resource server id should delete successfully`() {
        justRun { resourceServerRepository.deleteById(any()) }

        service.delete(ULID.random())

        verify { resourceServerRepository.deleteById(any()) }
    }

    @Test
    fun `given a resource id when found should return successfully`() {
        every { resourceServerRepository.findById(any()) } returns Optional.of(ResourceServerSampler.sample())

        service.findById(ULID.random())

        verify { resourceServerRepository.findById(any()) }
    }

    @Test
    fun `given a resource id when not found should return successfully`() {
        every { resourceServerRepository.findById(any()) } returns Optional.empty()

        val exception =
            assertThrows<DomainException> {
                service.findById(ULID.random())
            }

        verify { resourceServerRepository.findById(any()) }

        assertEquals(ErrorType.RESOURCE_SERVER_NOT_FOUND, exception.type)
    }
}
