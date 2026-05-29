package com.finance.domain.usecase.category

import com.finance.domain.model.Category
import com.finance.domain.repository.CategoryRepository

class CreateCategoryUseCase(
    private val categoryRepository: CategoryRepository
) {
    sealed class Result {
        data class Success(val category: Category) : Result()
        object InvalidType : Result()
        object EmptyName : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun execute(
        userId: Int,
        name: String,
        type: String
    ): Result {
        return try {
            if (name.isBlank()) return Result.EmptyName
            if (type != "income" && type != "expense") return Result.InvalidType

            val category = categoryRepository.create(
                userId = userId,
                name   = name,
                type   = type
            )
            Result.Success(category)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}