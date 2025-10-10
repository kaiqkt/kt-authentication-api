package com.kaiqkt.authentication.domain.models

import com.kaiqkt.authentication.domain.models.enums.Method
import io.azam.ulidj.ULID
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "policies")
@EntityListeners(AuditingEntityListener::class)
class Policy(
    val uri: String = "",

    @Enumerated(value = EnumType.STRING)
    val method: Method = Method.GET,

    val isPublic: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    val resourceServer: ResourceServer = ResourceServer()
) {
    @Id
    val id: String = ULID.random()

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "policies_roles",
        joinColumns = [JoinColumn(name = "policy_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: MutableSet<Role> = mutableSetOf()

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "policies_permissions",
        joinColumns = [JoinColumn(name = "policy_id")],
        inverseJoinColumns = [JoinColumn(name = "permission_id")]
    )
    val permissions: MutableSet<Permission> = mutableSetOf()

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
}

