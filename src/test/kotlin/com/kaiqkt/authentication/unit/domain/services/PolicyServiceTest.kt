package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.repositories.PolicyRepository
import com.kaiqkt.authentication.domain.services.PermissionService
import com.kaiqkt.authentication.domain.services.PolicyService
import com.kaiqkt.authentication.domain.services.ResourceServerService
import com.kaiqkt.authentication.domain.services.RoleService
import com.kaiqkt.authentication.unit.domain.dtos.PageRequestDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.PolicyDtoSampler
import com.kaiqkt.authentication.unit.domain.dtos.PolicySampler
import com.kaiqkt.authentication.unit.domain.models.PermissionSampler
import com.kaiqkt.authentication.unit.domain.models.ResourceServerSampler
import com.kaiqkt.authentication.unit.domain.models.RoleSampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals

class PolicyServiceTest {
    private val policyRepository = mockk<PolicyRepository>()
    private val resourceServerService = mockk<ResourceServerService>()
    private val permissionService = mockk<PermissionService>()
    private val roleService = mockk<RoleService>()
    private val policyService = PolicyService(
        policyRepository,
        resourceServerService,
        permissionService,
        roleService
    )

    @Test
    fun `given a resource server id and a policy should create successfully`() {
        every { policyRepository.existsByUriAndMethodAndResourceServerId(any(), any(), any()) } returns false
        every { resourceServerService.findById(any()) } returns ResourceServerSampler.sample()
        every { policyRepository.save(any()) } returns PolicySampler.sample()

        policyService.create(ULID.random(), PolicyDtoSampler.sample())

        verify { policyRepository.existsByUriAndMethodAndResourceServerId(any(), any(), any()) }
        verify { resourceServerService.findById(any()) }
        verify { policyRepository.save(any()) }
    }

    @Test
    fun `given a resource server id and a policy when a policy already exists should thrown an exception`() {
        every { policyRepository.existsByUriAndMethodAndResourceServerId(any(), any(), any()) } returns true

        val exception = assertThrows<DomainException> {
            policyService.create(ULID.random(), PolicyDtoSampler.sample())
        }

        verify { policyRepository.existsByUriAndMethodAndResourceServerId(any(), any(), any()) }

        assertEquals(ErrorType.POLICY_ALREADY_EXISTS, exception.type)
    }

    @Test
    fun `given a resource id should return all policies associated successfully`(){
        every { policyRepository.findAllByResourceServerId(any()) } returns listOf()

        policyService.findAllByResourceId(ULID.random())

        verify { policyRepository.findAllByResourceServerId(any()) }
    }

    @Test
    fun `given a policy id should return successfully`(){
        every { policyRepository.findById(any()) } returns Optional.of(PolicySampler.sample())

        policyService.findById(ULID.random())

        verify { policyRepository.findById(any()) }
    }

    @Test
    fun `given a policy id when does not exist should thrown an exception`(){
        every { policyRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> {
            policyService.findById(ULID.random())
        }

        verify { policyRepository.findById(any()) }

        assertEquals(ErrorType.POLICY_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a resource id and a page request should return policies associated and paginated successfully`(){
        every { policyRepository.findAllByResourceServerId(any(), any()) } returns PageImpl(listOf(PolicySampler.sample()))

        policyService.findAll(ULID.random(), PageRequestDtoSampler.sample())

        verify { policyRepository.findAllByResourceServerId(any(), any()) }
    }

    @Test
    fun `given a page request when resource server id is null should return policies associated and paginated successfully`(){
        every { policyRepository.findAll( any<Pageable>()) } returns PageImpl(listOf(PolicySampler.sample()))

        policyService.findAll(null, PageRequestDtoSampler.sample())

        verify { policyRepository.findAll( any<Pageable>()) }
    }

    @Test
    fun `given a page request when has a invalid sort by field should thrown an exception`(){
       val exception = assertThrows<DomainException> {
           policyService.findAll(null, PageRequestDtoSampler.sample(sortBy = "invalid"))
       }

        assertEquals(ErrorType.INVALID_FIELD, exception.type)
    }

    @Test
    fun `given a policy id should delete successfully`() {
        justRun { policyRepository.deleteById(any()) }

        policyService.delete(ULID.random())

        verify { policyRepository.deleteById(any()) }
    }

    @Test
    fun `given a permission to associate to a policy should associate successfully`() {
        every { permissionService.findById(any()) } returns PermissionSampler.sample()
        every { policyRepository.findById(any()) } returns Optional.of(PolicySampler.sample())

        policyService.associatePermission(ULID.random(), ULID.random())

        verify { permissionService.findById(any()) }
        verify { policyRepository.findById(any()) }
    }

    @Test
    fun `given a permission to associate to a policy when policy does not exist should throw an exception`() {
        every { permissionService.findById(any()) } returns PermissionSampler.sample()
        every { policyRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> {
            policyService.associatePermission(ULID.random(), ULID.random())
        }

        verify { permissionService.findById(any()) }
        verify { policyRepository.findById(any()) }

        assertEquals(ErrorType.POLICY_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a permission to dissociate to a policy should associate successfully`() {
        val permission = PermissionSampler.sample()
        val policy = PolicySampler.sample(permission = permission)

        every { permissionService.findById(any()) } returns permission
        every { policyRepository.findById(any()) } returns Optional.of(policy)

        policyService.associatePermission(ULID.random(), permission.id)

        verify { permissionService.findById(any()) }
    }

    @Test
    fun `given a role to associate to a policy should associate successfully`() {
        every { roleService.findById(any()) } returns RoleSampler.sample()
        every { policyRepository.findById(any()) } returns Optional.of(PolicySampler.sample())

        policyService.associateRole(ULID.random(), ULID.random())

        verify { roleService.findById(any()) }
        verify { policyRepository.findById(any()) }
    }

    @Test
    fun `given a role to associate to a policy when policy does not exist should throw an exception`() {
        every { roleService.findById(any()) } returns RoleSampler.sample()
        every { policyRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> {
            policyService.associateRole(ULID.random(), ULID.random())
        }

        verify { roleService.findById(any()) }
        verify { policyRepository.findById(any()) }

        assertEquals(ErrorType.POLICY_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a role to dissociate to a policy should associate successfully`() {
        val role = RoleSampler.sample()
        val policy = PolicySampler.sample(role = role)

        every { roleService.findById(any()) } returns role
        every { policyRepository.findById(any()) } returns Optional.of(policy)

        policyService.associateRole(ULID.random(), role.id)

        verify { policyRepository.findById(any()) }
        verify { roleService.findById(any()) }
    }

    @Test
    fun `given a list of ids should return a list of policies`(){
        every { policyRepository.findAllById(any()) } returns listOf()

        policyService.findAllById(listOf())

        verify { policyRepository.findAllById(any()) }
    }
}