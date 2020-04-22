package br.com.fogliato.api

import br.com.fogliato.api.config.Roles
import br.com.fogliato.api.controllers.TaskController
import br.com.fogliato.api.controllers.UserController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.security.SecurityUtil.roles
import org.koin.standalone.KoinComponent

class Router(private val taskController: TaskController,
             private val userController: UserController) : KoinComponent {

    fun register(app: Javalin) {
        val roleAuthenticateRequired = roles(Roles.AUTHENTICATED)
        val roleAnyone = roles(Roles.ANYONE)

        app.routes {
            path("tasks") {
                path(":id") {
                    get(taskController::findById, roleAuthenticateRequired)
                    put(taskController::update, roleAuthenticateRequired)
                    delete(taskController::delete, roleAuthenticateRequired)
                }
                get(taskController::get, roleAuthenticateRequired)
                post(taskController::create, roleAuthenticateRequired)
            }

            path("users") {
                path("/active") {
                    get(userController::getActives, roleAuthenticateRequired)
                }
                path("/inactive") {
                    get(userController::getInactives, roleAuthenticateRequired)
                }
                path("/authenticate") {
                    post(userController::authenticate, roleAnyone)
                }
                path("/:id/password") {
                    post(userController::updateOtherUser, roleAuthenticateRequired)
                }
                path("/password") {
                    post(userController::updatePassword, roleAuthenticateRequired)
                }
                get(userController::get, roleAuthenticateRequired)
                post(userController::create, roleAnyone)
            }
        }
    }
}
