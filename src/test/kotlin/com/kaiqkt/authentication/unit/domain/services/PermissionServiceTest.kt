package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.repositories.PermissionRepository
import com.kaiqkt.authentication.domain.services.PermissionService
import com.kaiqkt.authentication.domain.services.ResourceServerService
import com.kaiqkt.authentication.unit.domain.dtos.PageRequestDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.PermissionDtoSampler
import com.kaiqkt.authentication.unit.domain.models.PermissionSampler
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

class PermissionServiceTest {
    private val permissionRepository = mockk<PermissionRepository>()
    private val resourceServerService = mockk<ResourceServerService>()
    private val permissionService = PermissionService(permissionRepository, resourceServerService)

    @Test
    fun `given a resource id and a permission should create successfully`() {
        every { resourceServerService.findById(any()) } returns ResourceServerSampler.sample()
        every { permissionRepository.existsByResourceAndVerb(any(), any()) } returns false
        every { permissionRepository.save(any()) } returns PermissionSampler.sample()

        permissionService.create(ULID.random(), PermissionDtoSampler.sample())

        verify { resourceServerService.findById(any()) }
        verify { permissionRepository.existsByResourceAndVerb(any(), any()) }
        verify { permissionRepository.save(any()) }
    }

    @Test
    fun `given a resource id and a permission when permission already exists should`() {
        every { resourceServerService.findById(any()) } returns ResourceServerSampler.sample()
        every { permissionRepository.existsByResourceAndVerb(any(), any()) } returns true

        val exception =
            assertThrows<DomainException> {
                permissionService.create(ULID.random(), PermissionDtoSampler.sample())
            }

        verify { resourceServerService.findById(any()) }
        verify { permissionRepository.existsByResourceAndVerb(any(), any()) }

        assertEquals(ErrorType.PERMISSION_ALREADY_EXISTS, exception.type)
    }

    @Test
    fun `given a resource id and page request when is valid should return permissions paginated successfully`() {
        every {
            permissionRepository.findAllByResourceServerId(
                any(),
                any(),
            )
        } returns PageImpl(listOf(PermissionSampler.sample()))

        permissionService.findAll(ULID.random(), PageRequestDtoSampler.sample())

        verify { permissionRepository.findAllByResourceServerId(any(), any()) }
    }

    @Test
    fun `given a resource id and page request when has invalid sort by field should thrown exception`() {
        val exception =
            assertThrows<DomainException> {
                permissionService.findAll(ULID.random(), PageRequestDtoSampler.sample("name"))
            }

        assertEquals(ErrorType.INVALID_FIELD, exception.type)
    }

    @Test
    fun `given a page request when is valid should return permissions paginated successfully`() {
        every {
            permissionRepository.findAll(any<PageRequest>())
        } returns PageImpl(listOf(PermissionSampler.sample()))

        permissionService.findAll(null, PageRequestDtoSampler.sample())

        verify { permissionRepository.findAll(any<PageRequest>()) }
    }

    @Test
    fun `given a page request when has invalid sort by field should thrown exception`() {
        val exception =
            assertThrows<DomainException> {
                permissionService.findAll(null, PageRequestDtoSampler.sample("name"))
            }

        assertEquals(ErrorType.INVALID_FIELD, exception.type)
    }

    @Test
    fun `given a permission id should delete successfully`() {
        justRun { permissionRepository.deleteById(any()) }

        permissionService.delete(ULID.random())

        verify { permissionRepository.deleteById(any()) }
    }

    @Test
    fun `given a permission id when permission exists should return successfully`() {
        every { permissionRepository.findById(any()) } returns Optional.of(PermissionSampler.sample())

        permissionService.findById(ULID.random())

        verify { permissionRepository.findById(any()) }
    }

    @Test
    fun `given a permission id when permission does not exists should throw an exception`() {
        every { permissionRepository.findById(any()) } returns Optional.empty()

        val exception =
            assertThrows<DomainException> {
                permissionService.findById(ULID.random())
            }

        verify { permissionRepository.findById(any()) }

        assertEquals(ErrorType.PERMISSION_NOT_FOUND, exception.type)
    }
}
