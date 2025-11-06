package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.dtos.RoleDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.Role
import com.kaiqkt.authentication.domain.repositories.RoleRepository
import com.kaiqkt.authentication.domain.utils.Constants
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class RoleService(
    private val roleRepository: RoleRepository,
    private val permissionService: PermissionService,
) {
    private val log = LoggerFactory.getLogger(PermissionService::class.java)
    private val allowedSortFields = Constants.Sort.getAllowedFiled("name")

    fun create(roleDto: RoleDto): Role {
        if (roleRepository.existsByName(roleDto.name)) {
            throw DomainException(ErrorType.ROLE_ALREADY_EXISTS)
        }

        val role =
            Role(
                name = roleDto.name.uppercase(),
                description = roleDto.description,
            )

        roleRepository.save(role)

        log.info("Role ${role.id} created")

        return role
    }

    fun findById(roleId: String): Role =
        roleRepository.findById(roleId).getOrNull()
            ?: throw DomainException(ErrorType.ROLE_NOT_FOUND)

    fun delete(roleId: String) {
        roleRepository.deleteById(roleId)

        log.info("Role $roleId deleted")
    }

    fun findAll(pageRequestDto: PageRequestDto): Page<Role> {
        try {
            val pageable = pageRequestDto.toDomain(allowedSortFields)

            return roleRepository.findAll(pageable)
        } catch (_: IllegalArgumentException) {
            throw DomainException(ErrorType.INVALID_FIELD)
        }
    }

    @Transactional
    fun associate(
        roleId: String,
        permissionId: String,
    ) {
        val permission = permissionService.findById(permissionId)
        val role = findById(roleId)

        if (role.permissions.contains(permission)) {
            role.permissions.remove(permission)
            log.info("Role $roleId disassociate from permission $permissionId")

            return
        }

        role.permissions.add(permission)

        log.info("Role $roleId associated with permission $permissionId")
    }
}
