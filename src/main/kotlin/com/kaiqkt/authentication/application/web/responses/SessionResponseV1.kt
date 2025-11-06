package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.models.Session
import java.time.LocalDateTime

data class SessionResponseV1(
    val id: String,
    val expireAt: LocalDateTime,
    val revokedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
)

fun Session.toResponse(): SessionResponseV1 =
    SessionResponseV1(
        id = this.id,
        expireAt = this.expireAt,
        revokedAt = this.revokedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
