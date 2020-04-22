package br.com.fogliato.api.domain.model.user

import br.com.fogliato.api.domain.model.task.Area

data class UsersDTO(val users: List<User>, val count: Int)

data class UserDTO(val user: User?)

data class User(val id: Long? = null,
                val name: String? = null,
                val email: String? = null,
                val password: String? = null,
                val profile: Profile? = null,
                val group: Area? = null,
                val active: Boolean? = false,
                val token: String? = null)