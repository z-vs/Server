package com.finance.presentation.routes

import com.finance.domain.model.Transaction
import com.finance.domain.usecase.transaction.*
import com.finance.presentation.dto.CreateTransactionRequest
import com.finance.presentation.dto.TransactionResponse
import com.finance.presentation.dto.UpdateTransactionRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.math.BigDecimal


private suspend fun Transaction.toResponse(
    categoryRepository: com.finance.domain.repository.CategoryRepository
): TransactionResponse {
    val categoryName = categoryRepository.getById(this.categoryId)?.name ?: "Неизвестно"
    return TransactionResponse(
        id           = this.id,
        categoryId   = this.categoryId,
        categoryName = categoryName,
        amount       = this.amount.toDouble(),
        type         = this.type,
        description  = this.description,
        date         = this.date
    )
}

fun Route.transactionRoutes(
    getTransactionsUseCase: GetTransactionsUseCase,
    createTransactionUseCase: CreateTransactionUseCase,
    updateTransactionUseCase: UpdateTransactionUseCase,
    deleteTransactionUseCase: DeleteTransactionUseCase,
    categoryRepository: com.finance.domain.repository.CategoryRepository
) {
    authenticate("auth-jwt") {
        route("/transactions") {

            get {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()

                when (val result = getTransactionsUseCase.execute(userId)) {
                    is GetTransactionsUseCase.Result.Success -> {
                        val response = result.transactions.map { it.toResponse(categoryRepository) }
                        call.respond(HttpStatusCode.OK, response)
                    }
                    is GetTransactionsUseCase.Result.Error ->
                        call.respond(HttpStatusCode.InternalServerError, result.message)
                }
            }

            post {
                val userId  = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val request = call.receive<CreateTransactionRequest>()

                when (val result = createTransactionUseCase.execute(
                    userId      = userId,
                    categoryId  = request.categoryId,
                    amount      = java.math.BigDecimal.valueOf(request.amount),
                    type        = request.type,
                    description = request.description,
                    date        = request.date
                )) {
                    is CreateTransactionUseCase.Result.Success ->
                        call.respond(HttpStatusCode.Created, result.transaction.toResponse(categoryRepository))
                    is CreateTransactionUseCase.Result.CategoryNotFound ->
                        call.respond(HttpStatusCode.NotFound, "Category not found")
                    is CreateTransactionUseCase.Result.CategoryNotOwned ->
                        call.respond(HttpStatusCode.Forbidden, "Category not owned")
                    is CreateTransactionUseCase.Result.InvalidAmount ->
                        call.respond(HttpStatusCode.BadRequest, "Amount must be positive")
                    is CreateTransactionUseCase.Result.Error ->
                        call.respond(HttpStatusCode.InternalServerError, result.message)
                }
            }

            put("/{id}") {
                val userId        = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val transactionId = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid id")
                val request       = call.receive<UpdateTransactionRequest>()

                when (val result = updateTransactionUseCase.execute(
                    userId        = userId,
                    transactionId = transactionId,
                    categoryId    = request.categoryId,
                    amount        = java.math.BigDecimal.valueOf(request.amount),
                    description   = request.description,
                    date          = request.date
                )) {
                    is UpdateTransactionUseCase.Result.Success ->
                        call.respond(HttpStatusCode.OK, result.transaction.toResponse(categoryRepository))
                    is UpdateTransactionUseCase.Result.TransactionNotFound ->
                        call.respond(HttpStatusCode.NotFound, "Transaction not found")
                    is UpdateTransactionUseCase.Result.NotOwned ->
                        call.respond(HttpStatusCode.Forbidden, "Transaction not owned")
                    is UpdateTransactionUseCase.Result.CategoryNotFound ->
                        call.respond(HttpStatusCode.NotFound, "Category not found")
                    is UpdateTransactionUseCase.Result.InvalidAmount ->
                        call.respond(HttpStatusCode.BadRequest, "Amount must be positive")
                    is UpdateTransactionUseCase.Result.Error ->
                        call.respond(HttpStatusCode.InternalServerError, result.message)
                }
            }

            delete("/{id}") {
                val userId        = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val transactionId = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid id")

                when (val result = deleteTransactionUseCase.execute(userId, transactionId)) {
                    is DeleteTransactionUseCase.Result.Success ->
                        call.respond(HttpStatusCode.OK, "Transaction deleted")
                    is DeleteTransactionUseCase.Result.NotFound ->
                        call.respond(HttpStatusCode.NotFound, "Transaction not found")
                    is DeleteTransactionUseCase.Result.NotOwned ->
                        call.respond(HttpStatusCode.Forbidden, "Transaction not owned")
                    is DeleteTransactionUseCase.Result.Error ->
                        call.respond(HttpStatusCode.InternalServerError, result.message)
                }
            }
        }
    }
}