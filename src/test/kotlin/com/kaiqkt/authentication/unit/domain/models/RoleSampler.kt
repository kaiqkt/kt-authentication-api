package com.kaiqkt.authentication.unit.domain.models

import com.kaiqkt.authentication.domain.models.Role

object RoleSampler {
    fun sample() =
        Role(
            name = "ROLE_USER",
        )
}
