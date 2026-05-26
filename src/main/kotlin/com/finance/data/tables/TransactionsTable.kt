package com.finance.data.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object TransactionsTable : IntIdTable("transactions") {
    val userId      = integer("user_id").references(UsersTable.id)
    val categoryId  = integer("category_id").references(CategoriesTable.id)
    val amount      = decimal("amount", 12, 2)
    val type        = varchar("type", 10)
    val description = varchar("description", 255).nullable()
    val date        = long("date")
}