package com.finance.domain.model

data class User(
    val id: Int,
    val firebaseUid: String,
    val email: String,
    val createdAt: Long
)