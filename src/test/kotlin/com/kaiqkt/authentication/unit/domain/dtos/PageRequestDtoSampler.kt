package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.PageRequestDto
import org.springframework.data.domain.Sort

object PageRequestDtoSampler {
    fun sample(sortBy: String? = null) =
        PageRequestDto(
            page = 0,
            size = 20,
            sort = Sort.Direction.DESC,
            sortBy = sortBy,
        )
}
