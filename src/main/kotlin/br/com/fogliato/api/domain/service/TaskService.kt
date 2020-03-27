package br.com.fogliato.api.domain.service

import br.com.fogliato.api.domain.model.task.Task
import br.com.fogliato.api.domain.repository.TaskRepository
import io.javalin.http.InternalServerErrorResponse
import io.javalin.http.NotFoundResponse

class TaskService(private val taskRepository: TaskRepository) {

    fun create(task: Task) {
        try {
            taskRepository.create(task)
        } catch (e: Exception) {
            throw InternalServerErrorResponse("Error to create a task.")
        }
    }

    fun findById(id: Long): Task? {
        return taskRepository.findById(id) ?: throw NotFoundResponse()
    }

    fun findAll(limit: Int, offset: Long): List<Task> {
        return taskRepository.findAll(limit, offset);
    }

    fun update(id: Long, task: Task): Task? {
        return findById(id).run {
            taskRepository.update(id, task)
        }
    }

    fun delete(id: Long) {
        return taskRepository.delete(id)
    }
}