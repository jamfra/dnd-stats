package com.dndstats.presentation.views

import com.dndstats.domain.CharacterClass
import com.dndstats.usecases.GetCharacterSheet
import kotlinx.html.*
import java.util.UUID

fun HTML.characterFormPage(errors: List<String> = emptyList()) {
    body {
        p {
            a(href = "/") { +"Back to character list" }
        }

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

fun HTML.characterEditFormPage(
    characterId: UUID,
    sheet: GetCharacterSheet.SheetDto,
    errors: List<String> = emptyList(),
) {
    body {
        p {
            a(href = "/character/$characterId") { +"Back to character sheet" }
        }

        h1 { +"Edit Character" }

        if (errors.isNotEmpty()) {
            div(classes = "errors") {
                errors.forEach { error ->
                    p(classes = "error") { +error }
                }
            }
        }

        form(action = "/character/$characterId/edit", method = FormMethod.post) {
            div {
                label {
                    htmlFor = "name"
                    +"Name: "
                }
                textInput(name = "name") {
                    id = "name"
                    value = sheet.name
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
                    value = sheet.species
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
                            selected = cls.displayName.equals(sheet.characterClass, ignoreCase = true)
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
                    value = "${sheet.level}"
                }
            }

            div {
                h2 { +"Ability Scores" }
                val scoreInputs = listOf(
                    "strength" to "Strength",
                    "dexterity" to "Dexterity",
                    "constitution" to "Constitution",
                    "intelligence" to "Intelligence",
                    "wisdom" to "Wisdom",
                    "charisma" to "Charisma",
                )
                scoreInputs.forEach { (key, labelText) ->
                    div {
                        label {
                            htmlFor = key
                            +"$labelText: "
                        }
                        numberInput(name = key) {
                            id = key
                            min = "1"
                            max = "30"
                            value = "${sheet.abilityScores[key]?.score ?: 10}"
                        }
                    }
                }
            }

            div {
                label {
                    htmlFor = "armorClassOverride"
                    +"Armor class override: "
                }
                numberInput(name = "armorClassOverride") {
                    id = "armorClassOverride"
                    min = "1"
                    max = "30"
                    sheet.armourClassOverride?.let { value = "$it" }
                }
            }

            submitInput { value = "Save" }
        }
    }
}
