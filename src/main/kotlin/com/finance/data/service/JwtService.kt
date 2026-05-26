package com.finance.data.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtService(
    private val secret: String,
    private val issuer: String,
    private val audience: String
) {

    private val expirationMs = 30L * 24 * 60 * 60 * 1000

    fun generateToken(userId: Int, email: String): String =
        JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationMs))
            .sign(Algorithm.HMAC256(secret))
}