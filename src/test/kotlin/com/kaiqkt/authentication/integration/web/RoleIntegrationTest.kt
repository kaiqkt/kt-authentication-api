package com.kaiqkt.authentication.integration.web

import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.application.web.responses.InvalidArgumentErrorV1
import com.kaiqkt.authentication.application.web.responses.RoleResponseV1
import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.integration.IntegrationTest
import com.kaiqkt.authentication.unit.application.web.requests.RoleRequestV1Sampler
import com.kaiqkt.authentication.unit.application.web.responses.PageResponse
import com.kaiqkt.authentication.unit.domain.models.PermissionSampler
import com.kaiqkt.authentication.unit.domain.models.ResourceServerSampler
import com.kaiqkt.authentication.unit.domain.models.RoleSampler
import io.azam.ulidj.ULID
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import kotlin.test.Test
import kotlin.test.assertEquals

class RoleIntegrationTest : IntegrationTest() {
    @Test
    fun `given a request should create successfully`() {
        val request = RoleRequestV1Sampler.sample()

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/roles")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(RoleResponseV1::class.java)

        assertEquals(request.name, response.name)
        assertEquals(request.description, response.description)
    }

    @Test
    fun `given a request when data is invalid should thrown an exception`() {
        val request = RoleRequestV1Sampler.sample(
            name = "",
            description = "d".repeat(256)
        )

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/roles")
            .then()
            .statusCode(400)
            .extract()
            .response()
            .`as`(InvalidArgumentErrorV1::class.java)

        assertEquals("must contains letter or underlines", response.errors["name"])
        assertEquals("must not exceed 255 characters", response.errors["description"])
    }


    @Test
    fun `given a request when already exists a role with same name should thrown an exception`() {
        val request = RoleRequestV1Sampler.sample()
        roleRepository.save(RoleSampler.sample())

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/roles")
            .then()
            .statusCode(409)
            .extract()
            .response()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.ROLE_ALREADY_EXISTS, response.type)
    }

    @Test
    fun `given a role id should delete successfully`() {
        given()
            .delete("/v1/roles/${ULID.random()}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a request to find all roles should return paginated successfully`() {
        roleRepository.save(RoleSampler.sample())

        val response = given()
            .get("/v1/roles")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(PageResponse::class.java)

        assertEquals(1, response.totalElements)
    }

    @Test
    fun `given a request to find all roles when sort by field is invalid should thrown an exception`() {
        val response = given()
            .get("/v1/roles?sort_by=invalid")
            .then()
            .statusCode(400)
            .extract()
            .response()
            .`as`(DomainException::class.java)

        assertEquals(ErrorType.INVALID_SORT_FIELD, response.type)
    }

    @Test
    fun `given a role id and a permission id should associate successfully`() {
        val role = roleRepository.save(RoleSampler.sample())
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val permission = permissionRepository.save(PermissionSampler.sample(resourceServer = resourceServer))

        given()
            .patch("/v1/roles/${role.id}/associate/${permission.id}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a role id and a permission id to associate when role does not exists should thrown an exception`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val permission = permissionRepository.save(PermissionSampler.sample(resourceServer = resourceServer))

        val response = given()
            .patch("/v1/roles/${ULID.random()}/associate/${permission.id}")
            .then()
            .statusCode(404)
            .extract()
            .response()
            .`as`(DomainException::class.java)

        assertEquals(ErrorType.ROLE_NOT_FOUND, response.type)
    }

    @Test
    fun `given a role id and a permission id to associate when permission does not exists should thrown an exception`() {
        val response = given()
            .patch("/v1/roles/${ULID.random()}/associate/${ULID.random()}")
            .then()
            .statusCode(404)
            .extract()
            .response()
            .`as`(DomainException::class.java)

        assertEquals(ErrorType.PERMISSION_NOT_FOUND, response.type)
    }

    @Test
    fun `given a role id when exists should return a role successfully`(){
        val role = roleRepository.save(RoleSampler.sample())

        val response = given()
            .get("/v1/roles/${role.id}")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(RoleResponseV1::class.java)

        assertEquals(role.id, response.id)
    }


    @Test
    fun `given a role id and a permission id should disassociate successfully`() {
        val role = roleRepository.save(RoleSampler.sample())
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val permission = permissionRepository.save(PermissionSampler.sample(resourceServer = resourceServer))

        given()
            .patch("/v1/roles/${role.id}/associate/${permission.id}")
            .then()
            .statusCode(204)
    }
}