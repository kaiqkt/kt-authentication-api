package com.kaiqkt.authentication.domain.models

import io.azam.ulidj.ULID
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "roles")
@EntityListeners(AuditingEntityListener::class)
class Role(
    val name: String = "",
    val description: String? = null
) {
    @Id
    val id: String = ULID.random()

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    val permissions: MutableSet<Permission> = mutableSetOf()

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
}

