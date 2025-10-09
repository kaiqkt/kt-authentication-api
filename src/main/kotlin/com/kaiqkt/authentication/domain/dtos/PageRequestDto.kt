package com.kaiqkt.authentication.domain.dtos

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class PageRequestDto(
    val page: Int,
    val size: Int,
    val sort: Sort.Direction,
    val sortBy: String?
) {
    fun isValid(allowedSortFields: Set<String>): Boolean = this.sortBy == null || allowedSortFields.contains(this.sortBy)

    fun toDomain(): PageRequest {
        if (this.sortBy != null) {
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
