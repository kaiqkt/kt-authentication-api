package com.kaiqkt.authentication.domain.utils

import java.security.MessageDigest
import java.util.Base64

object EncryptHelper {
    fun encrypt(string: String): String{
        val shA256Digest = MessageDigest.getInstance(Constants.SHA_256).digest(string.toByteArray())

        return  Base64.getUrlEncoder().withoutPadding().encodeToString(shA256Digest)
    }
}