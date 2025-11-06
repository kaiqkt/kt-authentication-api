package com.kaiqkt.authentication.domain.repositories

import com.kaiqkt.authentication.domain.models.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, String> {
    fun existsByName(name: String): Boolean
}
