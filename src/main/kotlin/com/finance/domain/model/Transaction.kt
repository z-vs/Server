package com.finance.domain.model

import java.math.BigDecimal

data class Transaction(
    val id: Int,
    val userId: Int,
    val categoryId: Int,
    val amount: BigDecimal,
    val type: String,
    val description: String?,
    val date: Long
)