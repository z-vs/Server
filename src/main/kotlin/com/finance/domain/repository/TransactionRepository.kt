package com.finance.domain.repository

import com.finance.domain.model.Transaction
import java.math.BigDecimal

interface TransactionRepository {
    suspend fun getAllByUserId(userId: Int): List<Transaction>
    suspend fun getById(id: Int): Transaction?
    suspend fun create(
        userId: Int,
        categoryId: Int,
        amount: BigDecimal,
        type: String,
        description: String?,
        date: Long
    ): Transaction
    suspend fun update(
        id: Int,
        categoryId: Int,
        amount: BigDecimal,
        description: String?,
        date: Long
    ): Transaction?
    suspend fun delete(id: Int): Boolean
}