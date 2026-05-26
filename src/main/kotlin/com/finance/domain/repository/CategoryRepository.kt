package com.finance.domain.repository

import com.finance.domain.model.Category

interface CategoryRepository {
    suspend fun getAllByUserId(userId: Int): List<Category>
    suspend fun getById(id: Int): Category?
    suspend fun create(userId: Int, name: String, type: String, icon: String?): Category
    suspend fun update(id: Int, name: String, type: String, icon: String?): Category?
    suspend fun delete(id: Int): Boolean
}