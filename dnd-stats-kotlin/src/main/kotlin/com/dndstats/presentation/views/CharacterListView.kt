package com.dndstats.presentation.views

import com.dndstats.usecases.ListCharacters
import kotlinx.html.*

fun HTML.characterListPage(characters: List<ListCharacters.CharacterSummary>) {
    body {
        h1 { +"Characters" }

        p {
            a(href = "/createCharacter") { +"Create character" }
        }

        if (characters.isEmpty()) {
            p(classes = "empty-state") {
                +"No characters yet. "
                a(href = "/createCharacter") { +"Create one" }
            }
        } else {
            table {
                thead {
                    tr {
                        th { +"Name" }
                        th { +"Class" }
                        th { +"Level" }
                        th { +"Actions" }
                    }
                }
                tbody {
                    characters.forEach { character ->
                        tr {
                            td {
                                a(href = "/character/${character.id}") { +character.name }
                            }
                            td { +character.characterClass }
                            td { +"${character.level}" }
                            td {
                                a(href = "/character/${character.id}/delete") { +"Delete" }
                            }
                        }
                    }
                }
            }
        }
    }
}
