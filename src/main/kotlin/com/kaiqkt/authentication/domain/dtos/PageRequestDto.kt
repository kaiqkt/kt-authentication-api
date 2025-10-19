package com.kaiqkt.authentication.domain.dtos

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class PageRequestDto(
    val page: Int,
    val size: Int,
    val sort: Sort.Direction,
    val sortBy: String?
) {
    fun toDomain(allowedFields: Set<String>): PageRequest {
        if (this.sortBy != null) {
            require(allowedFields.contains(this.sortBy))

            return PageRequest.of(
                this.page,
                this.size,
                Sort.by(this.sort, this.sortBy)
            )
        }

        return PageRequest.of(
            this.page,
            this.size
        )
    }
}
