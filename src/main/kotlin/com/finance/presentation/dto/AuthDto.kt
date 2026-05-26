package com.finance.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val firebaseToken: String,
    val email: String
)

@Serializable
data class LoginRequest(
    val firebaseToken: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Int,
    val email: String
)