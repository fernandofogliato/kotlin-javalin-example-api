package br.com.fogliato.api.config

import br.com.fogliato.api.Router
import br.com.fogliato.api.config.ModulesConfig.allModules
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin

import io.javalin.plugin.json.JavalinJackson
import org.koin.core.KoinProperties
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import java.text.SimpleDateFormat

class AppConfig() : KoinComponent {
//    private val authConfig: AuthConfig by inject()
    private val router: Router by inject()

    fun setup(): Javalin {
        StandAloneContext.startKoin(
            allModules,
            KoinProperties(true, true)
        )

        return Javalin.create().also { app ->
            app.config.enableCorsForAllOrigins()
            app.config.enableWebjars()
            app.config.contextPath = getKoin().getProperty("context", "api")

            this.configureJackson()
//            authConfig.configure(app)
            router.register(app)
            ErrorExceptionMapping.register(app)

            app.events { event -> event.serverStopping{ StandAloneContext.stopKoin() }}
        }
    }

    private fun configureJackson() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        JavalinJackson.configure(
            jacksonObjectMapper()
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setDateFormat(dateFormat)
                .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
        )
    }
}
