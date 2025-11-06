package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.application.web.requests.UserRequestV1
import com.kaiqkt.authentication.application.web.requests.toDto
import com.kaiqkt.authentication.application.web.responses.UserResponseV1
import com.kaiqkt.authentication.application.web.responses.toResponseV1
import com.kaiqkt.authentication.domain.services.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/v1/users")
    fun create(
        @Valid @RequestBody userRequestV1: UserRequestV1,
    ): ResponseEntity<UserResponseV1> {
        val user = userService.create(userRequestV1.toDto())

        return ResponseEntity.ok(user.toResponseV1())
    }

    @PatchMapping("/v1/users/{user_id}/roles/{role_id}")
    fun assignRole(
        @PathVariable("user_id") userId: String,
        @PathVariable("role_id") roleId: String,
    ): ResponseEntity<Unit> {
        userService.assignRole(userId, roleId)

        return ResponseEntity.noContent().build()
    }
}
