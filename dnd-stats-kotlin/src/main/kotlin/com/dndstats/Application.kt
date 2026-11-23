package com.dndstats

import com.dndstats.gateways.InMemoryCharacterStore
import com.dndstats.presentation.buildUseCases
import com.dndstats.presentation.configureRouting
import com.dndstats.presentation.configureSerialization
import com.dndstats.presentation.configureStatusPages
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val store = InMemoryCharacterStore()
    val useCases = buildUseCases(store)

    embeddedServer(Netty, port = 3000) {
        configureSerialization()
        configureStatusPages()
        configureRouting(useCases)
    }.start(wait = true)
}
