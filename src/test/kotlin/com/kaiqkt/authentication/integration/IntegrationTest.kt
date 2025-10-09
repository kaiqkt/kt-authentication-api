package com.kaiqkt.authentication.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.kaiqkt.authentication.domain.repositories.PermissionRepository
import com.kaiqkt.authentication.domain.repositories.ResourceServerRepository
import com.kaiqkt.authentication.domain.repositories.RoleRepository
import com.kaiqkt.authentication.domain.repositories.SessionRepository
import com.kaiqkt.authentication.domain.repositories.UserRepository
import io.restassured.RestAssured
import io.restassured.config.ObjectMapperConfig
import io.restassured.mapper.ObjectMapperType
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTest {

    @LocalServerPort
    var port: Int = 0

    @field:Value("\${authentication.issuer}")
    lateinit var issuer: String

    @field:Value("\${authentication.access-token-ttl}")
    lateinit var accessTokenTll: String
    @field:Value("\${authentication.access-token-secret}")
    lateinit var accessTokenSecret: String

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var sessionRepository: SessionRepository

    @Autowired
    lateinit var resourceServerRepository: ResourceServerRepository

    @Autowired
    lateinit var permissionRepository: PermissionRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @BeforeAll
    fun before() {
        RestAssured.config = RestAssured.config()
            .objectMapperConfig(
                ObjectMapperConfig(ObjectMapperType.JACKSON_2)
                    .jackson2ObjectMapperFactory { _, _ -> mapper }
            )
        RestAssured.baseURI = "http://localhost:$port"
    }

    @BeforeEach
    fun beforeEach() {
        resourceServerRepository.deleteAll()
        roleRepository.deleteAll()
        userRepository.deleteAll()
    }
}