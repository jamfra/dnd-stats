package com.dndstats.presentation

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.html.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<NoSuchElementException> { call, cause ->
            if (call.acceptsHtml()) {
                call.respondHtml(HttpStatusCode.NotFound) {
                    body {
                        h1 { +"Character not found" }
                        p { +(cause.message ?: "Character not found") }
                        p {
                            a(href = "/") { +"Back to character list" }
                        }
                    }
                }
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to (cause.message ?: "Not found")))
            }
        }
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to (cause.message ?: "Bad request")))
        }
    }
}
