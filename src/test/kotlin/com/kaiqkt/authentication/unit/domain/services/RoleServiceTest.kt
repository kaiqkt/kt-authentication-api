package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.repositories.RoleRepository
import com.kaiqkt.authentication.domain.services.PermissionService
import com.kaiqkt.authentication.domain.services.RoleService
import com.kaiqkt.authentication.unit.domain.dtos.PageRequestDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.RoleDtoSampler
import com.kaiqkt.authentication.unit.domain.models.PermissionSampler
import com.kaiqkt.authentication.unit.domain.models.RoleSampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RoleServiceTest {
    private val roleRepository = mockk<RoleRepository>()
    private val permissionService = mockk<PermissionService>()
    private val roleService = RoleService(roleRepository, permissionService)

    @Test
    fun `given a role dto should create successfully`() {
        every { roleRepository.existsByName(any()) } returns false
        every { roleRepository.save(any()) } returns RoleSampler.sample()

        roleService.create(RoleDtoSampler.sample())

        verify { roleRepository.existsByName(any()) }
        verify { roleRepository.save(any()) }
    }

    @Test
    fun `given a role dto when already exists a role with same name should thrown an exception`() {
        every { roleRepository.existsByName(any()) } returns true

        val exception = assertThrows<DomainException> {
            roleService.create(RoleDtoSampler.sample())
        }

        verify { roleRepository.existsByName(any()) }

        assertEquals(ErrorType.ROLE_ALREADY_EXISTS, exception.type)
    }

    @Test
    fun `given a role id should delete successfully`() {
        justRun { roleRepository.deleteById(any()) }

        roleService.delete(ULID.random())

        verify { roleRepository.deleteById(any()) }
    }

    @Test
    fun `given a request to find all roles should return paginated successfully`() {
        every { roleRepository.findAll(any<Pageable>()) } returns PageImpl(listOf(RoleSampler.sample()))

        roleService.findAll(PageRequestDtoSampler.sample())

        verify { roleRepository.findAll(any<Pageable>()) }
    }

    @Test
    fun `given a request to find all roles when sort by field is invalid should thrown an exception`() {
        val exception = assertThrows<DomainException> {
            roleService.findAll(PageRequestDtoSampler.sample(sortBy = "invalid"))
        }

        assertEquals(ErrorType.INVALID_FIELD, exception.type)
    }

    @Test
    fun `given a role id and a permission id should associate successfully`() {
        every { permissionService.findById(any()) } returns PermissionSampler.sample()
        every { roleRepository.findById(any()) } returns Optional.of(RoleSampler.sample())

        roleService.associate(ULID.random(), ULID.random())

        verify { permissionService.findById(any()) }
        verify { roleRepository.findById(any()) }
    }

    @Test
    fun `given a role id and a permission id to associate when role does not exists should thrown an exception`() {
        every { permissionService.findById(any()) } returns PermissionSampler.sample()
        every { roleRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> {
            roleService.associate(ULID.random(), ULID.random())
        }

        verify { permissionService.findById(any()) }

        assertEquals(ErrorType.ROLE_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a role when found should return successfully`() {
        every { roleRepository.findById(any()) } returns Optional.of(RoleSampler.sample())

        roleService.findById(ULID.random())

        verify { roleRepository.findById(any()) }
    }

    @Test
    fun `given a role when not found should thrown an exception`() {
        every { roleRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> {
            roleService.findById(ULID.random())
        }

        verify { roleRepository.findById(any()) }

        assertEquals(ErrorType.ROLE_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a role id and a permission id should disassociate successfully`() {
        val permission = PermissionSampler.sample()
        val role = RoleSampler.sample()
            .apply { permissions.add(permission) }

        every { roleRepository.findById(any()) } returns Optional.of(role)
        every { permissionService.findById(any()) } returns permission

        roleService.associate(ULID.random(), permission.id)

        verify { roleRepository.findById(any()) }
        verify { permissionService.findById(any()) }
    }
}