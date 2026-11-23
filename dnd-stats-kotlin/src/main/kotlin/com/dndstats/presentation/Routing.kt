package com.dndstats.presentation

import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.GetCharacterSheet
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.util.UUID

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
            val species = params["species"].orEmpty()
            val characterClass = params["characterClass"] ?: params["class"].orEmpty()
            val level = params["level"]?.toIntOrNull() ?: 0
            val strength = params["strength"]?.toIntOrNull() ?: 0
            val dexterity = params["dexterity"]?.toIntOrNull() ?: 0
            val constitution = params["constitution"]?.toIntOrNull() ?: 0
            val intelligence = params["intelligence"]?.toIntOrNull() ?: 0
            val wisdom = params["wisdom"]?.toIntOrNull() ?: 0
            val charisma = params["charisma"]?.toIntOrNull() ?: 0

            val result = useCases.createCharacter.execute(
                CreateCharacter.Input(
                    name = name,
                    species = species,
                    characterClass = characterClass,
                    level = level,
                    abilityScores = CreateCharacter.Input.AbilityScoreInput(
                        strength = strength,
                        dexterity = dexterity,
                        constitution = constitution,
                        intelligence = intelligence,
                        wisdom = wisdom,
                        charisma = charisma,
                    ),
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
                GetCharacterSheet.Input(id = id)
            )

            call.respond(HttpStatusCode.OK, output.sheet)
        }
    }
}
