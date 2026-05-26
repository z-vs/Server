package com.finance.data.database

import com.finance.data.tables.CategoriesTable
import com.finance.data.tables.TransactionsTable
import com.finance.data.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(jdbcUrl: String, username: String, password: String) {
        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            driverClassName = "org.postgresql.Driver"
            this.username = username
            this.password = password
            maximumPoolSize = 10
            minimumIdle = 2
            idleTimeout = 300_000
            maxLifetime = 1_800_000
            connectionTimeout = 30_000
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            addDataSourceProperty("sslmode", "require")
            addDataSourceProperty("channel_binding", "disable")
            addDataSourceProperty("options", "-c scram_iterations=4096")
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(
                UsersTable,
                CategoriesTable,
                TransactionsTable
            )
        }
    }
}