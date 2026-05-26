package com.finance.domain.usecase.transaction

import com.finance.domain.repository.TransactionRepository

class DeleteTransactionUseCase(
    private val transactionRepository: TransactionRepository
) {
    sealed class Result {
        object Success : Result()
        object NotFound : Result()
        object NotOwned : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(userId: Int, transactionId: Int): Result {
        return try {
            val existing = transactionRepository.getById(transactionId)
                ?: return Result.NotFound
            if (existing.userId != userId) return Result.NotOwned

            transactionRepository.delete(transactionId)
            Result.Success
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}