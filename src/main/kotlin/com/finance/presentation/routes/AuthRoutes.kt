package com.finance.presentation.routes

import com.finance.data.service.JwtService
import com.finance.domain.usecase.auth.LoginUseCase
import com.finance.domain.usecase.auth.RegisterUseCase
import com.finance.presentation.dto.AuthResponse
import com.finance.presentation.dto.LoginRequest
import com.finance.presentation.dto.RegisterRequest
import com.google.firebase.auth.FirebaseAuth
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(
    registerUseCase: RegisterUseCase,
    loginUseCase: LoginUseCase,
    jwtService: JwtService
) {
    route("/auth") {

        post("/register") {
            val request = call.receive<RegisterRequest>()

            val firebaseToken = try {
                FirebaseAuth.getInstance().verifyIdToken(request.firebaseToken)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid Firebase token")
                return@post
            }

            when (val result = registerUseCase.execute(firebaseToken.uid, request.email)) {
                is RegisterUseCase.Result.Success -> {
                    val token = jwtService.generateToken(result.user.id, result.user.email)
                    call.respond(HttpStatusCode.Created, AuthResponse(
                        token  = token,
                        userId = result.user.id,
                        email  = result.user.email
                    ))
                }
                is RegisterUseCase.Result.AlreadyExists -> {
                    call.respond(HttpStatusCode.Conflict, "User already exists")
                }
                is RegisterUseCase.Result.Error -> {
                    call.respond(HttpStatusCode.InternalServerError, result.message)
                }
            }
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            val firebaseToken = try {
                FirebaseAuth.getInstance().verifyIdToken(request.firebaseToken)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid Firebase token")
                return@post
            }

            when (val result = loginUseCase.execute(firebaseToken.uid)) {
                is LoginUseCase.Result.Success -> {
                    val token = jwtService.generateToken(result.user.id, result.user.email)
                    call.respond(HttpStatusCode.OK, AuthResponse(
                        token  = token,
                        userId = result.user.id,
                        email  = result.user.email
                    ))
                }
                is LoginUseCase.Result.NotFound -> {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
                is LoginUseCase.Result.Error -> {
                    call.respond(HttpStatusCode.InternalServerError, result.message)
                }
            }
        }
    }
}