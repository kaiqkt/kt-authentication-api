package com.kaiqkt.authentication.domain.repositories

import com.kaiqkt.authentication.domain.models.User
import com.kaiqkt.authentication.domain.models.enums.AuthenticationType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun existsByEmail(email: String): Boolean

    fun findByEmailAndAuthenticationType(
        email: String,
        authType: AuthenticationType,
    ): User?
}
