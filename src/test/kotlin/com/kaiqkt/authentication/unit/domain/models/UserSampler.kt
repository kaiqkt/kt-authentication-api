package com.kaiqkt.authentication.unit.domain.models

import com.kaiqkt.authentication.domain.models.User
import org.springframework.security.crypto.bcrypt.BCrypt

object UserSampler {
    fun sample() = User(
        email = "kt@kt.com",
    ).apply {
        password = BCrypt.hashpw("strong-password", BCrypt.gensalt())
    }
}