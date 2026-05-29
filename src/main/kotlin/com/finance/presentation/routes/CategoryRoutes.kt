package com.finance.presentation.routes

import com.finance.domain.usecase.category.CreateCategoryUseCase
import com.finance.domain.usecase.category.GetCategoriesUseCase
import com.finance.presentation.dto.CategoryResponse
import com.finance.presentation.dto.CreateCategoryRequest
import com.finance.presentation.dto.UpdateCategoryRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoryRoutes(
    getCategoriesUseCase: GetCategoriesUseCase,
    createCategoryUseCase: CreateCategoryUseCase,
    categoryRepository: com.finance.domain.repository.CategoryRepository
) {
    authenticate("auth-jwt") {
        route("/categories") {

            get {
                val userId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                when (val result = getCategoriesUseCase.execute(userId)) {
                    is GetCategoriesUseCase.Result.Success -> {
                        val response = result.categories.map {
                            CategoryResponse(it.id, it.name, it.type)
                        }
                        call.respond(HttpStatusCode.OK, response)
                    }
                    is GetCategoriesUseCase.Result.Error ->
                        call.respond(HttpStatusCode.InternalServerError, result.message)
                }
            }

            post {
                val userId  = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val request = call.receive<CreateCategoryRequest>()
                when (val result = createCategoryUseCase.execute(
                    userId = userId,
                    name   = request.name,
                    type   = request.type
                )) {
                    is CreateCategoryUseCase.Result.Success ->
                        call.respond(HttpStatusCode.Created,
                            CategoryResponse(
                                result.category.id,
                                result.category.name,
                                result.category.type
                            )
                        )
                    is CreateCategoryUseCase.Result.EmptyName ->
                        call.respond(HttpStatusCode.BadRequest, "Name cannot be empty")
                    is CreateCategoryUseCase.Result.InvalidType ->
                        call.respond(HttpStatusCode.BadRequest, "Type must be 'income' or 'expense'")
                    is CreateCategoryUseCase.Result.Error ->
                        call.respond(HttpStatusCode.InternalServerError, result.message)
                }
            }

            put("/{id}") {
                val userId     = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val categoryId = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid id")
                val request    = call.receive<UpdateCategoryRequest>()

                val category = categoryRepository.getById(categoryId)
                if (category == null) {
                    call.respond(HttpStatusCode.NotFound, "Category not found")
                    return@put
                }
                if (category.userId != userId) {
                    call.respond(HttpStatusCode.Forbidden, "Category not owned")
                    return@put
                }

                val updated = categoryRepository.update(categoryId, request.name, request.type)
                if (updated != null) {
                    call.respond(HttpStatusCode.OK, CategoryResponse(updated.id, updated.name, updated.type))
                } else {
                    call.respond(HttpStatusCode.NotFound, "Category not found")
                }
            }

            delete("/{id}") {
                val userId     = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("userId").asInt()
                val categoryId = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid id")

                val category = categoryRepository.getById(categoryId)
                if (category == null) {
                    call.respond(HttpStatusCode.NotFound, "Category not found")
                    return@delete
                }
                if (category.userId != userId) {
                    call.respond(HttpStatusCode.Forbidden, "Category not owned")
                    return@delete
                }

                val deleted = categoryRepository.delete(categoryId)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Category deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Category not found")
                }
            }
        }
    }
}