package com.dndstats.presentation.views

import com.dndstats.domain.CharacterClass
import kotlinx.html.*

fun HTML.characterFormPage(errors: List<String> = emptyList()) {
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
