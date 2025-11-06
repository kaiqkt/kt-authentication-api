package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.application.web.requests.PolicyRequestV1
import com.kaiqkt.authentication.application.web.requests.toDto
import com.kaiqkt.authentication.application.web.responses.PolicyResponseV1
import com.kaiqkt.authentication.application.web.responses.toResponseV1
import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.services.PolicyService
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class PolicyController(
    private val policyService: PolicyService,
) {
    @PostMapping("/v1/resources/{resource_server_id}/policies")
    fun create(
        @PathVariable("resource_server_id") resourceServerId: String,
        @Valid @RequestBody requestV1: PolicyRequestV1,
    ): ResponseEntity<PolicyResponseV1> {
        val response = policyService.create(resourceServerId, requestV1.toDto()).toResponseV1()

        return ResponseEntity.ok(response)
    }

    @GetMapping("/v1/resources/{resource_server_id}/policies")
    fun findAllByResourceServerId(
        @PathVariable("resource_server_id") resourceServerId: String,
    ): ResponseEntity<List<PolicyResponseV1>> {
        val response = policyService.findAllByResourceId(resourceServerId).map { it.toResponseV1() }

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/v1/policies/{policy_id}")
    fun delete(
        @PathVariable("policy_id") policyId: String,
    ): ResponseEntity<Unit> {
        policyService.delete(policyId)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/v1/policies")
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
        @RequestParam("resource_server_id", required = false) resourceServerId: String,
    ): ResponseEntity<Page<PolicyResponseV1>> {
        val pageRequest = PageRequestDto(page, size, Sort.Direction.valueOf(sort), sortBy)
        val response = policyService.findAll(resourceServerId, pageRequest).map { it.toResponseV1() }

        return ResponseEntity.ok(response)
    }

    @PatchMapping("/v1/policies/{policy_id}/permissions/{permission_id}")
    fun associatePermission(
        @PathVariable("policy_id") policyId: String,
        @PathVariable("permission_id") permissionId: String,
    ): ResponseEntity<Unit> {
        policyService.associatePermission(policyId, permissionId)

        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/v1/policies/{policy_id}/roles/{role_id}")
    fun associateRole(
        @PathVariable("policy_id") policyId: String,
        @PathVariable("role_id") roleId: String,
    ): ResponseEntity<Unit> {
        policyService.associateRole(policyId, roleId)

        return ResponseEntity.noContent().build()
    }
}
