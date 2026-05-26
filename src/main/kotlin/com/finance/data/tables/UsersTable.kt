package com.finance.data.tables

import org.jetbrains.exposed.dao.id.IntIdTable


object UsersTable : IntIdTable("users") {
    val firebaseUid = varchar("firebase_uid", 128).uniqueIndex()
    val email       = varchar("email", 255).uniqueIndex()
    val createdAt   = long("created_at")
}