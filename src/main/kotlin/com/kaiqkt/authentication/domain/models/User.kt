package com.kaiqkt.authentication.domain.models

import com.kaiqkt.authentication.domain.models.enums.AuthenticationType
import io.azam.ulidj.ULID
import jakarta.persistence.*
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
    var authenticationType: AuthenticationType = AuthenticationType.PASSWORD,
    var password: String? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: MutableSet<Role> = mutableSetOf()
) {
    @Id
    val id: String = ULID.random()
    var isVerified: Boolean = false

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
}