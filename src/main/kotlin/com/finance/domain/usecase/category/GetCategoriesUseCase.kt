package com.finance.domain.usecase.category

import com.finance.domain.model.Category
import com.finance.domain.repository.CategoryRepository

class GetCategoriesUseCase(
    private val categoryRepository: CategoryRepository
) {
    sealed class Result {
        data class Success(val categories: List<Category>) : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(userId: Int): Result {
        return try {
            val categories = categoryRepository.getAllByUserId(userId)
            Result.Success(categories)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}