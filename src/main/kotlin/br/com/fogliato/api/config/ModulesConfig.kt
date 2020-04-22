package br.com.fogliato.api.config

import br.com.fogliato.api.Router
import br.com.fogliato.api.controllers.TaskController
import br.com.fogliato.api.controllers.UserController
import br.com.fogliato.api.domain.repository.TaskRepository
import br.com.fogliato.api.domain.repository.UserRepository
import br.com.fogliato.api.domain.service.TaskService
import br.com.fogliato.api.domain.service.UserService
import br.com.fogliato.api.utils.JwtProvider
import org.koin.dsl.module.module

object ModulesConfig {
    private val configModule = module {
        single { AppConfig() }
        single { JwtProvider() }
        single { AuthConfig(get()) }
        single { DbConfig(getProperty("jdbc.url"), getProperty("db.username"), getProperty("db.password")).getDataSource() }
        single { Router(get(), get()) }
    }

    private val taskModule = module {
        single { TaskController(get()) }
        single { TaskService(get(), get()) }
        single { TaskRepository(get()) }
    }

    private val userModule = module {
        single { UserController(get()) }
        single { UserService(get(), get()) }
        single { UserRepository(get()) }
    }

    internal val allModules = listOf(configModule, userModule, taskModule)
}
