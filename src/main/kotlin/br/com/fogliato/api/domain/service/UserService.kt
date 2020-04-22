package br.com.fogliato.api.domain.service

import br.com.fogliato.api.config.Roles
import br.com.fogliato.api.domain.model.task.Area
import br.com.fogliato.api.domain.model.user.User
import br.com.fogliato.api.domain.repository.UserRepository
import br.com.fogliato.api.utils.JwtProvider
import io.javalin.http.*

class UserService(private val userRepository: UserRepository,
                  private val jwtProvider: JwtProvider) {

    fun create(user: User): User? {
        userRepository.findByEmail(user.email!!)?.apply {
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

    fun updateOtherUser(emailToken: String, id: Long, user: User): User? {
        val userAction = findByEmail(emailToken) ?: throw BadRequestResponse("Invalid user!")

        if (userAction.id != id && userAction.group != Area.IT_ADM) {
            throw ForbiddenResponse("User don't have access to modify user's passwords")
        }

        return findById(id)?.run {
            userRepository.update(id, user)
        }
    }

    fun update(emailToken: String, user: User): User? {
        return findByEmail(emailToken)?.run { userRepository.update(this.id!!, user) }
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
        val email = user.email ?: throw BadRequestResponse("invalid email")
        val userFound = userRepository.findByEmail(email)

        userFound?.also {
            if (it.password == user.password) {
                val token = generateJwtToken(user)
                return userFound.copy(token = token)
            }
        }
        throw UnauthorizedResponse("email or password is invalid!")
    }

    private fun generateJwtToken(user: User): String? {
        return jwtProvider.createJWT(user, Roles.AUTHENTICATED)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}