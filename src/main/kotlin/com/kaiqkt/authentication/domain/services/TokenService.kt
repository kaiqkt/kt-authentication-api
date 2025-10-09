package com.kaiqkt.authentication.domain.services

import com.kaiqkt.authentication.domain.dtos.AuthenticationDto
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.utils.Constants
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.text.ParseException
import java.time.Instant
import java.util.*

@Service
class TokenService(
    @param:Value("\${authentication.issuer}")
    private val issuer: String,
    @param:Value("\${authentication.access-token-ttl}")
    private val accessTokenTll: Long,
    @param:Value("\${authentication.access-token-secret}")
    private val accessTokenSecret: String
) {
    private val secureRandom = SecureRandom()

    fun issueTokens(
        subject: String,
        sid: String
    ): AuthenticationDto {
        val accessToken = signToken(subject, accessTokenTll, sid)
        val refreshToken = opaqueToken()

        return AuthenticationDto(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = accessTokenTll
        )
    }

    fun getClaims(token: String): JWTClaimsSet {
        try {
            val signedJWT = SignedJWT.parse(token)

            val verifier = MACVerifier(accessTokenSecret.toByteArray())
            if (!signedJWT.verify(verifier)) {
                throw DomainException(ErrorType.INVALID_TOKEN)
            }
            val jwtClaimsSet = signedJWT.jwtClaimsSet

            if (jwtClaimsSet.expirationTime.before(Date())) {
                throw DomainException(ErrorType.EXPIRED_TOKEN)
            }

            return jwtClaimsSet
        } catch (_: ParseException) {
            throw DomainException(ErrorType.INVALID_TOKEN)
        }
    }

    private fun signToken(
        subject: String,
        ttl: Long,
        sid: String,
        //roles
        //permissions
    ): String {
        val now = Instant.now()

        val claims = JWTClaimsSet.Builder()
            .issuer(issuer)
            .subject(subject)
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plusSeconds(ttl)))
            .claim(Constants.Keys.SID_KEY, sid)
            .build()

        val header = JWSHeader.Builder(JWSAlgorithm.HS256)
            .type(JOSEObjectType.JWT)
            .build()

        val signedJWT = SignedJWT(header, claims).apply {
            sign(MACSigner(accessTokenSecret.toByteArray()))
        }

        return signedJWT.serialize()
    }

    private fun opaqueToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}