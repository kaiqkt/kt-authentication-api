package com.kaiqkt.authentication.domain.models

import com.kaiqkt.authentication.domain.models.enums.AuthenticationType
import io.azam.ulidj.ULID
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class User(
    var email: String = "",
    @Enumerated(EnumType.STRING)
    var authenticationType: AuthenticationType = AuthenticationType.PASSWORD
) {
    @Id
    val id: String = ULID.random()
    var isVerified: Boolean = false
    var password: String? = null

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
}