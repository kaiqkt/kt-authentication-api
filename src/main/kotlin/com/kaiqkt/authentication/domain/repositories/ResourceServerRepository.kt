package com.kaiqkt.authentication.domain.repositories

import com.kaiqkt.authentication.domain.models.ResourceServer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ResourceServerRepository : JpaRepository<ResourceServer, String>
