package com.kaiqkt.authentication.unit.domain.dtos

import com.kaiqkt.authentication.domain.dtos.UserDto

object UserDtoSampler {
    fun sampleCreate() = UserDto.Create("kt@kt.com", "12345")
}
