package com.dndstats.presentation

import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.GetCharacterSheet
import com.dndstats.gateways.CharacterStore

/**
 * Wires use cases together.
 * This is the composition root for the application layer.
 * Swap [CharacterStore] implementation here (e.g. a database-backed store).
 */
data class UseCases(
    val createCharacter: CreateCharacter,
    val getCharacterSheet: GetCharacterSheet,
)

fun buildUseCases(store: CharacterStore) = UseCases(
    createCharacter = CreateCharacter(store),
    getCharacterSheet = GetCharacterSheet(store),
)
