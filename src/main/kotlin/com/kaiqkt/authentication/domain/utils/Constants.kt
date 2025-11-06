package com.kaiqkt.authentication.domain.utils

import org.slf4j.MDC
import java.util.UUID

object Constants {
    object Parameters {
        val requestId = MDC.get("request_id") ?: UUID.randomUUID().toString()
    }

    object Keys {
        const val SID = "sid"
        const val ROLES = "roles"
        const val PERMISSIONS = "permissions"
    }

    object Headers {
        const val BEARER = "Bearer "
    }

    object Sort {
        fun getAllowedFiled(vararg fields: String): Set<String> = setOf("created_at", "updated_at").plus(fields)
    }
}
