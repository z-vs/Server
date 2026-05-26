package com.finance.domain.usecase.transaction

import com.finance.domain.model.Transaction
import com.finance.domain.repository.CategoryRepository
import com.finance.domain.repository.TransactionRepository
import java.math.BigDecimal

class CreateTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {
    sealed class Result {
        data class Success(val transaction: Transaction) : Result()
        object CategoryNotFound : Result()
        object CategoryNotOwned : Result()
        object InvalidAmount : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(
        userId: Int,
        categoryId: Int,
        amount: BigDecimal,
        type: String,
        description: String?,
        date: Long
    ): Result {
        return try {
            if (amount <= BigDecimal.ZERO) return Result.InvalidAmount

            val category = categoryRepository.getById(categoryId)
                ?: return Result.CategoryNotFound
            if (category.userId != userId) return Result.CategoryNotOwned

            val transaction = transactionRepository.create(
                userId      = userId,
                categoryId  = categoryId,
                amount      = amount,
                type        = type,
                description = description,
                date        = date
            )
            Result.Success(transaction)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}