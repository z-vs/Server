package com.finance.presentation.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuthentication(
    jwtSecret: String,
    jwtIssuer: String,
    jwtAudience: String
) {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .withAudience(jwtAudience)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                if (userId != null) JWTPrincipal(credential.payload) else null
            }
        }
    }
}