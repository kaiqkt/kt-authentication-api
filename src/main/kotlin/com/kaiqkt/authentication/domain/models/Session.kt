package com.kaiqkt.authentication.domain.models

import io.azam.ulidj.ULID
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "sessions")
@EntityListeners(AuditingEntityListener::class)
class Session(
    @Id
    val id: String = ULID.random(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User = User(),

    var refreshToken: String = "",
    var expireAt: LocalDateTime = LocalDateTime.now()
) {
    var revokedAt: LocalDateTime? = null

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
}