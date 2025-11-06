package com.kaiqkt.authentication.unit.domain.models

import com.kaiqkt.authentication.domain.models.Role
import com.kaiqkt.authentication.domain.models.User
import org.springframework.security.crypto.bcrypt.BCrypt

object UserSampler {
    fun sample(roles: MutableSet<Role> = mutableSetOf()): User =
        User(
            email = "kt@kt.com",
            roles = roles,
        ).apply {
            password = BCrypt.hashpw("strong-password", BCrypt.gensalt())
        }
}
