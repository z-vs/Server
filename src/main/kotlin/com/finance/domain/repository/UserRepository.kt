package com.finance.domain.repository

import com.finance.domain.model.User

interface UserRepository {
    suspend fun findByFirebaseUid(uid: String): User?
    suspend fun findById(id: Int): User?
    suspend fun create(firebaseUid: String, email: String): User
}