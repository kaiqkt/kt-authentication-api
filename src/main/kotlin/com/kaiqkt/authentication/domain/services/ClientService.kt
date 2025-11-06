package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.ClientDto
import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.Client
import com.kaiqkt.authentication.domain.repositories.ClientRepository
import com.kaiqkt.authentication.domain.utils.Constants
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class ClientService(
    private val clientRepository: ClientRepository,
    private val policyService: PolicyService,
    private val tokenService: TokenService,
) {
    private val allowedSortFields = Constants.Sort.getAllowedFiled("name")

    private val log = LoggerFactory.getLogger(ClientService::class.java)

    fun create(clientDto: ClientDto): Client {
        val policies = policyService.findAllById(clientDto.policies)

        if (policies.isEmpty()) {
            throw DomainException(ErrorType.POLICY_NOT_FOUND)
        }

        return Client(
            name = clientDto.name,
            description = clientDto.description,
            secret = tokenService.opaqueToken(),
            policies = policies.toMutableSet(),
        ).run(clientRepository::save)
            .also { log.info("Client ${it.id} created") }
    }

    fun delete(clientId: String) {
        clientRepository.deleteById(clientId)

        log.info("Client $clientId deleted")
    }

    fun findById(clientId: String): Client =
        clientRepository.findById(clientId).getOrNull()
            ?: throw DomainException(ErrorType.CLIENT_NOT_FOUND)

    fun findAll(pageRequestDto: PageRequestDto): Page<Client> {
        try {
            val pageable = pageRequestDto.toDomain(allowedSortFields)

            return clientRepository.findAll(pageable)
        } catch (_: IllegalArgumentException) {
            throw DomainException(ErrorType.INVALID_FIELD)
        }
    }
}
