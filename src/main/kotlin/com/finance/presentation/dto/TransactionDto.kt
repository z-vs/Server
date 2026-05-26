package com.finance.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateTransactionRequest(
    val categoryId: Int,
    val amount: Double,
    val type: String,
    val description: String? = null,
    val date: Long
)

@Serializable
data class UpdateTransactionRequest(
    val categoryId: Int,
    val amount: Double,
    val description: String? = null,
    val date: Long
)

@Serializable
data class TransactionResponse(
    val id: Int,
    val categoryId: Int,
    val categoryName: String,
    val amount: Double,
    val type: String,
    val description: String?,
    val date: Long
)