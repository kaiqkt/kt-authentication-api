package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.dtos.ResourceServerDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.ResourceServer
import com.kaiqkt.authentication.domain.repositories.ResourceServerRepository
import com.kaiqkt.authentication.domain.utils.Constants
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class ResourceServerService(
    private val resourceServerRepository: ResourceServerRepository
) {
    private val log = LoggerFactory.getLogger(ResourceServerService::class.java)

    private val allowedSortFields = Constants.Sort.getAllowedFiled("name")

    fun create(resourceServerDto: ResourceServerDto): ResourceServer {
        val resourceServer = ResourceServer(
            name = resourceServerDto.name,
            description = resourceServerDto.description
        )

        resourceServerRepository.save(resourceServer)

        log.info("Resource server ${resourceServer.id} with name ${resourceServerDto.name}")

        return resourceServer
    }

    fun findAll(pageRequestDto: PageRequestDto): Page<ResourceServer> {
        try {
            val pageable = pageRequestDto.toDomain(allowedSortFields)

            return resourceServerRepository.findAll(pageable)
        } catch (_: IllegalArgumentException) {
            throw DomainException(ErrorType.INVALID_FIELD)
        }
    }

    fun findAllById(ids: List<String>): List<ResourceServer> {
        return resourceServerRepository.findAllById(ids)
    }

    fun findById(id: String): ResourceServer {
        return resourceServerRepository.findById(id).getOrNull()
            ?: throw DomainException(ErrorType.RESOURCE_SERVER_NOT_FOUND)
    }

    fun delete(id: String) {
        resourceServerRepository.deleteById(id)

        log.info("Resource server $id deleted")
    }
}