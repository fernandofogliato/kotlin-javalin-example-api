package br.com.fogliato.api.domain.model.task

import java.time.LocalDateTime

data class Task(val id: Long,
                val title: String,
                val type: Type,
                val area: Area,
                val status: Status = Status.TODO,
                val description: String,
                val createdAt: LocalDateTime? = null,
                val updatedAt: LocalDateTime? = null)