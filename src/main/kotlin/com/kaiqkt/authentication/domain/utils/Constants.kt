package com.kaiqkt.authentication.domain.utils

object Constants {
    object Keys {
        const val SID_KEY = "sid"
    }

    object Headers {
        const val BEARER = "Bearer "
    }

    object Sort {
        val COMMON_FIELDS = setOf("created_at", "updated_at")
    }
}