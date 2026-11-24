package com.dndstats.presentation

import com.dndstats.presentation.views.characterFormPage
import com.dndstats.presentation.views.characterListPage
import com.dndstats.presentation.views.characterSheetPage
import com.dndstats.presentation.views.deleteConfirmationPage
import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.DeleteCharacter
import com.dndstats.usecases.GetCharacterSheet
import com.dndstats.usecases.ValidationException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(useCases: UseCases) {
    routing {
        get("/") {
            val characters = useCases.listCharacters.execute().characters
            if (call.acceptsJson()) {
                call.respond(HttpStatusCode.OK, characters)
            } else {
                call.respondHtml(HttpStatusCode.OK) {
                    characterListPage(characters)
                }
            }
        }

        get("/createCharacter") {
            call.respondHtml(HttpStatusCode.OK) {
                characterFormPage()
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

            try {
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
            } catch (e: ValidationException) {
                if (call.acceptsJson()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("errors" to e.errors))
                } else {
                    call.respondHtml(HttpStatusCode.BadRequest) {
                        characterFormPage(errors = e.errors)
                    }
                }
            }
        }

        get("/character/{id}") {
            val id = call.receiveUuid() ?: return@get
            val output = useCases.getCharacterSheet.execute(
                GetCharacterSheet.Input(id = id)
            )

            if (call.acceptsHtml()) {
                call.respondHtml(HttpStatusCode.OK) {
                    characterSheetPage(output.sheet)
                }
            } else {
                call.respond(HttpStatusCode.OK, output.sheet)
            }
        }

        get("/character/{id}/delete") {
            val id = call.receiveUuid() ?: return@get
            val output = useCases.getCharacterSheet.execute(GetCharacterSheet.Input(id = id))

            call.respondHtml(HttpStatusCode.OK) {
                deleteConfirmationPage(id, output.sheet.name)
            }
        }

        post("/character/{id}/delete") {
            val id = call.receiveUuid() ?: return@post
            useCases.deleteCharacter.execute(DeleteCharacter.Input(id = id))
            call.respondRedirect("/")
        }
    }
}
