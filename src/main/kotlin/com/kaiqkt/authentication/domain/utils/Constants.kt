package com.kaiqkt.authentication.domain.utils

object Constants {
    object Keys {
        const val SID = "sid"
        const val ROLES = "roles"
        const val PERMISSIONS = "permissions"
    }

    object Headers {
        const val BEARER = "Bearer "
    }

    object Sort {
        fun getAllowedFiled(vararg fields: String): Set<String> {
            return setOf("created_at", "updated_at").plus(fields)
        }
    }
}