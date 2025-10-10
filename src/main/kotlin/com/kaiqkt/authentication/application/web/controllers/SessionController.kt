package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.application.web.responses.SessionResponseV1
import com.kaiqkt.authentication.application.web.responses.toResponse
import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import com.kaiqkt.authentication.domain.services.SessionService
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/sessions")
@Validated
class SessionController(
    private val sessionService: SessionService
) {
    @DeleteMapping("/{session_id}")
    fun revokeById(
        @RequestHeader("X-User-Id") userId: String,
        @PathVariable("session_id") sessionId: String
    ): ResponseEntity<Unit> {
        sessionService.revoke(sessionId, userId)

        return ResponseEntity.noContent().build()
    }

    @GetMapping
    fun findAllByUserId(
        @RequestHeader("X-User-Id") userId: String,

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
    ): ResponseEntity<Page<SessionResponseV1>> {
        val pageRequest = PageRequestDto(page, size, Sort.Direction.valueOf(sort), sortBy)
        val response = sessionService.findAllByUserId(userId, pageRequest).map { it.toResponse() }

        return ResponseEntity.ok(response)
    }
}