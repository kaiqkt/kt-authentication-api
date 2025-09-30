package com.kaiqkt.authentication.application.web.controllers

import com.kaiqkt.authentication.application.web.requests.UserRequestV1
import com.kaiqkt.authentication.application.web.requests.toDto
import com.kaiqkt.authentication.application.web.responses.UserResponseV1
import com.kaiqkt.authentication.application.web.responses.toResponseV1
import com.kaiqkt.authentication.domain.services.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/user")
@Validated
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun create(@Valid @RequestBody userRequestV1: UserRequestV1): ResponseEntity<UserResponseV1> {
        val user = userService.create(userRequestV1.toDto())

        return ResponseEntity.ok(user.toResponseV1())
    }
}