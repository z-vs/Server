package com.finance.data.tables

import org.jetbrains.exposed.dao.id.IntIdTable


object CategoriesTable : IntIdTable("categories") {
    val userId = integer("user_id").references(UsersTable.id)
    val name   = varchar("name", 100)
    val type   = varchar("type", 10)
}