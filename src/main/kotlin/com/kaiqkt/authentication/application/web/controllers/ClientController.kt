package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.application.web.requests.ClientRequestV1
import com.kaiqkt.authentication.application.web.requests.toDto
import com.kaiqkt.authentication.application.web.responses.ClientResponseV1
import com.kaiqkt.authentication.application.web.responses.toResponseV1
import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.services.ClientService
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientController(
    private val clientService: ClientService
){
    @PostMapping("/v1/clients")
    fun create(
        @Valid @RequestBody requestV1: ClientRequestV1
    ): ResponseEntity<ClientResponseV1> {
        val response =  clientService.create(requestV1.toDto()).toResponseV1()

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/clients/{client_id}")
    fun delete(
        @PathVariable(value = "client_id") clientId: String
    ): ResponseEntity<Unit> {
        clientService.delete(clientId)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/v1/clients/{client_id}")
    fun findById(
        @PathVariable(value = "client_id") clientId: String
    ): ResponseEntity<ClientResponseV1> {
        val response = clientService.findById(clientId).toResponseV1()

        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/clients")
    fun findAll(
        @RequestParam(value = "page", required = false, defaultValue = "0")
        @PositiveOrZero
        page: Int,

        @RequestParam(value = "size", required = false, defaultValue = "20")
        @Min(value = 1, message = "page size must be at least 1")
        @Max(value = 20, message = "page size should not be greater than 20")
        size: Int,

        @RequestParam(value = "sort", required = false, defaultValue = "DESC")
        @Pattern(
            regexp = "ASC|DESC",
            flags = [Pattern.Flag.CASE_INSENSITIVE],
            message = "sort should be ASC or DESC",
        )
        sort: String,

        @RequestParam(value = "sort_by", required = false)
        sortBy: String?
    ): ResponseEntity<Page<ClientResponseV1>> {
        val pageRequestDto = PageRequestDto(page, size, Sort.Direction.valueOf(sort), sortBy)
        val response = clientService.findAll(pageRequestDto).map { it.toResponseV1() }

        return ResponseEntity.ok(response)
    }
}