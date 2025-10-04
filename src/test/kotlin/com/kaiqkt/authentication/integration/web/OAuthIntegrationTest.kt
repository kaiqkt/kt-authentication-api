package com.kaiqkt.authentication.integration.web

import com.kaiqkt.authentication.application.web.requests.LoginRequestV1
import com.kaiqkt.authentication.application.web.responses.AuthenticationTokenResponseV1
import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.application.web.responses.IntrospectResponseV1
import com.kaiqkt.authentication.application.web.responses.InvalidArgumentErrorV1
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.User
import com.kaiqkt.authentication.domain.utils.Constants
import com.kaiqkt.authentication.integration.IntegrationTest
import com.kaiqkt.authentication.unit.application.web.requests.AuthenticationTokenRequestV1Sampler
import com.kaiqkt.authentication.unit.application.web.requests.LoginRequestV1Sampler
import com.kaiqkt.authentication.unit.domain.models.SessionSampler
import com.kaiqkt.authentication.unit.domain.models.UserSampler
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.springframework.http.HttpHeaders
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OAuthIntegrationTest : IntegrationTest() {

    @Test
    fun `given a request to login with email and password when user does not exist should thrown an exception`(){
        val user = userRepository.save(UserSampler.sample())
        val request = LoginRequestV1Sampler.sample(user.email, "@Admin2345#")

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/oauth/login")
            .then()
            .statusCode(401)
            .extract()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_PASSWORD, response.type)
        assertEquals(ErrorType.INVALID_PASSWORD.message, response.message)
    }

    @Test
    fun `given a request to login with email and password when user exist but password does not match should thrown an exception`(){
        val request = LoginRequestV1Sampler.sample("", "")

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/oauth/login")
            .then()
            .statusCode(400)
            .extract()
            .`as`(InvalidArgumentErrorV1::class.java)

        assertEquals("must not be blank", response.errors["email"])
        assertEquals("must not be blank", response.errors["password"])
    }

    @Test
    fun `given a request to login with email and password when user exist and password does match should authenticate successfully`(){
        val user = userRepository.save(UserSampler.sample())
        val request = LoginRequestV1Sampler.sample(user.email, "@Admin12345#")

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/oauth/login")
            .then()
            .statusCode(200)
            .extract()
            .`as`(AuthenticationTokenResponseV1::class.java)

        assertEquals("bearer", response.tokenType)
        assertEquals(900, response.expiresIn)
    }

    @Test
    fun `given a request to authorize based on refresh token should return a pair of tokens successfully`() {
        val user = userRepository.save(UserSampler.sample())
        val session = sessionRepository.save(SessionSampler.sample(user))

        val request = AuthenticationTokenRequestV1Sampler.sample(
            refreshToken = session.refreshToken,
            grantType = "refresh_token"
        )

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/oauth/token")
            .then()
            .statusCode(200)
            .extract()
            .`as`(AuthenticationTokenResponseV1::class.java)

        assertEquals("bearer", response.tokenType)
        assertEquals(900, response.expiresIn)
    }

    @Test
    fun `given a request to authorize based on refresh token when not exist a session should thrown an exception`() {
        val request = AuthenticationTokenRequestV1Sampler.sample(
            refreshToken = "refresh-token",
            grantType = "refresh_token"
        )

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/oauth/token")
            .then()
            .statusCode(404)
            .extract()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.SESSION_NOT_FOUND, response.type)
    }

    @Test
    fun `given a request to authorize based on refresh token when request is invalid thrown an exception`() {
        val request = AuthenticationTokenRequestV1Sampler.sample(
            refreshToken = null,
            grantType = "refresh_token"
        )

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/oauth/token")
            .then()
            .statusCode(400)
            .extract()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_GRANT_TYPE_ARGUMENTS, response.type)
    }

    @Test
    fun `iven a request to introspect when session is not expired and not revoked should return the introspection data with active true`(){
        val user = userRepository.save(UserSampler.sample())
        val session = sessionRepository.save(SessionSampler.sample(user))
        val accessToken = getAccessToken(user.id, session.id, accessTokenSecret)

        val response = given()
            .contentType(ContentType.JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .get("/v1/oauth/introspect")
            .then()
            .statusCode(200)
            .extract()
            .`as`(IntrospectResponseV1::class.java)

        assertEquals(user.id, response.sub)
        assertEquals(session.id, response.sid)
        assertTrue { response.active }
    }

    @Test
    fun `given a request to introspect when session is expired and revoked should return the introspection data with active false`(){
        val user = userRepository.save(UserSampler.sample())
        val session = sessionRepository.save(SessionSampler.sample(user, LocalDateTime.now().minusSeconds(300)))
        val accessToken = getAccessToken(user.id, session.id, accessTokenSecret)

        val response = given()
            .contentType(ContentType.JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .get("/v1/oauth/introspect")
            .then()
            .statusCode(200)
            .extract()
            .`as`(IntrospectResponseV1::class.java)

        assertEquals(user.id, response.sub)
        assertEquals(session.id, response.sid)
        assertFalse { response.active }
    }

    @Test
    fun `given a request to introspect when access token has a invalid signature should thrown an exception`(){
        val user = userRepository.save(UserSampler.sample())
        val session = sessionRepository.save(SessionSampler.sample(user, LocalDateTime.now().minusSeconds(300)))
        val accessToken = getAccessToken(user.id, session.id, "i".repeat(256))

        val response = given()
            .contentType(ContentType.JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .get("/v1/oauth/introspect")
            .then()
            .statusCode(401)
            .extract()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_TOKEN, response.type)
    }

    @Test
    fun `given a request to introspect when access token has a invalid format should thrown an exception`(){
        val response = given()
            .contentType(ContentType.JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer access_token")
            .get("/v1/oauth/introspect")
            .then()
            .statusCode(401)
            .extract()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_TOKEN, response.type)
    }

    @Test
    fun `given a request to introspect when access token is expired should thrown an exception`(){
        val user = userRepository.save(UserSampler.sample())
        val session = sessionRepository.save(SessionSampler.sample(user, LocalDateTime.now().minusSeconds(300)))
        val accessToken = getAccessToken(user.id, session.id, accessTokenSecret, Instant.now().minusSeconds(300))

        val response = given()
            .contentType(ContentType.JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .get("/v1/oauth/introspect")
            .then()
            .statusCode(401)
            .extract()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.EXPIRED_TOKEN, response.type)
    }

    private fun getAccessToken(
        subject: String,
        sid: String,
        secret: String,
        exp: Instant = Instant.now().plusSeconds(accessTokenTll.toLong())
    ): String {
        val claims = JWTClaimsSet.Builder()
            .issuer(issuer)
            .subject(subject)
            .issueTime(Date.from(exp))
            .expirationTime(Date.from(exp))
            .claim(Constants.SID_KEY, sid)
            .claim(Constants.SCOPE_KEY, " ")
            .build()

        val header = JWSHeader.Builder(JWSAlgorithm.HS256)
            .type(JOSEObjectType.JWT)
            .build()

        val signedJWT = SignedJWT(header, claims).apply {
            sign(MACSigner(secret.toByteArray()))
        }

        return signedJWT.serialize()
    }
}