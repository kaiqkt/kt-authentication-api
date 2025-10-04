package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.Session
import com.kaiqkt.authentication.domain.models.User
import com.kaiqkt.authentication.domain.repositories.SessionRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
    @param:Value("\${authentication.session-ttl}")
    private val sessionTtl: Long
) {
    private val log = LoggerFactory.getLogger(SessionService::class.java)

    fun save(id: String, refreshToken: String, user: User): Session {
        val session = Session(
            id = id,
            user = user,
            refreshToken = refreshToken,
            expireAt = LocalDateTime.now().plusSeconds(sessionTtl)
        )

        sessionRepository.save(session)

        log.info("Session ${session.id} persisted for user ${user.id} successfully")

        return session
    }

    fun findByRefreshToken(refreshToken: String): Session {
        return sessionRepository.findByRefreshToken(refreshToken)
            ?: throw DomainException(ErrorType.SESSION_NOT_FOUND)
    }

    fun findById(id: String): Session? {
        return sessionRepository.findById(id).getOrNull()
    }

    @Transactional
    fun revoke(id: String, userId: String) {
        val session = sessionRepository.findByIdAndUserId(id, userId)
            ?: throw DomainException(ErrorType.SESSION_NOT_FOUND)

        session.revokedAt = LocalDateTime.now()

        log.info("Session $id revoked successfully")
    }
}