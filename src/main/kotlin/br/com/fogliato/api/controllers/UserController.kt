package br.com.fogliato.api.controllers

import br.com.fogliato.api.config.Roles
import br.com.fogliato.api.domain.model.task.Area
import br.com.fogliato.api.domain.model.user.User
import br.com.fogliato.api.domain.model.user.UserDTO
import br.com.fogliato.api.domain.model.user.UsersDTO
import br.com.fogliato.api.domain.service.UserService
import br.com.fogliato.api.utils.JwtProvider
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class UserController(private val userService: UserService) {
    
    fun create(ctx: Context) {
        ctx.bodyValidator<UserDTO>()
                .check({ it.user?.id == null }, "the id must be null")
                .check({ !it.user?.name.isNullOrBlank() }, "name is required")
                .check({ !it.user?.email.isNullOrBlank() }, "email is required")
                .check({ !it.user?.password.isNullOrBlank() }, "password is required")
                .check({ it.user?.profile != null }, "profile is required")
                .check({ it.user?.group != null }, "group is required")
                .get().user?.also { task ->
                    userService.create(task).apply {
                        ctx.status(HttpStatus.CREATED_201)
                        ctx.json(UserDTO(this))
                    }
                }
    }

    fun get(ctx: Context) {
        val area: Area? = ctx.queryParam("area")?.let {
            try { Area.valueOf(it) } catch (e: IllegalArgumentException) {
                throw BadRequestResponse("Area type $it does not exist") }
        }

        val limit = ctx.queryParam("limit") ?: "20"
        val offset = ctx.queryParam("offset") ?: "0"

        userService.findAll(limit.toInt(), offset.toLong(), area)
            .also { users -> ctx.json(UsersDTO(users, users.size)) }
    }

    fun getActives(ctx: Context) {
        val limit = ctx.queryParam("limit") ?: "20"
        val offset = ctx.queryParam("offset") ?: "0"

        userService.findAllByActive(limit.toInt(), offset.toLong(), true)
                .also { users -> ctx.json(UsersDTO(users, users.size)) }
    }

    fun getInactives(ctx: Context) {
        val limit = ctx.queryParam("limit") ?: "20"
        val offset = ctx.queryParam("offset") ?: "0"

        userService.findAllByActive(limit.toInt(), offset.toLong(), false)
                .also { users -> ctx.json(UsersDTO(users, users.size)) }
    }

    fun authenticate(ctx: Context) {
        ctx.bodyValidator<UserDTO>()
                .get().user?.also { user ->
                    userService.authenticate(user).apply {
                        ctx.json(this)
                    }
                }
    }

    fun updatePassword(ctx: Context) {
        ctx.bodyValidator<UserDTO>()
                .get().user?.also { user ->
                    val emailToken = ctx.attribute("email") ?: ""
                    userService.update(emailToken, user).apply {
                        ctx.status(HttpStatus.NO_CONTENT_204)
                    }
                }
    }

    fun updateOtherUser(ctx: Context) {
        val id = ctx.pathParam<Long>("id").get()

        ctx.bodyValidator<UserDTO>()
                .get().user?.also { user ->
                    val emailToken = ctx.attribute("email") ?: ""
                    userService.updateOtherUser(emailToken, id, user).apply {
                        ctx.status(HttpStatus.NO_CONTENT_204)
                    }
                }
    }
}