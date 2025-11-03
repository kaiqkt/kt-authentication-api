package com.kaiqkt.authentication.domain.models

import io.azam.ulidj.ULID
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "clients")
@EntityListeners(AuditingEntityListener::class)
class Client(
    val name: String = "",
    val description: String? = null,
    val secret: String = "",

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "clients_resource_servers",
        joinColumns = [JoinColumn(name = "client_id")],
        inverseJoinColumns = [JoinColumn(name = "resource_server_id")]
    )
    val resourceServers: MutableSet<ResourceServer> = mutableSetOf()
) {
    @Id
    val id: String = ULID.random()

    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
}