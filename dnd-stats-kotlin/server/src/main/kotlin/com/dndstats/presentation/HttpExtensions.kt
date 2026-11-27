package com.dndstats.presentation

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlin.uuid.Uuid

suspend fun ApplicationCall.receiveUuid(paramName: String = "id"): Uuid? {
    val raw = parameters[paramName]
    if (raw == null) {
        respond(HttpStatusCode.BadRequest, "Missing character id")
        return null
    }
    val uuid = runCatching { Uuid.parse(raw) }.getOrNull()
    if (uuid == null) {
        respond(HttpStatusCode.BadRequest, "Invalid character id")
        return null
    }
    return uuid
}

fun ApplicationCall.acceptsHtml(): Boolean =
    request.acceptItems().any { it.value.contains("text/html") }

fun ApplicationCall.acceptsJson(): Boolean =
    request.acceptItems().any { it.value.equals("application/json", ignoreCase = true) }
