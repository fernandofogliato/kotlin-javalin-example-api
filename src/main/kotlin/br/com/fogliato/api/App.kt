package br.com.fogliato.api

import br.com.fogliato.api.config.AppConfig
import org.h2.tools.Server

fun main() {
    Server.createWebServer().start()
    AppConfig().setup().start(7000)
}
