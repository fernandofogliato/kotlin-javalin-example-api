package br.com.fogliato.api.domain.repository

import br.com.fogliato.api.domain.model.task.Area
import br.com.fogliato.api.domain.model.task.Status
import br.com.fogliato.api.domain.model.task.Task
import br.com.fogliato.api.domain.model.task.Type
import br.com.fogliato.api.domain.model.user.User

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import javax.sql.DataSource

private object Tasks: Table() {
    val id: Column<Long> = long("id").autoIncrement()
    val title: Column<String> = varchar("title", 150)
    val description: Column<String?> = varchar("description", 1000).nullable()
    val type: Column<Type> = enumerationByName ("type", 50, Type::class)
    val area: Column<Area> = enumerationByName ("area", 50, Area::class)
    val status: Column<Status> = enumerationByName ("status", 50, Status::class)
    val assignee: Column<Long?> = reference("assignee_id", Users.id).nullable()
    val createdAt: Column<LocalDateTime> = datetime("created_at")
    val createdBy: Column<Long?> = reference("created_by", Users.id).nullable()
    val updatedAt: Column<LocalDateTime?> = datetime("updated_at").nullable()

    override val primaryKey = PrimaryKey(id)

    fun toDomain(row: ResultRow, assignee: User?, createdBy: User?): Task {
        println(row)
        return Task(
                id = row[id],
                title = row[title],
                description = row[description],
                type = row[type],
                area = row[area],
                status = row[status],
                assignee = assignee,
                createdBy = createdBy,
                createdAt = row[createdAt],
                updatedAt = row[updatedAt]
        )
    }
}

class TaskRepository(private val dataSource: DataSource) {

    init {
        transaction(Database.connect(dataSource)) {
            SchemaUtils.create(Tasks)
        }
    }

    fun create(task: Task): Task? {
        val id = transaction {
            Tasks.insert {
                it[title] = task.title
                it[type] = task.type
                it[area] = task.area
                it[status] = task.status
                it[assignee] = task.assignee?.id
                it[createdBy] = task.createdBy?.id
                it[createdAt] = LocalDateTime.now()
            } get Tasks.id
        }
        return findById(id)
    }

    fun update(id: Long, task: Task): Task? {
        return transaction(Database.connect(dataSource)) {
            Tasks.update({ Tasks.id eq id }) {
                it[title] = task.title
                it[description] = task.description
                it[type] = task.type
                it[area] = task.area
                it[status] = task.status
                it[updatedAt] = LocalDateTime.now()
            }
        }.let {
            findById(id)
        }
    }

    fun delete(id: Long) {
        transaction(Database.connect(dataSource)) {
            Tasks.deleteWhere { Tasks.id eq id }
        }
    }

    private fun findWithConditional(where: Op<Boolean>, limit: Int, offset: Long): List<Task> {
        val assigneeJoin = Users.alias("ass")
        val createdByJoin = Users.alias("cr")

        return transaction(Database.connect(dataSource)) {
            (Tasks.join(assigneeJoin, JoinType.LEFT, additionalConstraint = { assigneeJoin[Users.id] eq Tasks.assignee })
                  .join(createdByJoin, JoinType.INNER, additionalConstraint = { createdByJoin[Users.id] eq Tasks.createdBy }))
                .select(where)
                .limit(limit, offset)
                .orderBy(Tasks.createdAt, SortOrder.DESC)
                .map { row ->
                    val assignee = row[Tasks.assignee]?.let { Users.select { Users.id eq it }.map { r -> Users.toDomain(r) }.firstOrNull() }
                    val createdBy = row[Tasks.createdBy]?.let { Users.select { Users.id eq it }.map { r -> Users.toDomain(r) }.firstOrNull() }

                    Tasks.toDomain(row, assignee, createdBy) }
        }
    }

    fun findAllByArea(limit: Int, offset: Long, area: Area): List<Task> {
        return findWithConditional((Tasks.area eq area), limit, offset)
    }

    fun findAllByAreaAndStatus(limit: Int, offset: Long, area: Area, status: Status): List<Task> {
        return findWithConditional((Tasks.area eq area) and (Tasks.status eq status), limit, offset)
    }

    fun findById(id: Long): Task? {
        return findWithConditional((Tasks.id eq id), 1, 0).firstOrNull()
    }

    fun findAll(limit: Int, offset: Long): List<Task> {
        return findWithConditional((Tasks.id greater 0L), limit, offset)
    }
}