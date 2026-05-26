package com.finance.domain.usecase.transaction

import com.finance.domain.model.Transaction
import com.finance.domain.repository.TransactionRepository

class GetTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    sealed class Result {
        data class Success(val transactions: List<Transaction>) : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(userId: Int): Result {
        return try {
            val transactions = transactionRepository.getAllByUserId(userId)
            Result.Success(transactions)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}