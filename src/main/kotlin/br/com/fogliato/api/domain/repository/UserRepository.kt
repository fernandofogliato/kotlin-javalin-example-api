package br.com.fogliato.api.domain.repository

import br.com.fogliato.api.domain.model.task.Area
import br.com.fogliato.api.domain.model.user.Profile
import br.com.fogliato.api.domain.model.user.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

internal object Users: Table() {
    val id: Column<Long> = long("id").autoIncrement()
    val name: Column<String> = varchar("name", 150)
    val email: Column<String> = varchar("email", 150).uniqueIndex()
    val password: Column<String> = varchar("password", 50)
    val profile: Column<Profile> = enumerationByName ("profile", 50, Profile::class)
    val group: Column<Area> = enumerationByName ("group", 50, Area::class)
    val active: Column<Boolean> = bool("active")

    override val primaryKey = PrimaryKey(id)

    fun toDomain(row: ResultRow): User {
        return User(
                id = row[id],
                name = row[name],
                email = row[email],
                password = row[password],
                profile = row[profile],
                group = row[group],
                active = row[active]
        )
    }
}

class UserRepository(private val dataSource: DataSource) {

    init {
        transaction(Database.connect(dataSource)) {
            SchemaUtils.create(Users)
        }
    }

    fun create(user: User): User? {
        val id = transaction {
            Users.insert {
                it[name] = user.name!!
                it[email] = user.email!!
                it[password] = user.password!!
                it[profile] = user.profile!!
                it[group] = user.group!!
                it[active] = true
            } get Users.id
        }
        return findById(id);
    }

    fun findById(id: Long): User? {
        return transaction(Database.connect(dataSource)) {
            val select = Users.select { Users.id eq id }
            select.map { row -> Users.toDomain(row) }.firstOrNull()
        }
    }

    fun findAll(limit: Int, offset: Long): List<User> {
        return transaction(Database.connect(dataSource)) {
            Users.selectAll()
                    .limit(limit, offset)
                    .map { row -> Users.toDomain(row) }
        }
    }

    fun update(id: Long, user: User): User? {
        return transaction(Database.connect(dataSource)) {
            Users.update({ Users.id eq id }) {
                it[name] = user.name!!
                it[email] = user.email!!
                it[password] = user.password!!
                it[profile] = user.profile!!
                it[group] = user.group!!
                it[active] = user.active!!
            }
        }.let {
            findById(id)
        }
    }

    private fun findWithConditional(where: Op<Boolean>, limit: Int, offset: Long): List<User> {
        return transaction(Database.connect(dataSource)) {
            Users.select(where)
                .limit(limit, offset)
                .map { row -> Users.toDomain(row) }
        }
    }

    fun findAllByActive(limit: Int, offset: Long, active: Boolean): List<User> {
        return findWithConditional((Users.active eq active), limit, offset);
    }

    fun findAllByArea(limit: Int, offset: Long, area: Area): List<User> {
        return findWithConditional((Users.group eq area), limit, offset);
    }

    fun findByEmail(email: String): User? {
        return findWithConditional((Users.email eq email), 1, 0).firstOrNull();
    }
}