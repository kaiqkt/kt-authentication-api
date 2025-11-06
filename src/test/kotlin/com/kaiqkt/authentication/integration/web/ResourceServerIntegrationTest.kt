package com.kaiqkt.authentication.integration.web

import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.application.web.responses.ResourceServerResponseV1
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.integration.IntegrationTest
import com.kaiqkt.authentication.unit.application.web.requests.ResourceServerRequestV1Sampler
import com.kaiqkt.authentication.unit.application.web.responses.PageResponse
import com.kaiqkt.authentication.unit.domain.models.ResourceServerSampler
import io.azam.ulidj.ULID
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceServerIntegrationTest : IntegrationTest() {
    @Test
    fun `given a request to create a resource server when is valid should create successfully`() {
        val request = ResourceServerRequestV1Sampler.sample()

        val response =
            given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/v1/resources")
                .then()
                .statusCode(200)
                .extract()
                .`as`(ResourceServerResponseV1::class.java)

        assertEquals(request.name, response.name)
        assertEquals(request.description, response.description)
    }

    @Test
    fun `given a request to create a resource server when payload is invalid should thrown an exception`() {
        val request =
            ResourceServerRequestV1Sampler.sample(
                name = "",
                description = "d".repeat(256),
            )

        val response =
            given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/v1/resources")
                .then()
                .statusCode(400)
                .extract()
                .`as`(ErrorV1::class.java)

        assertEquals("INVALID_REQUEST", response.type)
        assertEquals("Invalid request", response.message)
        assertEquals("input should not be blank", response.details["name"])
        assertEquals("must not exceed 255 characters", response.details["description"])
    }

    @Test
    fun `given a resource id should delete successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())

        given()
            .delete("/v1/resources/${resourceServer.id}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given parameters should find resource servers successfully`() {
        resourceServerRepository.save(ResourceServerSampler.sample())

        val response =
            given()
                .get("/v1/resources?page=0&size=1&sort=ASC")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .`as`(PageResponse::class.java)

        assertEquals(1, response.totalElements)
    }

    @Test
    fun `given parameters when sort_by field is invalid should thrown an exception`() {
        resourceServerRepository.save(ResourceServerSampler.sample())

        val response =
            given()
                .get("/v1/resources?sort_by=invalid")
                .then()
                .statusCode(400)
                .extract()
                .response()
                .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_FIELD.name, response.type)
        assertEquals(ErrorType.INVALID_FIELD.message, response.message)
    }

    @Test
    fun `given parameters when sort is invalid should thrown an exception`() {
        resourceServerRepository.save(ResourceServerSampler.sample())

        val response =
            given()
                .get("/v1/resources?sort=invalid")
                .then()
                .statusCode(400)
                .extract()
                .response()
                .`as`(ErrorV1::class.java)

        assertEquals("INVALID_REQUEST", response.type)
        assertEquals("Invalid request", response.message)
        assertEquals("sort should be ASC or DESC", response.details["findAll.sort"])
    }

    @Test
    fun `given a resource id when found a resource server should return successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())

        val response =
            given()
                .get("/v1/resources/${resourceServer.id}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(ResourceServerResponseV1::class.java)

        assertEquals(resourceServer.id, response.id)
        assertEquals(resourceServer.name, response.name)
        assertEquals(resourceServer.description, response.description)
    }

    @Test
    fun `given a resource id when not found a resource server should thrown an exception`() {
        val response =
            given()
                .get("/v1/resources/${ULID.random()}")
                .then()
                .statusCode(404)
                .extract()
                .response()
                .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.RESOURCE_SERVER_NOT_FOUND.name, response.type)
        assertEquals(ErrorType.RESOURCE_SERVER_NOT_FOUND.message, response.message)
    }
}
