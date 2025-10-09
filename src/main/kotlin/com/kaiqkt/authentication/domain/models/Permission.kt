package com.kaiqkt.authentication.domain.models

import io.azam.ulidj.ULID
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "permissions")
@EntityListeners(AuditingEntityListener::class)
class Permission(
    val resource: String = "",
    val verb: String = "",
    val description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_server_id")
    val resourceServer: ResourceServer = ResourceServer()
) {
    @Id
    val id: String = ULID.random()

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
}
