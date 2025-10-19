package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.dtos.PolicyDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.Policy
import com.kaiqkt.authentication.domain.repositories.PolicyRepository
import com.kaiqkt.authentication.domain.utils.Constants
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class PolicyService(
    private val policyRepository: PolicyRepository,
    private val resourceServerService: ResourceServerService,
    private val permissionService: PermissionService,
    private val roleService: RoleService
) {
    private val log = LoggerFactory.getLogger(PolicyService::class.java)

    private val allowedSortFields = Constants.Sort.getAllowedFiled()

    fun create(resourceServerId: String, policyDto: PolicyDto): Policy {
        val alreadyExists = policyRepository.existsByUriAndMethodAndResourceServerId(
            policyDto.uri,
            policyDto.method,
            resourceServerId
        )

        if (alreadyExists) {
            throw DomainException(ErrorType.POLICY_ALREADY_EXISTS)
        }

        val resourceServer = resourceServerService.findById(resourceServerId)

        val policy = Policy(
            uri = policyDto.uri,
            method = policyDto.method,
            isPublic = policyDto.isPublic,
            resourceServer = resourceServer,
        )

        policyRepository.save(policy)

        log.info("Policy ${policy.id} for resource server ${resourceServer.id} created")

        return policy
    }

    fun delete(policyId: String) {
        policyRepository.deleteById(policyId)

        log.info("Policy $policyId deleted")
    }

    fun findAllByResourceId(resourceServerId: String): List<Policy> {
        return policyRepository.findAllByResourceServerId(resourceServerId)
    }

    fun findById(policyId: String): Policy {
        return policyRepository.findById(policyId).getOrNull()
            ?: throw DomainException(ErrorType.POLICY_NOT_FOUND)
    }

    fun findAll(resourceServerId: String?, pageRequestDto: PageRequestDto): Page<Policy> {
        try {
            val pageable = pageRequestDto.toDomain(allowedSortFields)

            if (resourceServerId != null) {
                return policyRepository.findAllByResourceServerId(resourceServerId, pageable)
            }

            return policyRepository.findAll(pageable)
        } catch (_: IllegalArgumentException) {
            throw DomainException(ErrorType.INVALID_SORT_FIELD)
        }
    }

    @Transactional
    fun associatePermission(policyId: String, permissionId: String){
        val permission = permissionService.findById(permissionId)
        val policy = findById(policyId)

        if (policy.permissions.contains(permission)) {
            policy.permissions.remove(permission)
            log.info("Permission $permissionId dissociate of policy $policyId")

            return
        }

        policy.permissions.add(permission)

        log.info("Permission $permissionId associated to policy $policyId")
    }

    @Transactional
    fun associateRole(policyId: String, roleId: String){
        val role = roleService.findById(roleId)
        val policy = findById(policyId)

        if (policy.roles.contains(role)) {
            policy.roles.remove(role)
            log.info("Role $roleId dissociate of policy $policyId")

            return
        }

        policy.roles.add(role)

        log.info("Role $roleId associated to policy $policyId")
    }
}