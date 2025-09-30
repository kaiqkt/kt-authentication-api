package com.kaiqkt.authentication.domain.repositories

import com.kaiqkt.authentication.domain.models.Session
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SessionRepository: JpaRepository<Session, String> {
    @Query("""
        SELECT s FROM Session s
        WHERE s.refreshToken = :refreshToken
        AND s.expireAt > CURRENT_TIMESTAMP AND s.revokedAt IS NULL
        AND s.revokedAt IS NULL
    """)
    fun findByRefreshToken(refreshToken: String): Session?

    @Query("""
        SELECT s FROM Session s
        WHERE s.id = :id
        AND s.expireAt > CURRENT_TIMESTAMP AND s.revokedAt IS NULL
        AND s.revokedAt IS NULL
    """)
    override fun findById(id: String): Optional<Session>

    @Query("""
        SELECT s FROM Session s
        WHERE s.id = :id 
        AND s.user.id = :userId
        AND s.expireAt > CURRENT_TIMESTAMP AND s.revokedAt IS NULL
        AND s.revokedAt IS NULL
    """)
    fun findByIdAndUserId(id: String, userId: String): Session?
}