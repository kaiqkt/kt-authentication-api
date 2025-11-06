package com.kaiqkt.authentication.integration.web

import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.integration.IntegrationTest
import com.kaiqkt.authentication.unit.application.web.responses.PageResponse
import com.kaiqkt.authentication.unit.domain.models.ClientSampler
import com.kaiqkt.authentication.unit.domain.models.SessionSampler
import com.kaiqkt.authentication.unit.domain.models.UserSampler
import io.azam.ulidj.ULID
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SessionIntegrationTest : IntegrationTest() {
    @Test
    fun `given a session id and a user id when session exist should revoke successfully`() {
        val user = userRepository.save(UserSampler.sample())
        val client = clientRepository.save(ClientSampler.sample())
        val session = sessionRepository.save(SessionSampler.sample(client, user))

        given()
            .header("X-User-Id", user.id)
            .delete("/v1/sessions/${session.id}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a session id and a user id when session not exists should thrown an exception`() {
        val user = userRepository.save(UserSampler.sample())

        val response =
            given()
                .header("X-User-Id", user.id)
                .delete("/v1/sessions/${ULID.random()}")
                .then()
                .statusCode(404)
                .extract()
                .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.SESSION_NOT_FOUND.name, response.type)
        assertEquals(ErrorType.SESSION_NOT_FOUND.message, response.message)
    }

    @Test
    fun `given a session id when user id is not given should thrown an exception`() {
        val response =
            given()
                .delete("/v1/sessions/${ULID.random()}")
                .then()
                .statusCode(400)
                .extract()
                .`as`(ErrorV1::class.java)

        assertEquals("INVALID_REQUEST", response.type)
        assertEquals("Invalid request", response.message)
        assertEquals("required header", response.details["X-User-Id"])
    }

    @Test
    fun `given a user id and parameters should return sessions paginated successfully`() {
        val user = userRepository.save(UserSampler.sample())
        val client = clientRepository.save(ClientSampler.sample())
        sessionRepository.save(SessionSampler.sample(client, user))

        val response =
            given()
                .header("X-User-Id", user.id)
                .get("/v1/sessions")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .`as`(PageResponse::class.java)

        assertEquals(1, response.totalElements)
    }

    @Test
    fun `given a user id and parameters when sort by is invalid should thrown an exception`() {
        val response =
            given()
                .header("X-User-Id", ULID.random())
                .get("/v1/sessions?sort_by=invalid")
                .then()
                .statusCode(400)
                .extract()
                .response()
                .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_FIELD.name, response.type)
        assertEquals(ErrorType.INVALID_FIELD.message, response.message)
    }
}
