package com.kaiqkt.authentication.domain.models

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "authorization_codes")
@EntityListeners(AuditingEntityListener::class)
class AuthorizationCode(
    @Id
    val code: String = "",
    val redirectUri: String = "",
    val codeChallenge: String = "",
    val expireAt: LocalDateTime = LocalDateTime.now(),
    //val clientId: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User = User()
) {
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
}