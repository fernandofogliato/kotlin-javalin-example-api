package br.com.fogliato.api

import br.com.fogliato.api.config.AppConfig

fun main() {
    AppConfig().setup().start(7000)
}
