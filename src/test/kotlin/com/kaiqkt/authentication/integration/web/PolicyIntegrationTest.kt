package com.kaiqkt.authentication.integration.web

import com.kaiqkt.authentication.application.web.responses.ErrorV1
import com.kaiqkt.authentication.application.web.responses.PolicyResponseV1
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.integration.IntegrationTest
import com.kaiqkt.authentication.unit.application.web.requests.PolicyRequestV1Sampler
import com.kaiqkt.authentication.unit.application.web.responses.PageResponse
import com.kaiqkt.authentication.unit.domain.models.PermissionSampler
import com.kaiqkt.authentication.unit.domain.models.PolicySampler
import com.kaiqkt.authentication.unit.domain.models.ResourceServerSampler
import com.kaiqkt.authentication.unit.domain.models.RoleSampler
import io.azam.ulidj.ULID
import io.restassured.RestAssured.given
import io.restassured.common.mapper.TypeRef
import io.restassured.http.ContentType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class PolicyIntegrationTest : IntegrationTest() {

    @Test
    fun `given a request should create successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val request = PolicyRequestV1Sampler.sample()

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/resources/${resourceServer.id}/policies")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(PolicyResponseV1::class.java)

        assertEquals(request.uri, response.uri)
        assertEquals(request.method, response.method.name)
        assertEquals(request.isPublic, response.isPublic)
    }

    @Test
    fun `given a request when data is invalid should thrown an exception`() {
        val request = PolicyRequestV1Sampler.sample(
            uri = "",
            method = "invalid"
        )

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/resources/${ULID.random()}/policies")
            .then()
            .statusCode(400)
            .extract()
            .response()
            .`as`(ErrorV1::class.java)

        assertEquals("INVALID_REQUEST", response.type)
        assertEquals("Invalid request", response.message)
        assertEquals("must not be blank", response.details["uri"])
        assertEquals("must be POST, GET, DELETE, PUT or PATCH", response.details["method"])
    }

    @Test
    fun `given a request when already exists a policy with same name should thrown an exception`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        policyRepository.save(PolicySampler.sample(resourceServer))
        val request = PolicyRequestV1Sampler.sample()

        val response = given()
            .contentType(ContentType.JSON)
            .body(request)
            .post("/v1/resources/${resourceServer.id}/policies")
            .then()
            .statusCode(409)
            .extract()
            .response()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.POLICY_ALREADY_EXISTS.name, response.type)
        assertEquals(ErrorType.POLICY_ALREADY_EXISTS.message, response.message)
    }

    @Test
    fun `given a resource server id should return all policies successfully`(){
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        policyRepository.save(PolicySampler.sample(resourceServer))

        val response = given()
            .get("/v1/resources/${resourceServer.id}/policies")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(object : TypeRef<List<PolicyResponseV1>>(){})

        assertEquals(1, response.size)
    }

    @Test
    fun `given a policy id should delete successfully`() {
        given()
            .delete("/v1/policies/${ULID.random()}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a request to find all policies should return paginated successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        policyRepository.save(PolicySampler.sample(resourceServer))

        val response = given()
            .get("/v1/policies?resource_server_id=${resourceServer.id}")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(PageResponse::class.java)

        assertEquals(1, response.totalElements)
    }

    @Test
    fun `given a request to find all policies when sort by field is invalid should thrown an exception`() {
        val response = given()
            .get("/v1/policies?sort_by=invalid&resource_server_id=${ULID.random()}")
            .then()
            .statusCode(400)
            .extract()
            .response()
            .`as`(ErrorV1::class.java)

        assertEquals(ErrorType.INVALID_FIELD.name, response.type)
        assertEquals(ErrorType.INVALID_FIELD.message, response.message)
    }

    @Test
    fun `given a policy id and a permission id should associate successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val policy = policyRepository.save(PolicySampler.sample(resourceServer))
        val permission = permissionRepository.save(PermissionSampler.sample(resourceServer = resourceServer))

        given()
            .patch("/v1/policies/${policy.id}/permissions/${permission.id}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a policy id and a role id should associate successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val policy = policyRepository.save(PolicySampler.sample(resourceServer))
        val role = roleRepository.save(RoleSampler.sample())

        given()
            .patch("/v1/policies/${policy.id}/roles/${role.id}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a policy id and a permission id should disassociate successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val permission = permissionRepository.save(PermissionSampler.sample(resourceServer = resourceServer))
        val policy = policyRepository.save(PolicySampler.sample(resourceServer).apply { permissions.add(permission) })

        given()
            .patch("/v1/policies/${policy.id}/permissions/${permission.id}")
            .then()
            .statusCode(204)
    }

    @Test
    fun `given a policy id and a role id should disassociate successfully`() {
        val resourceServer = resourceServerRepository.save(ResourceServerSampler.sample())
        val role = roleRepository.save(RoleSampler.sample())
        val policy = policyRepository.save(PolicySampler.sample(resourceServer).apply { roles.add(role) })

        given()
            .patch("/v1/policies/${policy.id}/roles/${role.id}")
            .then()
            .statusCode(204)
    }
}