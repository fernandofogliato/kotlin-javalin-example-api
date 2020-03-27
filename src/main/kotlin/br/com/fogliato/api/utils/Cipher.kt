package br.com.fogliato.api.utils

import com.auth0.jwt.algorithms.Algorithm

object Cipher {
    val algorithm = Algorithm.HMAC256("something-very-secret-here")

    fun encrypt(data: String?): ByteArray =
            algorithm.sign(data?.toByteArray())

}