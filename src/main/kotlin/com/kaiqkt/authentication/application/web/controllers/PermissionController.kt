package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.application.web.requests.PermissionRequestV1
import com.kaiqkt.authentication.application.web.requests.toDto
import com.kaiqkt.authentication.application.web.responses.PermissionResponseV1
import com.kaiqkt.authentication.application.web.responses.toResponseV1
import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.services.PermissionService
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
@RequestMapping("/v1/permissions")
@Validated
class PermissionController(
    private val permissionService: PermissionService
) {
    @PostMapping
    fun create(
        @RequestParam("resource_server_id") resourceId: String,
        @Valid @RequestBody requestV1: PermissionRequestV1
    ): ResponseEntity<PermissionResponseV1> {
        val response = permissionService.create(resourceId, requestV1.toDto()).toResponseV1()

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{permission_id}")
    fun delete(
        @PathVariable("permission_id") permissionId: String
    ): ResponseEntity<PermissionResponseV1> {
        permissionService.delete(permissionId)

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
        sortBy: String?,
        @RequestParam(value = "resource_server_id", required = false) resourceServerId: String?
    ): ResponseEntity<Page<PermissionResponseV1>> {
        val request = PageRequestDto(page, size, Sort.Direction.valueOf(sort), sortBy)
        val response = if (resourceServerId != null) {
            permissionService.findAllByResourceServerId(resourceServerId, request).map { it.toResponseV1() }
        } else {
            permissionService.findAll(request).map { it.toResponseV1() }
        }

        return ResponseEntity.ok(response)
    }
}