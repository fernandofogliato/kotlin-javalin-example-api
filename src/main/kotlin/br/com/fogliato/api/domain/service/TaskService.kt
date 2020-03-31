package br.com.fogliato.api.domain.service

import br.com.fogliato.api.domain.model.task.Area
import br.com.fogliato.api.domain.model.task.Status
import br.com.fogliato.api.domain.model.task.Task
import br.com.fogliato.api.domain.repository.TaskRepository
import io.javalin.http.InternalServerErrorResponse
import io.javalin.http.NotFoundResponse

class TaskService(private val taskRepository: TaskRepository) {

    fun create(task: Task): Task? {
        try {
            return taskRepository.create(task)
        } catch (e: Exception) {
            throw InternalServerErrorResponse("Error to create a task.")
        }
    }

    fun findById(id: Long): Task? {
        return taskRepository.findById(id) ?: throw NotFoundResponse()
    }

    fun update(id: Long, task: Task): Task? {
        return findById(id).run {
            taskRepository.update(id, task)
        }
    }

    fun delete(id: Long) {
        return taskRepository.delete(id)
    }

    fun findAll(limit: Int, offset: Long, area: Area?, status: Status?): List<Task> {
        return when {
            status != null && area != null -> taskRepository.findAllByAreaAndStatus(limit, offset, area, status)
            area != null -> taskRepository.findAllByArea(limit, offset, area)
            else -> return taskRepository.findAll(limit, offset)
        }
    }
}