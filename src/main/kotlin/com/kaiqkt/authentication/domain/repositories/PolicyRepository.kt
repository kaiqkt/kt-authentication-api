package com.kaiqkt.authentication.domain.repositories

import com.kaiqkt.authentication.domain.models.Policy
import com.kaiqkt.authentication.domain.models.enums.Method
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PolicyRepository: JpaRepository<Policy, String>{
    fun existsByUriAndMethodAndResourceServerId(uri: String, method: Method, resourceServerId: String): Boolean
    fun findAllByResourceServerId(resourceServerId: String): List<Policy>
    fun findAllByResourceServerId(resourceServerId: String, pageable: Pageable): Page<Policy>
}