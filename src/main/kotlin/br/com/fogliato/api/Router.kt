package br.com.fogliato.api

import br.com.fogliato.api.controllers.TaskController
import br.com.fogliato.api.controllers.UserController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import org.koin.standalone.KoinComponent

class Router(private val taskController: TaskController,
             private val userController: UserController) : KoinComponent {

    fun register(app: Javalin) {
        app.routes {
            path("tasks") {
                path(":id") {
                    get(taskController::findById)
                    put(taskController::update)
                    delete(taskController::delete)
                }
                get(taskController::get)
                post(taskController::create)
            }

            path("users") {
                path("/active") {
                    get(userController::getActives)
                }
                path("/inactive") {
                    get(userController::getInactives)
                }
                get(userController::get)
                post(userController::create)
            }
        }
    }
}
