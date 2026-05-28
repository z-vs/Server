package com.finance

import com.finance.data.database.DatabaseFactory
import com.finance.data.repository.CategoryRepositoryImpl
import com.finance.data.repository.TransactionRepositoryImpl
import com.finance.data.repository.UserRepositoryImpl
import com.finance.data.service.JwtService
import com.finance.domain.usecase.auth.LoginUseCase
import com.finance.domain.usecase.auth.RegisterUseCase
import com.finance.domain.usecase.category.CreateCategoryUseCase
import com.finance.domain.usecase.category.GetCategoriesUseCase
import com.finance.domain.usecase.transaction.*
import com.finance.presentation.plugins.*
import com.finance.presentation.routes.*
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import java.io.FileInputStream

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    val serviceAccount = FileInputStream("serviceAccountKey.json")
    val options = FirebaseOptions.builder()
        .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccount))
        .build()
    if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options)
    }

    val jwtSecret = System.getenv("JWT_SECRET") ?: "your-super-secret-key-change-in-prod"
    val jwtIssuer = System.getenv("JWT_ISSUER") ?: "finance-app"
    val jwtAudience = System.getenv("JWT_AUDIENCE") ?: "finance-app-users"
    val dbUrl      = System.getenv("DATABASE_URL")
        ?: "jdbc:postgresql://ep-billowing-moon-apofxier.c-7.us-east-1.aws.neon.tech/neondb"
    val dbUser     = System.getenv("DB_USER")      ?: "neondb_owner"
    val dbPassword = System.getenv("DB_PASSWORD")  ?: "npg_Psh0LJpVWf3N"

    DatabaseFactory.init(dbUrl, dbUser, dbPassword)

    val userRepository = UserRepositoryImpl()
    val categoryRepository = CategoryRepositoryImpl()
    val transactionRepository = TransactionRepositoryImpl()
    val jwtService = JwtService(jwtSecret, jwtIssuer, jwtAudience)

    val registerUseCase = RegisterUseCase(userRepository)
    val loginUseCase = LoginUseCase(userRepository)
    val getCategoriesUseCase = GetCategoriesUseCase(categoryRepository)
    val createCategoryUseCase = CreateCategoryUseCase(categoryRepository)
    val getTransactionsUseCase = GetTransactionsUseCase(transactionRepository)
    val createTransactionUseCase = CreateTransactionUseCase(transactionRepository, categoryRepository)
    val updateTransactionUseCase = UpdateTransactionUseCase(transactionRepository, categoryRepository)
    val deleteTransactionUseCase = DeleteTransactionUseCase(transactionRepository)

    configureSerialization()
    configureLogging()
    configureCORS()
    configureStatusPages()
    configureAuthentication(jwtSecret, jwtIssuer, jwtAudience)
    configureRouting(
        registerUseCase          = registerUseCase,
        loginUseCase             = loginUseCase,
        jwtService               = jwtService,
        getCategoriesUseCase     = getCategoriesUseCase,
        createCategoryUseCase    = createCategoryUseCase,
        getTransactionsUseCase   = getTransactionsUseCase,
        createTransactionUseCase = createTransactionUseCase,
        updateTransactionUseCase = updateTransactionUseCase,
        deleteTransactionUseCase = deleteTransactionUseCase,
        categoryRepository       = categoryRepository
    )
}