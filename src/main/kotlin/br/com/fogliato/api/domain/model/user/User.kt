package br.com.fogliato.api.domain.model.user

import br.com.fogliato.api.domain.model.task.Area

data class UserDTO(val user: User?)

data class User(val id: Long? = null,
                val name: String,
                val email: String,
                val password: String,
                val profile: Profile,
                val group: Area,
                var active: Boolean = false,
                val token: String? = null)