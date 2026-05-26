package com.finance.presentation.plugins

import com.finance.data.service.JwtService
import com.finance.domain.usecase.auth.LoginUseCase
import com.finance.domain.usecase.auth.RegisterUseCase
import com.finance.domain.usecase.category.CreateCategoryUseCase
import com.finance.domain.usecase.category.GetCategoriesUseCase
import com.finance.domain.usecase.transaction.*
import com.finance.presentation.routes.authRoutes
import com.finance.presentation.routes.categoryRoutes
import com.finance.presentation.routes.transactionRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    registerUseCase: RegisterUseCase,
    loginUseCase: LoginUseCase,
    jwtService: JwtService,
    getCategoriesUseCase: GetCategoriesUseCase,
    createCategoryUseCase: CreateCategoryUseCase,
    getTransactionsUseCase: GetTransactionsUseCase,
    createTransactionUseCase: CreateTransactionUseCase,
    updateTransactionUseCase: UpdateTransactionUseCase,
    deleteTransactionUseCase: DeleteTransactionUseCase,
    categoryRepository: com.finance.domain.repository.CategoryRepository
) {
    routing {
        authRoutes(registerUseCase, loginUseCase, jwtService)
        transactionRoutes(
            getTransactionsUseCase,
            createTransactionUseCase,
            updateTransactionUseCase,
            deleteTransactionUseCase,
            categoryRepository,
        )
        categoryRoutes(getCategoriesUseCase, createCategoryUseCase, categoryRepository)
    }
}