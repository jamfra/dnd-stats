package com.dndstats.presentation

import com.dndstats.domain.CharacterClass
import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.GetCharacterSheet
import com.dndstats.usecases.ValidationException
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
                renderCharacterForm()
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
                val acceptsJson = call.request.acceptItems().any {
                    it.value.equals("application/json", ignoreCase = true)
                }
                if (acceptsJson) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("errors" to e.errors))
                } else {
                    call.respondHtml(HttpStatusCode.BadRequest) {
                        renderCharacterForm(errors = e.errors)
                    }
                }
            }
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

            val acceptsHtml = call.request.acceptItems().any {
                it.value.contains("text/html")
            }

            if (acceptsHtml) {
                call.respondHtml(HttpStatusCode.OK) {
                    renderCharacterSheet(output.sheet)
                }
            } else {
                call.respond(HttpStatusCode.OK, output.sheet)
            }
        }
    }
}

private fun HTML.renderCharacterForm(errors: List<String> = emptyList()) {
    body {
        h1 { +"Create Character" }

        if (errors.isNotEmpty()) {
            div(classes = "errors") {
                errors.forEach { error ->
                    p(classes = "error") { +error }
                }
            }
        }

        form(action = "/createCharacter", method = FormMethod.post) {
            div {
                label {
                    htmlFor = "name"
                    +"Name: "
                }
                textInput(name = "name") {
                    id = "name"
                    maxLength = "50"
                }
            }

            div {
                label {
                    htmlFor = "species"
                    +"Species: "
                }
                textInput(name = "species") {
                    id = "species"
                    maxLength = "50"
                }
            }

            div {
                label {
                    htmlFor = "characterClass"
                    +"Class: "
                }
                select {
                    id = "characterClass"
                    name = "characterClass"
                    option {
                        value = ""
                        +"Select a class"
                    }
                    CharacterClass.entries.forEach { cls ->
                        option {
                            value = cls.displayName
                            +cls.displayName
                        }
                    }
                }
            }

            div {
                label {
                    htmlFor = "level"
                    +"Level: "
                }
                numberInput(name = "level") {
                    id = "level"
                    min = "1"
                    max = "20"
                    value = "1"
                }
            }

            div {
                h2 { +"Ability Scores" }
                div {
                    label {
                        htmlFor = "strength"
                        +"Strength: "
                    }
                    numberInput(name = "strength") {
                        id = "strength"
                        min = "1"
                        max = "30"
                        value = "10"
                    }
                }
                div {
                    label {
                        htmlFor = "dexterity"
                        +"Dexterity: "
                    }
                    numberInput(name = "dexterity") {
                        id = "dexterity"
                        min = "1"
                        max = "30"
                        value = "10"
                    }
                }
                div {
                    label {
                        htmlFor = "constitution"
                        +"Constitution: "
                    }
                    numberInput(name = "constitution") {
                        id = "constitution"
                        min = "1"
                        max = "30"
                        value = "10"
                    }
                }
                div {
                    label {
                        htmlFor = "intelligence"
                        +"Intelligence: "
                    }
                    numberInput(name = "intelligence") {
                        id = "intelligence"
                        min = "1"
                        max = "30"
                        value = "10"
                    }
                }
                div {
                    label {
                        htmlFor = "wisdom"
                        +"Wisdom: "
                    }
                    numberInput(name = "wisdom") {
                        id = "wisdom"
                        min = "1"
                        max = "30"
                        value = "10"
                    }
                }
                div {
                    label {
                        htmlFor = "charisma"
                        +"Charisma: "
                    }
                    numberInput(name = "charisma") {
                        id = "charisma"
                        min = "1"
                        max = "30"
                        value = "10"
                    }
                }
            }

            submitInput { value = "Submit" }
        }
    }
}

private fun HTML.renderCharacterSheet(sheet: GetCharacterSheet.SheetDto) {
    body {
        h1 { +"Character Sheet" }

        div(classes = "basic-info") {
            h2 { +"Character Info" }
            p { +"Name: ${sheet.name}" }
            p { +"Species: ${sheet.species}" }
            p { +"Class: ${sheet.characterClass}" }
            p { +"Level: ${sheet.level}" }
        }

        div(classes = "vitals") {
            h2 { +"Vitals" }
            val profSign = if (sheet.proficiencyBonus >= 0) "+${sheet.proficiencyBonus}" else "${sheet.proficiencyBonus}"
            p { +"Proficiency bonus: $profSign" }
            p { +"Hit points: ${sheet.hitpoints}" }
            p { +"Armor class: ${sheet.armourClass}" }
        }

        div(classes = "ability-scores") {
            h2 { +"Ability Scores" }
            ul {
                val scoreNames = listOf(
                    "strength" to "Strength",
                    "dexterity" to "Dexterity",
                    "constitution" to "Constitution",
                    "intelligence" to "Intelligence",
                    "wisdom" to "Wisdom",
                    "charisma" to "Charisma",
                )
                scoreNames.forEach { (key, label) ->
                    val stat = sheet.abilityScores[key]
                    if (stat != null) {
                        val modSign = if (stat.modifier >= 0) "+${stat.modifier}" else "${stat.modifier}"
                        li { +"$label: ${stat.score} ($modSign)" }
                    }
                }
            }
        }

        div(classes = "saving-throws") {
            h2 { +"Saving Throws" }
            ul {
                val scoreNames = listOf(
                    "strength" to "Strength",
                    "dexterity" to "Dexterity",
                    "constitution" to "Constitution",
                    "intelligence" to "Intelligence",
                    "wisdom" to "Wisdom",
                    "charisma" to "Charisma",
                )
                scoreNames.forEach { (key, label) ->
                    val stat = sheet.abilityScores[key]
                    if (stat != null) {
                        val saveSign = if (stat.savingThrow >= 0) "+${stat.savingThrow}" else "${stat.savingThrow}"
                        val profStr = if (stat.proficient) " (proficient)" else ""
                        li { +"$label: $saveSign$profStr" }
                    }
                }
            }
        }

        div(classes = "weapons") {
            h2 { +"Weapons" }
            p { +"No weapon equipped" }
        }
    }
}
