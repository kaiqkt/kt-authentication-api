package com.kaiqkt.authentication.integration.web

import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.application.web.responses.InvalidArgumentErrorV1
import com.kaiqkt.authentication.application.web.responses.UserResponseV1
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.enums.AuthenticationType
import com.kaiqkt.authentication.integration.IntegrationTest
import com.kaiqkt.authentication.unit.application.web.requests.UserRequestV1Sampler
import com.kaiqkt.authentication.unit.domain.models.RoleSampler
import com.kaiqkt.authentication.unit.domain.models.UserSampler
import io.azam.ulidj.ULID
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class UserIntegrationTest : IntegrationTest() {

    @Test
    fun `given a request to create a user should create successfully`() {
        val request = UserRequestV1Sampler.sample()

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/users")
            .then()
            .statusCode(200)
            .extract()
            .`as`(UserResponseV1::class.java)

        assertEquals(request.email, response.email)
        assertFalse { response.isVerified }
        assertEquals(AuthenticationType.PASSWORD, response.authenticationType)
    }

    @Test
    fun `given a request to create a user when is invalid should thrown an exception`() {
        val request = UserRequestV1Sampler.sample(email = "kt", password = "1")

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/users")
            .then()
            .statusCode(400)
            .extract()
            .`as`(InvalidArgumentErrorV1::class.java)

        assertEquals("must be a valid email", response.errors["email"])
        assertEquals("must be at least 8 characters long and include at least one letter, one special character, and one number", response.errors["password"])
    }

    @Test
    fun `given a request to create a user when email is already in use should thrown an exception`() {
        userRepository.save(UserSampler.sample())
        val request = UserRequestV1Sampler.sample()

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/users")
            .then()
            .statusCode(409)
            .extract()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.EMAIL_ALREADY_IN_USE, response.type)
    }

    @Test
    fun `given a user id and role id should assign user a role successfully`(){
       val user = userRepository.save(UserSampler.sample())
        val role = roleRepository.save(RoleSampler.sample())

        given()
            .patch("/v1/users/${user.id}/role/${role.id}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a user id and role id when user does not found should thrown an exception`(){
        val role = roleRepository.save(RoleSampler.sample())

        val response = given()
            .patch("/v1/users/${ULID.random()}/role/${role.id}")
            .then()
            .statusCode(404)
            .extract()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.USER_NOT_FOUND, response.type)
    }

    @Test
    fun `given a user id and role id when role does not found should thrown an exception`(){
        val response = given()
            .patch("/v1/users/${ULID.random()}/role/${ULID.random()}")
            .then()
            .statusCode(404)
            .extract()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.ROLE_NOT_FOUND, response.type)
    }
}