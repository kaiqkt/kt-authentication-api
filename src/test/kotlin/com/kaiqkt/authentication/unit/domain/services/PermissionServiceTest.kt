package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.repositories.PermissionRepository
import com.kaiqkt.authentication.domain.services.PermissionService
import com.kaiqkt.authentication.unit.domain.dtos.PageRequestDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.PermissionDtoSampler
import com.kaiqkt.authentication.unit.domain.models.PermissionSampler
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
    private val permissionService = PermissionService(permissionRepository)

    @Test
    fun `given a permission should create successfully`() {
        every { permissionRepository.existsByResourceAndVerb(any(), any()) } returns false
        every { permissionRepository.save(any()) } returns PermissionSampler.sample()

        permissionService.create(PermissionDtoSampler.sample())

        verify { permissionRepository.existsByResourceAndVerb(any(), any()) }
        verify { permissionRepository.save(any()) }
    }

    @Test
    fun `given a permission when permission already exists should`() {
        every { permissionRepository.existsByResourceAndVerb(any(), any()) } returns true

        val exception =
            assertThrows<DomainException> {
                permissionService.create(PermissionDtoSampler.sample())
            }

        verify { permissionRepository.existsByResourceAndVerb(any(), any()) }

        assertEquals(ErrorType.PERMISSION_ALREADY_EXISTS, exception.type)
    }

    @Test
    fun `given a page request when is valid should return permissions paginated successfully`() {
        every {
            permissionRepository.findAll(any<PageRequest>())
        } returns PageImpl(listOf(PermissionSampler.sample()))

        permissionService.findAll(PageRequestDtoSampler.sample())

        verify { permissionRepository.findAll(any<PageRequest>()) }
    }

    @Test
    fun `given a page request when has invalid sort by field should thrown exception`() {
        val exception =
            assertThrows<DomainException> {
                permissionService.findAll(PageRequestDtoSampler.sample("name"))
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
