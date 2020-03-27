package br.com.fogliato.api

import br.com.fogliato.api.controllers.TaskController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import org.koin.standalone.KoinComponent

class Router(
    private val taskController: TaskController
) : KoinComponent {

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
        }
    }
}
