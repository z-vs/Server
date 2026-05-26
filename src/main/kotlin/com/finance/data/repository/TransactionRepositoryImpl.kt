package com.finance.data.repository

import com.finance.data.tables.TransactionsTable
import com.finance.domain.model.Transaction
import com.finance.domain.repository.TransactionRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.math.BigDecimal

class TransactionRepositoryImpl : TransactionRepository {

    private fun ResultRow.toTransaction() = Transaction(
        id          = this[TransactionsTable.id].value,
        userId      = this[TransactionsTable.userId],
        categoryId  = this[TransactionsTable.categoryId],
        amount      = this[TransactionsTable.amount],
        type        = this[TransactionsTable.type],
        description = this[TransactionsTable.description],
        date        = this[TransactionsTable.date]
    )

    override suspend fun getAllByUserId(userId: Int): List<Transaction> =
        newSuspendedTransaction {
            TransactionsTable
                .select { TransactionsTable.userId eq userId }
                .orderBy(TransactionsTable.date, SortOrder.DESC)
                .map { it.toTransaction() }
        }

    override suspend fun getById(id: Int): Transaction? =
        newSuspendedTransaction {
            TransactionsTable
                .select { TransactionsTable.id eq id }
                .singleOrNull()
                ?.toTransaction()
        }

    override suspend fun create(
        userId: Int,
        categoryId: Int,
        amount: BigDecimal,
        type: String,
        description: String?,
        date: Long
    ): Transaction =
        newSuspendedTransaction {
            val insertedId = TransactionsTable.insertAndGetId {
                it[TransactionsTable.userId]      = userId
                it[TransactionsTable.categoryId]  = categoryId
                it[TransactionsTable.amount]      = amount
                it[TransactionsTable.type]        = type
                it[TransactionsTable.description] = description
                it[TransactionsTable.date]        = date
            }
            TransactionsTable
                .select { TransactionsTable.id eq insertedId }
                .single()
                .toTransaction()
        }

    override suspend fun update(
        id: Int,
        categoryId: Int,
        amount: BigDecimal,
        description: String?,
        date: Long
    ): Transaction? =
        newSuspendedTransaction {
            val updated = TransactionsTable.update(
                where = { TransactionsTable.id eq id }
            ) {
                it[TransactionsTable.categoryId]  = categoryId
                it[TransactionsTable.amount]      = amount
                it[TransactionsTable.description] = description
                it[TransactionsTable.date]        = date
            }
            if (updated > 0) {
                TransactionsTable
                    .select { TransactionsTable.id eq id }
                    .single()
                    .toTransaction()
            } else null
        }

    override suspend fun delete(id: Int): Boolean =
        newSuspendedTransaction {
            TransactionsTable
                .deleteWhere { TransactionsTable.id eq id } > 0
        }
}