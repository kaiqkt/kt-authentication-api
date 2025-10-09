package com.kaiqkt.authentication.integration.web

import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.application.web.responses.InvalidArgumentErrorV1
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.integration.IntegrationTest
import com.kaiqkt.authentication.unit.domain.models.SessionSampler
import com.kaiqkt.authentication.unit.domain.models.UserSampler
import io.azam.ulidj.ULID
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SessionIntegrationTest : IntegrationTest(){

    @Test
    fun `given a session id and a user id when session exist should revoke successfully`(){
        val user = userRepository.save(UserSampler.sample())
        val session = sessionRepository.save(SessionSampler.sample(user))

        given()
            .header("X-User-Id", user.id)
            .delete("/v1/sessions/${session.id}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a session id and a user id when session not exists should thrown an exception`(){
        val user = userRepository.save(UserSampler.sample())

       val response = given()
            .header("X-User-Id", user.id)
            .delete("/v1/sessions/${ULID.random()}")
            .then()
            .statusCode(404)
           .extract()
           .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.SESSION_NOT_FOUND, response.type)
    }

    @Test
    fun `given a session id when user id is not given should thrown an exception`(){
        val response = given()
            .delete("/v1/sessions/${ULID.random()}")
            .then()
            .statusCode(400)
            .extract()
            .`as`(InvalidArgumentErrorV1::class.java)

        assertEquals("required header", response.errors["X-User-Id"])
    }
}