package com.finance.domain.usecase.auth

import com.finance.domain.model.User
import com.finance.domain.repository.UserRepository

class RegisterUseCase(
    private val userRepository: UserRepository
) {
    sealed class Result {
        data class Success(val user: User) : Result()
        object AlreadyExists : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(firebaseUid: String, email: String): Result {
        return try {
            val existing = userRepository.findByFirebaseUid(firebaseUid)
            if (existing != null) return Result.AlreadyExists

            val user = userRepository.create(firebaseUid, email)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}