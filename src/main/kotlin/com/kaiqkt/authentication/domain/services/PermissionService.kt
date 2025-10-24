package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.dtos.PermissionDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.Permission
import com.kaiqkt.authentication.domain.repositories.PermissionRepository
import com.kaiqkt.authentication.domain.utils.Constants
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class PermissionService(
    private val permissionRepository: PermissionRepository,
    private val resourceServerService: ResourceServerService
) {
    private val log = LoggerFactory.getLogger(PermissionService::class.java)

    private val allowedSortFields = Constants.Sort.getAllowedFiled("resource", "verb")

    fun create(resourceId: String, permissionDto: PermissionDto): Permission {
        val resourceServer = resourceServerService.findById(resourceId)

        if (permissionRepository.existsByResourceAndVerb(permissionDto.resource, permissionDto.verb)) {
            throw DomainException(ErrorType.PERMISSION_ALREADY_EXISTS)
        }

        val permission = Permission(
            resource = permissionDto.resource,
            verb = permissionDto.verb,
            description = permissionDto.description,
            resourceServer = resourceServer
        )

        permissionRepository.save(permission)

        log.info("Permission ${permission.id} for resource server ${resourceServer.id} created")

        return permission
    }

    fun delete(permissionId: String) {
        permissionRepository.deleteById(permissionId)

        log.info("Permission $permissionId deleted")
    }

    fun findById(permissionId: String): Permission {
        return permissionRepository.findById(permissionId).getOrNull()
            ?: throw DomainException(ErrorType.PERMISSION_NOT_FOUND)
    }

    fun findAll(resourceServerId: String?, pageRequestDto: PageRequestDto): Page<Permission> {
        try {
            val pageable = pageRequestDto.toDomain(allowedSortFields)

            if (resourceServerId != null) {
                return permissionRepository.findAllByResourceServerId(resourceServerId, pageable)
            }

            return permissionRepository.findAll(pageable)
        } catch (_: IllegalArgumentException) {
            throw DomainException(ErrorType.INVALID_FIELD)
        }
    }
}