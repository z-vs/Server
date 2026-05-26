package com.finance.domain.usecase.auth

import com.finance.domain.model.User
import com.finance.domain.repository.UserRepository

class LoginUseCase(
    private val userRepository: UserRepository
) {
    sealed class Result {
        data class Success(val user: User) : Result()
        object NotFound : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(firebaseUid: String): Result {
        return try {
            val user = userRepository.findByFirebaseUid(firebaseUid)
                ?: return Result.NotFound
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}