package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.Role
import com.kaiqkt.authentication.domain.models.Session
import com.kaiqkt.authentication.domain.models.User
import com.kaiqkt.authentication.domain.repositories.SessionRepository
import com.kaiqkt.authentication.domain.utils.Constants
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
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
    private val allowedSortFields = Constants.Sort.COMMON_FIELDS

    fun save(sessionId: String, refreshToken: String, user: User): Session {
        val session = Session(
            id = sessionId,
            user = user,
            refreshToken = refreshToken,
            expireAt = LocalDateTime.now().plusSeconds(sessionTtl)
        )

        sessionRepository.save(session)

        log.info("Session ${session.id} of user ${user.id} persisted")

        return session
    }

    fun findByRefreshToken(refreshToken: String): Session {
        return sessionRepository.findByRefreshToken(refreshToken)
            ?: throw DomainException(ErrorType.SESSION_NOT_FOUND)
    }

    fun findById(sessionId: String): Session? {
        return sessionRepository.findById(sessionId).getOrNull()
    }

    @Transactional
    fun revoke(sessionId: String, userId: String) {
        val session = sessionRepository.findByIdAndUserId(sessionId, userId)
            ?: throw DomainException(ErrorType.SESSION_NOT_FOUND)

        session.revokedAt = LocalDateTime.now()

        log.info("Session $sessionId revoked")
    }

    fun findAllByUserId(userId: String, pageRequestDto: PageRequestDto): Page<Session> {
        if (!pageRequestDto.isValid(allowedSortFields)) {
            throw DomainException(ErrorType.INVALID_SORT_FIELD)
        }

        return sessionRepository.findAll(pageRequestDto.toDomain())
    }
}