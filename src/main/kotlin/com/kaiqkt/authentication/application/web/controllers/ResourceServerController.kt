package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.application.web.requests.ResourceServerRequestV1
import com.kaiqkt.authentication.application.web.requests.toDto
import com.kaiqkt.authentication.application.web.responses.ResourceServerResponseV1
import com.kaiqkt.authentication.application.web.responses.toResponseV1
import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.services.PermissionService
import com.kaiqkt.authentication.domain.services.ResourceServerService
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/resources")
@Validated
class ResourceServerController(
    private val resourceServerService: ResourceServerService
) {

    @PostMapping
    fun create(
        @Valid @RequestBody requestV1: ResourceServerRequestV1
    ): ResponseEntity<ResourceServerResponseV1> {
        val resourceServer = resourceServerService.create(requestV1.toDto())

        return ResponseEntity.ok(resourceServer.toResponseV1())
    }

    @DeleteMapping("/{resource_server_id}")
    fun delete(
        @PathVariable("resource_server_id") resourceId: String
    ): ResponseEntity<Unit> {
        resourceServerService.delete(resourceId)

        return ResponseEntity.noContent().build()
    }

    @GetMapping
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
    ): ResponseEntity<Page<ResourceServerResponseV1>> {
        val pageRequest = PageRequestDto(page, size, Sort.Direction.valueOf(sort), sortBy)
        val response = resourceServerService.findAll(pageRequest).map { it.toResponseV1() }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{resource_server_id}")
    fun findById(
        @PathVariable("resource_server_id") resourceId: String
    ): ResponseEntity<ResourceServerResponseV1> {
        val response = resourceServerService.findById(resourceId).toResponseV1()

        return ResponseEntity.ok(response)
    }
}