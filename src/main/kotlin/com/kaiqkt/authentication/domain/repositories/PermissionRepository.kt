package com.kaiqkt.authentication.domain.repositories

import com.kaiqkt.authentication.domain.models.Permission
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository: JpaRepository<Permission, String>{
    fun existsByResourceAndVerb(resource: String, verb: String): Boolean
    fun findAllByResourceServerId(resourceId: String, pageable: Pageable): Page<Permission>
}