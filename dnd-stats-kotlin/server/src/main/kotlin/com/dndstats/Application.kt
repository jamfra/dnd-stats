package com.dndstats

import com.dndstats.gateways.InMemoryCharacterStore
import com.dndstats.presentation.UseCases
import com.dndstats.presentation.configureRouting
import com.dndstats.presentation.configureSerialization
import com.dndstats.presentation.configureStatusPages
import com.dndstats.usecases.CharacterStore
import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.DeleteCharacter
import com.dndstats.usecases.EditCharacter
import com.dndstats.usecases.GetCharacterSheet
import com.dndstats.usecases.ListCharacters
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun buildUseCases(store: CharacterStore) = UseCases(
    createCharacter = CreateCharacter(store),
    getCharacterSheet = GetCharacterSheet(store),
    listCharacters = ListCharacters(store),
    deleteCharacter = DeleteCharacter(store),
    editCharacter = EditCharacter(store),
)

fun main() {
    val store = InMemoryCharacterStore()
    val useCases = buildUseCases(store)

    embeddedServer(Netty, port = 3000) {
        configureSerialization()
        configureStatusPages()
        configureRouting(useCases)
    }.start(wait = true)
}
