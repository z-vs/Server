package com.finance.data.repository

import com.finance.data.tables.UsersTable
import com.finance.domain.model.User
import com.finance.domain.repository.UserRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserRepositoryImpl : UserRepository {

    private fun ResultRow.toUser() = User(
        id          = this[UsersTable.id].value,
        firebaseUid = this[UsersTable.firebaseUid],
        email       = this[UsersTable.email],
        createdAt   = this[UsersTable.createdAt]
    )

    override suspend fun findByFirebaseUid(uid: String): User? =
        newSuspendedTransaction {
            UsersTable
                .select { UsersTable.firebaseUid eq uid }
                .singleOrNull()
                ?.toUser()
        }

    override suspend fun findById(id: Int): User? =
        newSuspendedTransaction {
            UsersTable
                .select { UsersTable.id eq id }
                .singleOrNull()
                ?.toUser()
        }

    override suspend fun create(firebaseUid: String, email: String): User =
        newSuspendedTransaction {
            val insertedId = UsersTable.insertAndGetId {
                it[UsersTable.firebaseUid] = firebaseUid
                it[UsersTable.email]       = email
                it[UsersTable.createdAt]   = System.currentTimeMillis()
            }
            UsersTable
                .select { UsersTable.id eq insertedId }
                .single()
                .toUser()
        }
}