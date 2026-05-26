package com.finance.domain.usecase.transaction

import com.finance.domain.model.Transaction
import com.finance.domain.repository.CategoryRepository
import com.finance.domain.repository.TransactionRepository
import java.math.BigDecimal

class UpdateTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {
    sealed class Result {
        data class Success(val transaction: Transaction) : Result()
        object TransactionNotFound : Result()
        object NotOwned : Result()
        object CategoryNotFound : Result()
        object InvalidAmount : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(
        userId: Int,
        transactionId: Int,
        categoryId: Int,
        amount: BigDecimal,
        description: String?,
        date: Long
    ): Result {
        return try {
            if (amount <= BigDecimal.ZERO) return Result.InvalidAmount

            val existing = transactionRepository.getById(transactionId)
                ?: return Result.TransactionNotFound
            if (existing.userId != userId) return Result.NotOwned

            val category = categoryRepository.getById(categoryId)
                ?: return Result.CategoryNotFound

            val updated = transactionRepository.update(
                id          = transactionId,
                categoryId  = categoryId,
                amount      = amount,
                description = description,
                date        = date
            ) ?: return Result.TransactionNotFound

            Result.Success(updated)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}