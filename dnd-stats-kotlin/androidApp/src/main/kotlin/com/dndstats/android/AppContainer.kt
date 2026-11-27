package com.dndstats.android

import com.dndstats.gateways.InMemoryCharacterStore
import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.DeleteCharacter
import com.dndstats.usecases.EditCharacter
import com.dndstats.usecases.GetCharacterSheet
import com.dndstats.usecases.ListCharacters

/**
 * Manual dependency container following hexagonal architecture:
 * the presentation layer depends only on shared use cases.
 * Lives at application scope so all ViewModels share the same in-memory store.
 */
class AppContainer {
    private val store = InMemoryCharacterStore()

    val listCharacters = ListCharacters(store)
    val createCharacter = CreateCharacter(store)
    val getCharacterSheet = GetCharacterSheet(store)
    val editCharacter = EditCharacter(store)
    val deleteCharacter = DeleteCharacter(store)
}
