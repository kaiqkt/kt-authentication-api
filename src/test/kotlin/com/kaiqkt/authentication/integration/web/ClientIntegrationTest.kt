package com.kaiqkt.authentication.integration.web

import com.kaiqkt.authentication.application.web.responses.ClientResponseV1
import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.integration.IntegrationTest
import com.kaiqkt.authentication.unit.application.web.requests.ClientRequestV1Sampler
import com.kaiqkt.authentication.unit.application.web.responses.PageResponse
import com.kaiqkt.authentication.unit.domain.models.ClientSampler
import com.kaiqkt.authentication.unit.domain.models.PolicySampler
import com.kaiqkt.authentication.unit.domain.models.ResourceServerSampler
import io.azam.ulidj.ULID
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientIntegrationTest : IntegrationTest() {
    @Test
    fun `given a client should create successfully`(){
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val policy = policyRepository.save(PolicySampler.sample(resourceServer))
        val request = ClientRequestV1Sampler.sample(policies = setOf(policy.id))

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/clients")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(ClientResponseV1::class.java)

        assertEquals(request.name, response.name)
        assertEquals(request.description, response.description)
        assertEquals(request.policies, response.policies.map { it.id }.toSet())
    }

    @Test
    fun `given a client when none of the associate resource servers exists should thrown an exception`(){
        val request = ClientRequestV1Sampler.sample(policies = setOf(ULID.random()))

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/clients")
            .then()
            .statusCode(404)
            .extract()
            .response()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.POLICY_NOT_FOUND.name, response.type)
        assertEquals(ErrorType.POLICY_NOT_FOUND.message, response.message)
    }

    @Test
    fun `given a client when request is invalid should thrown an exception`(){
        val request = ClientRequestV1Sampler.sample(
            name = "",
            description = "a".repeat(256),
            policies = setOf()
        )

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/clients")
            .then()
            .statusCode(400)
            .extract()
            .response()
            .`as`(ErrorV1::class.java)

        assertEquals("INVALID_REQUEST", response.type)
        assertEquals("Invalid request", response.message)
        assertEquals("must not be blank", response.details["name"])
        assertEquals("must not exceed 255 characters", response.details["description"])
        assertEquals("must not be empty", response.details["policies"])
    }

    @Test
    fun `given a client id should delete successfully`(){
        given()
            .contentType(ContentType.JSON)
            .delete("/v1/clients/${ULID.random()}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a client id when exist should return successfully`(){
        val client = clientRepository.save(ClientSampler.sample(policies = mutableSetOf()))

        val response = given()
            .contentType(ContentType.JSON)
            .get("/v1/clients/${client.id}")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(ClientResponseV1::class.java)

        assertEquals(client.id, response.id)
    }

    @Test
    fun `given a client id when does not exist should thrown an exception`(){
        val response = given()
            .contentType(ContentType.JSON)
            .get("/v1/clients/${ULID.random()}")
            .then()
            .statusCode(404)
            .extract()
            .response()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.CLIENT_NOT_FOUND.name, response.type)
        assertEquals(ErrorType.CLIENT_NOT_FOUND.message, response.message)
    }

    @Test
    fun `given parameters should return clients paginated`(){
        clientRepository.save(ClientSampler.sample(policies = mutableSetOf()))

        val response = given()
            .contentType(ContentType.JSON)
            .get("/v1/clients")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(PageResponse::class.java)

        assertEquals(1, response.totalElements)
    }

    @Test
    fun `given parameters when sort by field is invalid should thrown an exception`(){
        val response = given()
            .contentType(ContentType.JSON)
            .get("/v1/clients?sort_by=invalid")
            .then()
            .statusCode(400)
            .extract()
            .response()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_FIELD.name, response.type)
        assertEquals(ErrorType.INVALID_FIELD.message, response.message)
    }
}