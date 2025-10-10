package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.application.web.requests.RoleRequestV1
import com.kaiqkt.authentication.application.web.requests.toDto
import com.kaiqkt.authentication.application.web.responses.RoleResponseV1
import com.kaiqkt.authentication.application.web.responses.toResponseV1
import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.services.RoleService
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
@RequestMapping("/v1/roles")
@Validated
class RoleController(
    private val roleService: RoleService
) {

    @PostMapping
    fun create(
        @Valid @RequestBody requestV1: RoleRequestV1
    ): ResponseEntity<RoleResponseV1> {
        val response = roleService.create(requestV1.toDto()).toResponseV1()

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{role_id}")
    fun delete(@PathVariable("role_id") roleId: String): ResponseEntity<Unit> {
        roleService.delete(roleId)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{role_id}")
    fun findById(@PathVariable("role_id") roleId: String): ResponseEntity<RoleResponseV1?> {
        val response = roleService.findById(roleId).toResponseV1()

        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{role_id}/associate/{permission_id}")
    fun associate(
        @PathVariable("role_id") roleId: String,
        @PathVariable("permission_id") permissionId: String
    ): ResponseEntity<Unit> {
        roleService.associate(roleId, permissionId)

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
    ): ResponseEntity<Page<RoleResponseV1>> {
        val pageRequest = PageRequestDto(page, size, Sort.Direction.valueOf(sort), sortBy)
        val response = roleService.findAll(pageRequest).map { it.toResponseV1() }

        return ResponseEntity.ok(response)
    }
}