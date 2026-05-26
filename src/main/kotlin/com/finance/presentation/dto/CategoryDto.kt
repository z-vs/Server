package com.finance.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateCategoryRequest(
    val name: String,
    val type: String,
    val icon: String? = null
)

@Serializable
data class UpdateCategoryRequest(
    val name: String,
    val type: String,
    val icon: String? = null
)

@Serializable
data class CategoryResponse(
    val id: Int,
    val name: String,
    val type: String,
    val icon: String?
)