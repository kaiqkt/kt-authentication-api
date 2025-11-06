package com.kaiqkt.authentication.integration.web

import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.application.web.responses.PermissionResponseV1
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.integration.IntegrationTest
import com.kaiqkt.authentication.unit.application.web.requests.PermissionRequestV1Sampler
import com.kaiqkt.authentication.unit.application.web.responses.PageResponse
import com.kaiqkt.authentication.unit.domain.models.PermissionSampler
import com.kaiqkt.authentication.unit.domain.models.ResourceServerSampler
import io.azam.ulidj.ULID
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PermissionIntegrationTest : IntegrationTest() {
    @Test
    fun `given a permission to create when is valid should create successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val request = PermissionRequestV1Sampler.sample()

        val response =
            given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/v1/resources/${resourceServer.id}/permissions")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .`as`(PermissionResponseV1::class.java)

        assertEquals(request.resource, response.resource)
        assertEquals(request.verb, response.verb)
        assertEquals(request.description, response.description)
    }

    @Test
    fun `given a permission to create when request is invalid should thrown an exception`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val request =
            PermissionRequestV1Sampler.sample(
                resource = "",
                verb = "",
                description = "d".repeat(256),
            )

        val response =
            given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/v1/resources/${resourceServer.id}/permissions")
                .then()
                .statusCode(400)
                .extract()
                .response()
                .`as`(ErrorV1::class.java)

        assertEquals("INVALID_REQUEST", response.type)
        assertEquals("Invalid request", response.message)
        assertEquals("must contains letter or underlines", response.details["resource"])
        assertEquals("must contains letter or underlines", response.details["verb"])
        assertEquals("must not exceed 255 characters", response.details["description"])
    }

    @Test
    fun `given a permission to create when resource and verb already exists should thrown an exception`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        permissionRepository.save(PermissionSampler.sample(resourceServer = resourceServer))

        val request = PermissionRequestV1Sampler.sample()

        val response =
            given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/v1/resources/${resourceServer.id}/permissions")
                .then()
                .statusCode(409)
                .extract()
                .response()
                .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.PERMISSION_ALREADY_EXISTS.name, response.type)
        assertEquals(ErrorType.PERMISSION_ALREADY_EXISTS.message, response.message)
    }

    @Test
    fun `given a permission to create when resource server does not exists should thrown an exception`() {
        val request = PermissionRequestV1Sampler.sample()

        val response =
            given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/v1/resources/${ULID.random()}/permissions")
                .then()
                .statusCode(404)
                .extract()
                .response()
                .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.RESOURCE_SERVER_NOT_FOUND.name, response.type)
        assertEquals(ErrorType.RESOURCE_SERVER_NOT_FOUND.message, response.message)
    }

    @Test
    fun `given parameters should find permissions successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        permissionRepository.save(PermissionSampler.sample(resourceServer = resourceServer))

        val response =
            given()
                .get("/v1/permissions?page=0&size=1&sort=ASC")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .`as`(PageResponse::class.java)

        assertEquals(1, response.totalElements)
    }

    @Test
    fun `given parameters and resource id should find permissions successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        permissionRepository.save(PermissionSampler.sample(resourceServer = resourceServer))

        val response =
            given()
                .get("/v1/permissions?page=0&size=1&sort=ASC&resource_server_id=${resourceServer.id}")
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
                .get("/v1/permissions?sort_by=invalid")
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
                .get("/v1/permissions?sort=invalid")
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
    fun `given a permission id should delete permission successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val permission = permissionRepository.save(PermissionSampler.sample(resourceServer = resourceServer))

        given()
            .delete("/v1/permissions/${permission.id}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a permission id when permission exists should return successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val permission = permissionRepository.save(PermissionSampler.sample(resourceServer = resourceServer))

        val response =
            given()
                .get("/v1/permissions/${permission.id}")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .`as`(PermissionResponseV1::class.java)

        assertEquals(permission.id, response.id)
    }

    @Test
    fun `given a permission id when permission does exists should thrown a exception`() {
        val response =
            given()
                .get("/v1/permissions/${ ULID.random()}")
                .then()
                .statusCode(404)
                .extract()
                .response()
                .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.PERMISSION_NOT_FOUND.name, response.type)
        assertEquals(ErrorType.PERMISSION_NOT_FOUND.message, response.message)
    }
}
