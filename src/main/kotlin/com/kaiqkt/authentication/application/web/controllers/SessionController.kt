package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.domain.services.SessionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/session")
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
}