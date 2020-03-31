package br.com.fogliato.api.controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil

import br.com.fogliato.api.config.AppConfig
import br.com.fogliato.api.domain.model.task.*
import com.mashape.unirest.http.HttpResponse

import org.eclipse.jetty.http.HttpStatus
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TaskControllerTest {
    private lateinit var app: Javalin
    private lateinit var http: HttpUtil

    @Before
    fun start() {
        app = AppConfig().setup().start()
        http = HttpUtil(app.port())
    }

    @After
    fun stop() {
        app.stop()
    }

    fun createTask(): HttpResponse<TaskDTO> {
        val task = Task(title = "Teste", status = Status.TODO, area = Area.FINANCE_MGT, type = Type.FEATURE)
        return http.post<TaskDTO>("/api/tasks", TaskDTO(task))
    }

    @Test
    fun `get all tasks`() {
        this.createTask()
        val http = HttpUtil(app.port())
        val response = http.get<TasksDTO>("/api/tasks")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.tasks)
        assertEquals(response.body.tasks.size, response.body.count)
    }

    @Test
    fun `get all tasks by area`() {
        this.createTask()
        val http = HttpUtil(app.port())
        val response = http.get<TasksDTO>("/api/tasks", mapOf(Area.FINANCE_MGT.name to "area"))

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.tasks)
        assertEquals(response.body.tasks.size, response.body.count)
    }

    @Test
    fun `get all tasks by area and status`() {
        this.createTask()
        val http = HttpUtil(app.port())
        val params = mapOf(Area.FINANCE_MGT.name to "area", Status.TODO.name to "status")
        val response = http.get<TasksDTO>("/api/tasks", params)

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.tasks)
        assertEquals(response.body.tasks.size, response.body.count)
    }

    @Test
    fun `create task`() {
        val task = Task(title = "Teste123", status = Status.DOING, area = Area.HR_OPS, type = Type.CHANGE)
        val response = http.post<TaskDTO>("/api/tasks", TaskDTO(task))

        assertEquals(response.status, HttpStatus.CREATED_201)
        assertNotNull(response.body.task)
        assertNotNull(response.body.task?.id)
        assertEquals(response.body.task?.title, task.title)
        assertNull(response.body.task?.description)
        assertEquals(response.body.task?.area, task.area)
        assertEquals(response.body.task?.status, task.status)
        assertEquals(response.body.task?.type, task.type)
    }

    @Test
    fun `get single task by id`() {
        val responseTask = this.createTask()
        val id = responseTask.body.task?.id
        val response = http.get<TaskDTO>("/api/tasks/$id")

        assertEquals(response.status, HttpStatus.OK_200)
        assertNotNull(response.body.task)
        assertEquals(id, response.body.task?.id)
    }

    @Test
    fun `update task by id`() {
        val responseTask = this.createTask()
        val id = responseTask.body.task?.id
        val task = Task(
            title = "Title update.", description = "Desc update", status = Status.DOING,
            area = Area.FINANCE_OPS, type = Type.CHANGE
        )
        val response = http.put<TaskDTO>("/api/tasks/$id", TaskDTO(task))

        assertEquals(response.status, HttpStatus.NO_CONTENT_204)
    }

    @Test
    fun `delete task by id`() {
        val responseCreate = this.createTask()
        val response = http.delete("/api/tasks/${responseCreate.body.task?.id}")

        assertEquals(response.status, HttpStatus.NO_CONTENT_204)
    }
}