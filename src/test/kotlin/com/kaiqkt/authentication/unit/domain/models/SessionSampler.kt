package com.kaiqkt.authentication.unit.domain.models

import com.kaiqkt.authentication.domain.models.Client
import com.kaiqkt.authentication.domain.models.Session
import com.kaiqkt.authentication.domain.models.User
import java.time.LocalDateTime

object SessionSampler {
    fun sample(
        client: Client = Client(),
        user: User = UserSampler.sample(),
        expireAt: LocalDateTime = LocalDateTime.now().plusSeconds(300)
    ) = Session(
        user = user,
        refreshToken = "refresh-token",
        expireAt = expireAt,
        client = client
    )
}