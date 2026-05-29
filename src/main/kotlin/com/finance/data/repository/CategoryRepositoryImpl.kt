package com.finance.data.repository

import com.finance.data.tables.CategoriesTable
import com.finance.domain.model.Category
import com.finance.domain.repository.CategoryRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class CategoryRepositoryImpl : CategoryRepository {

    private fun ResultRow.toCategory() = Category(
        id     = this[CategoriesTable.id].value,
        userId = this[CategoriesTable.userId],
        name   = this[CategoriesTable.name],
        type   = this[CategoriesTable.type]
    )

    override suspend fun getAllByUserId(userId: Int): List<Category> =
        newSuspendedTransaction {
            CategoriesTable
                .select { CategoriesTable.userId eq userId }
                .map { it.toCategory() }
        }

    override suspend fun getById(id: Int): Category? =
        newSuspendedTransaction {
            CategoriesTable
                .select { CategoriesTable.id eq id }
                .singleOrNull()
                ?.toCategory()
        }

    override suspend fun create(
        userId: Int,
        name: String,
        type: String
    ): Category =
        newSuspendedTransaction {
            val insertedId = CategoriesTable.insertAndGetId {
                it[CategoriesTable.userId] = userId
                it[CategoriesTable.name]   = name
                it[CategoriesTable.type]   = type
            }
            CategoriesTable
                .select { CategoriesTable.id eq insertedId }
                .single()
                .toCategory()
        }

    override suspend fun delete(id: Int): Boolean =
        newSuspendedTransaction {
            CategoriesTable
                .deleteWhere { CategoriesTable.id eq id } > 0
        }

    override suspend fun update(
        id: Int,
        name: String,
        type: String
    ): Category? =
        newSuspendedTransaction {
            val updated = CategoriesTable.update(
                where = { CategoriesTable.id eq id }
            ) {
                it[CategoriesTable.name] = name
                it[CategoriesTable.type] = type
            }
            if (updated > 0) {
                CategoriesTable
                    .select { CategoriesTable.id eq id }
                    .single()
                    .toCategory()
            } else null
        }
}