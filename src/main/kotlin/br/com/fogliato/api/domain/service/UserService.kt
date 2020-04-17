package br.com.fogliato.api.domain.service

import br.com.fogliato.api.domain.model.user.User
import br.com.fogliato.api.domain.repository.UserRepository
import io.javalin.http.InternalServerErrorResponse
import io.javalin.http.NotFoundResponse

class UserService(private val userRepository: UserRepository) {

    fun create(user: User): User? {
        try {
            return userRepository.create(user)
        } catch (e: Exception) {
            throw InternalServerErrorResponse("Error to create a user.")
        }
    }

    fun findById(id: Long): User? {
        return userRepository.findById(id) ?: throw NotFoundResponse()
    }

    fun update(id: Long, user: User): User? {
        return findById(id)?.run {
            userRepository.update(id, user)
        }
    }

    fun delete(id: Long) {
        return userRepository.delete(id)
    }

    fun findAll(limit: Int, offset: Long, active: Boolean?): List<User> {
        return when {
            active != null && active -> userRepository.findAllByActive(limit, offset, active)
            else -> return userRepository.findAll(limit, offset)
        }
    }
}