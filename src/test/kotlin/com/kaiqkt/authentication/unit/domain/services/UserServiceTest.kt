package com.kaiqkt.authentication.unit.domain.services

import com.kaiqkt.authentication.domain.exceptions.DomainException
import com.kaiqkt.authentication.domain.exceptions.ErrorType
import com.kaiqkt.authentication.domain.models.enums.AuthenticationType
import com.kaiqkt.authentication.domain.repositories.UserRepository
import com.kaiqkt.authentication.domain.services.UserService
import com.kaiqkt.authentication.unit.domain.dtos.UserDtoSampler
import com.kaiqkt.authentication.unit.domain.models.UserSampler
import io.azam.ulidj.ULID
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val userService = UserService(userRepository, passwordEncoder)

    @Test
    fun `given a user dto when is valid should create successfully`(){
        every { userRepository.existsByEmail(any()) } returns false
        every { userRepository.save(any()) } returns UserSampler.sample()
        every { passwordEncoder.encode(any()) } returns "encrypted"

        userService.create(UserDtoSampler.sampleCreate())

        verify { userRepository.existsByEmail(any()) }
        verify { userRepository.save(any()) }
        verify { passwordEncoder.encode(any()) }
    }

    @Test
    fun `given a user dto when email in use should throw a exception`(){
        every { userRepository.existsByEmail(any()) } returns true

        val exception = assertThrows<DomainException> {
            userService.create(UserDtoSampler.sampleCreate())
        }

        verify { userRepository.existsByEmail(any()) }

        assertEquals(ErrorType.EMAIL_ALREADY_IN_USE, exception.type)
    }

    @Test
    fun `given a email and a authentication type should when exists should return a user`(){
        every { userRepository.findByEmailAndAuthenticationType(any(), any()) } returns UserSampler.sample()

        userService.findByEmailAndType("kt@kt.com", AuthenticationType.PASSWORD)

        verify { userRepository.findByEmailAndAuthenticationType(any(), any()) }
    }

    @Test
    fun `given a email and a authentication type should when not exists should throw a exception`(){
        every { userRepository.findByEmailAndAuthenticationType(any(), any()) } returns null

        val exception = assertThrows<DomainException> {
            userService.findByEmailAndType("kt@kt.com", AuthenticationType.PASSWORD)
        }

        verify { userRepository.findByEmailAndAuthenticationType(any(), any()) }

        assertEquals(ErrorType.USER_NOT_FOUND, exception.type)
    }

    @Test
    fun `given a id when user does exist should return successfully`(){
        every { userRepository.findById(any()) } returns Optional.of(UserSampler.sample())

        userService.findById(ULID.random())

        verify { userRepository.findById(any()) }
    }

    @Test
    fun `given a id when user does not exist should thrown an exception`(){
        every { userRepository.findById(any()) } returns Optional.empty()

        val exception = assertThrows<DomainException> {
            userService.findById(ULID.random())
        }

        verify { userRepository.findById(any()) }

        assertEquals(ErrorType.USER_NOT_FOUND, exception.type)
    }
}