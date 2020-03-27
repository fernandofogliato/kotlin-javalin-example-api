package br.com.fogliato.api.domain.model.user

data class UserDTO(val user: User? = null)

data class User(val id: Long? = null,
                val email: String,
                val password: String? = null,
                val token: String? = null)