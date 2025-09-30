package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.repositories.AuthorizationCodeRepository
import com.kaiqkt.authentication.domain.services.AuthorizationService
import com.kaiqkt.authentication.domain.services.UserService
import com.kaiqkt.authentication.unit.domain.dtos.AuthorizationCodeDtoSampler
import com.kaiqkt.authentication.unit.domain.models.AuthorizationCodeSampler
import com.kaiqkt.authentication.unit.domain.models.UserSampler
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals

class AuthorizationServiceTest {
    private val authorizationRepository = mockk<AuthorizationCodeRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val userService = mockk<UserService>()
    private val codeTtl = 300L
    private val authorizationService = AuthorizationService(
        authorizationRepository,
        passwordEncoder,
        userService,
        codeTtl
    )

    @Test
    fun `given a request to authorize when password matches should create a authorization code successfully`() {
        val codeDto = AuthorizationCodeDtoSampler.sampleCreate()

        every { userService.findByEmailAndType(any(), any()) } returns UserSampler.sample()
        every { passwordEncoder.matches(any(), any()) } returns true
        every { authorizationRepository.save(any()) } returns AuthorizationCodeSampler.sample()

        authorizationService.create(codeDto)

        verify { userService.findByEmailAndType(any(), any()) }
        verify { passwordEncoder.matches(any(), any()) }
        verify { authorizationRepository.save(any()) }
    }

    @Test
    fun `given a request to authorize when password not matches should throw an exception`() {
        val codeDto = AuthorizationCodeDtoSampler.sampleCreate()

        every { userService.findByEmailAndType(any(), any()) } returns UserSampler.sample()
        every { passwordEncoder.matches(any(), any()) } returns false

        val exception = assertThrows<DomainException> {
            authorizationService.create(codeDto)
        }

        verify { userService.findByEmailAndType(any(), any()) }
        verify { passwordEncoder.matches(any(), any()) }

        assertEquals(ErrorType.INVALID_PASSWORD, exception.type)
    }

    @Test
    fun `given a code and a redirect uri when exist a authorization code should return successfully`(){
        every { authorizationRepository.findByCode(any(), any()) } returns AuthorizationCodeSampler.sample()

        authorizationService.findByCodeAndRedirectUri("code", "http://localhost:8080")

        verify { authorizationService.findByCodeAndRedirectUri(any(), any()) }
    }

    @Test
    fun `given a code and a redirect uri when not exist a authorization code should throw an exception`(){
        every { authorizationRepository.findByCode(any(), any()) } returns null

        val exception = assertThrows<DomainException> {
            authorizationService.findByCodeAndRedirectUri("code", "http://localhost:8080")
        }

        verify { authorizationService.findByCodeAndRedirectUri(any(), any()) }

        assertEquals(ErrorType.AUTHORIZATION_CODE_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a code should delete successfully`(){
        justRun { authorizationRepository.deleteByCode(any()) }

        authorizationService.deleteByCode("code")

        verify { authorizationRepository.deleteByCode(any()) }
    }
}