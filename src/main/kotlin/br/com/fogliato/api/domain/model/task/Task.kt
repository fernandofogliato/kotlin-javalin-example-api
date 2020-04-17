package br.com.fogliato.api.domain.model.task

import br.com.fogliato.api.domain.model.user.User
import java.time.LocalDateTime

data class TasksDTO(val tasks: List<Task>, val count: Int)

data class TaskDTO(val task: Task?)

data class Task(val id: Long? = null,
                val title: String,
                val type: Type,
                val area: Area,
                val status: Status = Status.TODO,
                val description: String? = null,
                val assignee: User? = null,
                val createdAt: LocalDateTime? = null,
                val createdBy: User? = null,
                val updatedAt: LocalDateTime? = null)