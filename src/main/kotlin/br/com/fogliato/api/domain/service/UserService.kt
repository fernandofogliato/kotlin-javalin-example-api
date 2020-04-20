package br.com.fogliato.api.domain.service

import br.com.fogliato.api.config.Roles
import br.com.fogliato.api.domain.model.task.Area
import br.com.fogliato.api.domain.model.user.User
import br.com.fogliato.api.domain.repository.UserRepository
import br.com.fogliato.api.utils.JwtProvider
import io.javalin.http.*
import org.eclipse.jetty.http.HttpStatus

class UserService(private val userRepository: UserRepository,
                  private val jwtProvider: JwtProvider) {

    fun create(user: User): User? {
        userRepository.findByEmail(user.email)?.apply {
            throw BadRequestResponse("User with ${user.email} already registred")
        }

        try {
            return userRepository.create(user)?.copy(token = generateJwtToken(user))
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

    fun findAll(limit: Int, offset: Long, area: Area?): List<User> {
        return when {
            area != null -> userRepository.findAllByArea(limit, offset, area)
            else -> return userRepository.findAll(limit, offset)
        }
    }

    fun findAllByActive(limit: Int, offset: Long, active: Boolean): List<User> {
        return userRepository.findAllByActive(limit, offset, active)
    }

    fun authenticate(user: User): User {
        val userFound = userRepository.findByEmail(user.email)

        if (userFound?.password == user.password) {
            return userFound.copy(token = generateJwtToken(user))
        }
        throw UnauthorizedResponse("email or password is invalid!")
    }

    private fun generateJwtToken(user: User): String? {
        return jwtProvider.createJWT(user, Roles.AUTHENTICATED)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email);
    }
}