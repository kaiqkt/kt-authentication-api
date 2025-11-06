package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.PermissionDto

object PermissionDtoSampler {
    fun sample() =
        PermissionDto(
            resource = "authentication",
            verb = "view",
            description = null,
        )
}
