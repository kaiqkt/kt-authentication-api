package com.kaiqkt.authentication.domain.repositories

import com.kaiqkt.authentication.domain.models.AuthorizationCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AuthorizationCodeRepository : JpaRepository<AuthorizationCode, String> {
    @Query(
        """
        SELECT a FROM AuthorizationCode a
        WHERE a.code = :code 
        AND a.redirectUri = :redirectUri
        AND a.expireAt > CURRENT_TIMESTAMP
    """
    )
    fun findByCode(code: String, redirectUri: String): AuthorizationCode?
    fun deleteByCode(code: String)
}