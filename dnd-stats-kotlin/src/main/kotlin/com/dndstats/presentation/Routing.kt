package com.dndstats.presentation

import com.dndstats.usecases.CreateCharacter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.util.UUID

/**
 * Presentation layer: Ktor routing.
 * Depends on use cases only — no domain types leak into this layer.
 */
fun Application.configureRouting(useCases: UseCases) {
    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK) {
                body {
                    h1 { +"My First Heading" }
                    p { +"My first paragraph." }
                    form(action = "/createCharacter", method = FormMethod.post) {
                        p { +"Please enter character name:" }
                        textInput(name = "name")
                        submitInput { value = "Submit" }
                    }
                }
            }
        }

        post("/createCharacter") {
            val params = call.receiveParameters()
            val name = params["name"].orEmpty()

            val result = useCases.createCharacter.execute(
                CreateCharacter.Input(
                    name = name,
                    species = "",
                    characterClass = "",
                    level = 0,
                    abilityScores = CreateCharacter.Input.AbilityScoreInput(0, 0, 0, 0, 0, 0),
                )
            )

            call.respondRedirect("/character/${result.id}")
        }

        get("/character/{id}") {
            val idParam = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing character id")

            val id = runCatching { UUID.fromString(idParam) }.getOrElse {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid character id")
            }

            val output = useCases.getCharacterSheet.execute(
                com.dndstats.usecases.GetCharacterSheet.Input(id = id)
            )

            call.respond(HttpStatusCode.OK, output.sheet)
        }
    }
}
