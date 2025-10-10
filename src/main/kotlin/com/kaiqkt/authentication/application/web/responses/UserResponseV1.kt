package com.kaiqkt.authentication.application.web.responses

import com.kaiqkt.authentication.domain.models.User
import com.kaiqkt.authentication.domain.models.enums.AuthenticationType

data class UserResponseV1(
    val id: String,
    val email: String,
    val isVerified: Boolean,
    val authenticationType: AuthenticationType
)

fun User.toResponseV1(): UserResponseV1 = UserResponseV1(
    id = this.id,
    email = this.email,
    isVerified = this.isVerified,
    authenticationType = this.authenticationType
)