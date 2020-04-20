package br.com.fogliato.api.controllers

import br.com.fogliato.api.domain.model.task.Area
import br.com.fogliato.api.domain.model.task.Status
import br.com.fogliato.api.domain.model.task.TaskDTO
import br.com.fogliato.api.domain.model.task.TasksDTO
import br.com.fogliato.api.domain.service.TaskService
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus
import java.lang.IllegalArgumentException

class TaskController(private val taskService: TaskService) {
    
    fun findById(ctx: Context) {
        ctx.pathParam<Long>("id")
            .get().also { id ->
                taskService.findById(id).apply {
                    ctx.json(TaskDTO(this))
                }
            }
    }

    fun create(ctx: Context) {
        ctx.bodyValidator<TaskDTO>()
                .check({ it.task?.id == null }, "the id must be null")
                .check({ !it.task?.title.isNullOrBlank() }, "title is required")
                .get().task?.also { task ->
                    taskService.create(task).apply {
                        print(this)
                        ctx.status(HttpStatus.CREATED_201)
                        ctx.json(TaskDTO(this))
                    }
                }
    }

    fun update(ctx: Context) {
        val id = ctx.pathParam<Long>("id").get()

        ctx.bodyValidator<TaskDTO>()
            .check({ !it.task?.title.isNullOrBlank() })
            .check({ !it.task?.description.isNullOrBlank() })
            .get().task?.also { task ->
                taskService.update(id, task).apply {
                    ctx.status(HttpStatus.NO_CONTENT_204)
                }
            }
    }

    fun delete(ctx: Context) {
        ctx.pathParam<Long>("id").get().also { id ->
            taskService.delete(id)
            ctx.status(HttpStatus.NO_CONTENT_204)
        }
    }

    fun get(ctx: Context) {
        val area: Area? = ctx.queryParam("area")?.let {
            try { Area.valueOf(it) } catch (e: IllegalArgumentException) { throw BadRequestResponse("Area type $it does not exist") }
        }

        val status: Status? = ctx.queryParam("status")?.let {
            try { Status.valueOf(it) } catch (e: IllegalArgumentException) { throw BadRequestResponse("Status type $it does not exist") }
        }

        val limit = ctx.queryParam("limit") ?: "20"
        val offset = ctx.queryParam("offset") ?: "0"

        taskService.findAll(limit.toInt(), offset.toLong(), area, status)
            .also { tasks -> ctx.json(TasksDTO(tasks, tasks.size)) }
    }
}